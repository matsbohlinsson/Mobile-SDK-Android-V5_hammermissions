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
    def disableSimulator(self, timeout):
        return DroneMover.getInstance().disableSimulator(timeout)

    @util.trace_function_call_and_return
    def setVirtualStickAdvancedModeEnabled(self, enable):
        DroneMover.getInstance().setVirtualStickAdvancedModeEnabled(enable)
        return "OK"

    @util.trace_function_call_and_return
    def native_SendData(self):
        return DroneMover.getInstance().native_SendData()


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

    @util.trace_function_call_and_return
    def getAircraftLocation3D(self):
        loc = DroneMover.getInstance().getAircraftLocation3D()
        if loc is None:
            return -99,-99,-99
        return loc.getLatitude(), loc.getLongitude(), loc.getAltitude()
    @util.trace_function_call_and_return
    def getAircraftSpeed(self):
        speed = DroneMover.getInstance().getAircraftSpeed()
        return speed.getX(), speed.getY(), speed.getZ()
    @util.trace_function_call_and_return
    def getAltitude(self):
        return DroneMover.getInstance().getAltitude()
    @util.trace_function_call_and_return
    def getUltrasonicHeight(self):
        return DroneMover.getInstance().getUltrasonicHeight()
    @util.trace_function_call_and_return
    def getFlightMode(self):
        return DroneMover.getInstance().getFlightMode().toString()
    @util.trace_function_call_and_return
    def getIsMotorOn(self):
        return DroneMover.getInstance().getIsMotorOn()
    @util.trace_function_call_and_return
    def getIsFlying(self):
        return DroneMover.getInstance().getIsFlying()
    @util.trace_function_call_and_return
    def startMotor(self, timeout):
        return DroneMover.getInstance().startMotor(timeout)
    @util.trace_function_call_and_return
    def getRcSticks(self):
        return  DroneMover.getInstance().getStickLeftHorizontal(),DroneMover.getInstance().getStickLeftVertical(),DroneMover.getInstance().getStickRightHorizontal(),DroneMover.getInstance().getStickRightVertical()

    @util.trace_function_call_and_return
    def getHomeLocation(self):
        lat,lon=-99.0,-99.0
        takeoffLocationAltitude=-99
        loc=DroneMover.getInstance().getHomeLocation()
        if loc is None:
            lat,lon=-99.0,-99.0
        try:
            takeoffLocationAltitude=DroneMover.getInstance().getTakeoffLocationAltitude()
        except:
            takeoffLocationAltitude=-99.0
        return lat,lon, takeoffLocationAltitude

