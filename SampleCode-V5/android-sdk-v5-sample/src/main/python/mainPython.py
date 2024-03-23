import android_python_api
import util
import logging

def main_python():
    print("start:main_python()")
    util.setup_logging()
    logging.info("Logs init")
    android_python_api.AndroidPythonApi()._start()
    logging.info("end:main_python()")
    return 0
