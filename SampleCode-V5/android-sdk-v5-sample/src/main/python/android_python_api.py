import Pyro5.server
import time
import threading

from dji.v5.manager.aircraft.virtualstick import VirtualStickManager
from dji.v5.manager.aircraft.virtualstick import VirtualStickState
from dji.v5.manager.aircraft.virtualstick import VirtualStickStateListener
from dji.v5.common.error import IDJIError;
from dji.v5.common.callback import CommonCallbacks
from  dji.sdk.keyvalue.value.flightcontroller import VirtualStickFlightControlParam
from dji.sampleV5.aircraft.models import DroneMover
import inspect
import Pyro5.api
import inspect
import util
import logging
@Pyro5.server.expose
class AndroidPythonApi:
    def _main(self, port=9090):
        daemon = Pyro5.server.Daemon(host="0.0.0.0", port=port)  # Create a Pyro daemon
        uri = daemon.register(self, objectId="AndroidPythonDroneApi")
        logging.info(f"Ready. Object uri = {uri}")  # Print the object uri so the client can use it
        daemon.requestLoop()  # Start the event loop of the server to wait for calls

    def _start(self):
        daemon_thread = threading.Thread(target=self._main, daemon=True)
        daemon_thread.start()

    def testConnection(self, arg):
        logging.info(f"testConnection({arg})")
        return f"hello from android api:{arg}"

    def get_method_signatures(self):
        methods = [attr for attr in dir(self) if callable(getattr(self, attr)) and not attr.startswith("_")]
        signatures = {}
        for method in methods:
            signatures[method] = str(inspect.signature(getattr(self, method)))
        return signatures

    def enableVirtualStick(self, timeout:int=2):
        return str(DroneMover.getInstance().enableVirtualStick(timeout))

    @util.trace_function_call_and_return
    def startTakeOff(self, timeout:int=1):
        return DroneMover.getInstance().startTakeOff(timeout)

    @util.trace_function_call_and_return
    def sendVirtualStickAdvancedParam(self, vertical:float, roll:float, pitch:float, yaw:float, mode_horizontal_speed:bool, mode_vertical_speed:bool, mode_coordinate_ground:bool, mode_yaw_speed:bool):
        DroneMover.getInstance().sendVirtualStickAdvancedParam(vertical, roll, pitch, yaw, mode_horizontal_speed, mode_vertical_speed, mode_coordinate_ground, mode_yaw_speed)

    def getAllIpAddresses(self):
        ip=DroneMover.getInstance().getAllIpAddresses()
        ipList=[]
        for i in ip.toArray():
            ipList.append(str(i))
        logging.info(f'{ipList=}')
        return ipList

    @util.trace_function_call_and_return
    def enableSimulator(self, timeout, lat, lon, gps_num):
        return DroneMover.getInstance().enableSimulator(timeout, lat, lon, gps_num)

    @util.trace_function_call_and_return
    def setVirtualStickAdvancedModeEnabled(self, enable):
        DroneMover.getInstance().setVirtualStickAdvancedModeEnabled(enable)
        return "OK"

    @util.trace_function_call_and_return
    def getLastLocation(self):
        loc = DroneMover.getInstance().getLastLocation()
        if loc is None:
            return -1,-1,-1,-1
        lat = loc.getLatitude()
        lon = loc.getLongitude()
        speed = loc.getSpeed()
        bearing = loc.getBearing()
        return lat,lon,speed,bearing