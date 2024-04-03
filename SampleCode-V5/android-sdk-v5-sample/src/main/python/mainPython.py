#import android_python_api
import admin_server
import util
import logging
import traceback

def main_python():
    print("start:main_python()")
    util.setup_logging()
    logging.info("Logs init")
    admin_server.AdminServer().start_server(serverName="AdminServer", port=9999)
    import flaskServer
    from dji.sampleV5.aircraft.models import DroneMover
    path=DroneMover.getInstance().getExternalCacheDirPath()
    flaskServer.startServer(path, port=9797)

    try:
        import android_python_api
        android_python_api.AndroidPythonApi().start_server(serverName="apiServer", port=9000)
    except Exception as e:
        logging.exception("An exception occurred at start")
    logging.info("end:main_python()")
    return 0
