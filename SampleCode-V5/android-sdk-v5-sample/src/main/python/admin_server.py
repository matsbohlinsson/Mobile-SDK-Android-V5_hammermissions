import threading
import logging
import Pyro5.api
import importlib
import sys
from pathlib import Path


# Define the remote object class
@Pyro5.api.expose
class AdminServer:

    def __init__(self):
        self.api_server=None

    def write_and_import_module(self, data, file_path):
        """
        Writes data to a file and dynamically imports the file as a Python module.
        If the module is already loaded, it is reloaded to reflect the latest changes.

        Parameters:
        - data: str, the string to write to the file.
        - file_path: str, the path to the file where the data should be written.

        Returns:
        The imported module object.
        """
        # Convert the file path to a Path object for easier manipulation
        file_path = Path(file_path)

        # Ensure the containing directory exists
        file_path.parent.mkdir(parents=True, exist_ok=True)

        # Write the string data to the file
        with open(file_path, 'w') as file:
            file.write(data)

        # Derive the module name from the file path
        module_name = file_path.stem

        # Check if the module is already loaded
        if module_name in sys.modules:
            # Remove the loaded module to force a re-import
            del sys.modules[module_name]

        # Dynamically import the module
        module = importlib.import_module(module_name)
        return 0
    def call_function_in_module(self, module_name, function_name, *args, **kwargs):
        if module_name not in sys.modules:
            raise ImportError(f"Module '{module_name}' not found. Please load it first.")

        module = sys.modules[module_name]
        if not hasattr(module, function_name):
            raise AttributeError(f"Function '{function_name}' not found in module '{module_name}'.")

        function = getattr(module, function_name)
        return function(*args, **kwargs)


    # Start the Pyro5 server
    def _main(self):
        logging.info(f"Ping1")
        daemon = Pyro5.api.Daemon(host="0.0.0.0", port=self._port)  # Listen on a random port
        uri = daemon.register(self, self._serverName)
        logging.info(f"Ping2")
        logging.info(f"Started server:{uri}")
        daemon.requestLoop()

    def start_server(self, serverName:str, port:int):
        logging.info("start_server")

        self._port=port
        self._serverName=serverName
        daemon_thread = threading.Thread(target=self._main, daemon=True)
        daemon_thread.start()
        return daemon_thread

    def runPythonCode(self, pythonCode:str):
        self.return_value=None
        exec(pythonCode)
        try: return self.return_value
        except: pass
        return None

    def start_api_server(self, serverName:str="AndroidPythonDroneApi", port:int=9090):
        import android_python_api
        if self.api_server:
            return "Already started"
        self.api_server = android_python_api.AndroidPythonApi()
        return self.api_server.start_server(serverName=serverName, port=port)
    def shutdown_api_server(self):
        '''
        Doesnt work. We get a new instance everytime.
        Do a baseclass and inherit
        :return:
        '''
        self.api_server.shutdown_server()
        self.api_server=None

if __name__ == "__main__":
    daemon_thread=AdminServer().start_server(serverName="AdminServer", port=9999)
    daemon_thread.join()
