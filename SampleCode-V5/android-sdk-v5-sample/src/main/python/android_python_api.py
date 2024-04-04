import Pyro5.server
import threading
from dji.sampleV5.aircraft.models import DroneMover
import Pyro5.api
import inspect
import util
import logging
import numpy as np
@Pyro5.server.expose
class AndroidPythonApi:
    def _main(self):
        self.daemon = Pyro5.server.Daemon(host="0.0.0.0", port=self._port)  # Create a Pyro daemon
        self.uri = self.daemon.register(self, objectId=self._serverName)
        logging.info(f"Started:{self.uri}")  # Print the object uri so the client can use it
        self.daemon.requestLoop()  # Start the event loop of the server to wait for calls

    def start_server(self, serverName:str="apiServer", port:int=9000):
        self._serverName=serverName
        self._port=port
        daemon_thread = threading.Thread(target=self._main, daemon=True)
        daemon_thread.start()
        return f"Started:{self._serverName}"

    def shutdown_server(self):
        self.daemon.shutdown()

    def testConnection(self, arg):
        logging.info(f"testConnection({arg})")
        s1,s2=DroneMover.getInstance().testConnection(arg)
        return s1,s2


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
    def setGimbalPitch(self, pitch, duration):
        return DroneMover.getInstance().setGimbalPitch(pitch, duration)
    @util.trace_function_call_and_return
    def setGimbalYaw(self, yaw, duration):
        return DroneMover.getInstance().setGimbalYaw(yaw, duration)

    @util.trace_function_call_and_return
    def setGimbalMode(self, free):
        return DroneMover.getInstance().setGimbalMode(free)

    @util.trace_function_call_and_return
    def setGimbalAttitude(self, pitch, yaw, roll,
                          pitchIgnored, yawIgnored, rollIgnored,
                          duration, absoluteAngle):
        return DroneMover.getInstance().setGimbalAttitude(pitch, yaw, roll,
                                                     pitchIgnored, yawIgnored, rollIgnored,
                                                     duration, absoluteAngle)


    @util.trace_function_call_and_return
    def getRcSticks(self):
        try:
            lh,lv,rh,rv = DroneMover.getInstance().getRcSticks()
            return lh,lv,rh,rv
        except:
            return 0,0,0,0
    @util.trace_function_call_and_return
    def getHomeLocation(self):
        try:
            lat,lon,alt=DroneMover.getInstance().getHomeLocation()
            return lat,lon,alt
        except:
            return -99,-99,-99

    @util.trace_function_call_and_return
    def getDroneType(self):
        return DroneMover.getInstance().getDroneType()

    @util.trace_function_call_and_return
    def setCoordinatedTurnEnabled(self, enable):
        return DroneMover.getInstance().setCoordinatedTurnEnabled(enable)

    @util.trace_function_call_and_return
    def setKeyLEDsSettings(self, frontLEDsOn, statusIndicatorLEDsOn, rearLEDsOn, navigationLEDsOn):
        return DroneMover.getInstance().setTiltInAttiNormal(frontLEDsOn, statusIndicatorLEDsOn, rearLEDsOn, navigationLEDsOn)


    @util.trace_function_call_and_return
    def setHorizMaxSpeedInNormal(self, speed):
        return DroneMover.getInstance().setHorizMaxSpeedInNormal(speed)

    @util.trace_function_call_and_return
    def setHomeLocation(self, lat, lon):
        return DroneMover.getInstance().setHomeLocation(lat,lon)

    @util.trace_function_call_and_return
    def setHomeLocation(self, lat, lon):
        return DroneMover.getInstance().setHomeLocation(lat,lon)
    @util.trace_function_call_and_return
    def setHomeLocation(self, lat, lon):
        return DroneMover.getInstance().setHomeLocation(lat,lon)
    @util.trace_function_call_and_return
    def setHomeLocation(self, lat, lon):
        return DroneMover.getInstance().setHomeLocation(lat,lon)
    @util.trace_function_call_and_return
    def setHomeLocation(self, lat, lon):
        return DroneMover.getInstance().setHomeLocation(lat,lon)

    def startStreamingFpv(self):
        return DroneMover.getInstance().startStreamingFpv()
    def startStreamingUdp(self, ip, port):
        return DroneMover.getInstance().startStreamingUdp(ip, port)

    def getFpvFrameFilePath(self):
        return DroneMover.getInstance().getFpvFrameFilePath()

    @util.trace_function_call_and_return
    def startRecord(self):
        return DroneMover.getInstance().startRecord()
    def stopRecord(self):
        return DroneMover.getInstance().stopRecord()

    def startShootPhoto(self):
        return DroneMover.getInstance().startShootPhoto()

    def stopShootPhoto(self):
        return DroneMover.getInstance().stopShootPhoto()
    def getExternalCacheDirPath(self):
        return DroneMover.getInstance().getExternalCacheDirPath()
    def getAircraftName(self):
        return DroneMover.getInstance().getAircraftName()
    def setAircraftName(self, name):
        return DroneMover.getInstance().setAircraftName(name)
    def getRcAndroidGps(self):
        lat,lon,alt,speed,bearing,time,rawTime,fullBiasNanos,age = DroneMover.getInstance().getRcAndroidGps()
        return lat,lon,alt,speed,bearing,time,rawTime,fullBiasNanos,age


