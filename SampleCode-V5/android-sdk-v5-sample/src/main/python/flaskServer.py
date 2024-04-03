import http.server
import socketserver
import threading

def startServer(directory, port):
    class Handler(http.server.SimpleHTTPRequestHandler):
        def __init__(self, *args, **kwargs):
            super().__init__(*args, directory=directory, **kwargs)

    def server_thread():
        with socketserver.TCPServer(("0.0.0.0", port), Handler) as httpd:
            print(f"Serving at port {port}")
            httpd.serve_forever()

    thread = threading.Thread(target=server_thread)
    thread.daemon = True  # This ensures the thread will exit when the main program does
    thread.start()
    #server_thread()
    return thread
