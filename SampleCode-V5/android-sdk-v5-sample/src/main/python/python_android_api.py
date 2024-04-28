import time
import Pyro5.server


@Pyro5.server.expose
class PythonAndroidApi:
    def __init__(self, proxy=None):
        if proxy is None:
            from dji.sampleV5.aircraft.models import DroneMover
            self._proxy = DroneMover.getInstance()
        else:
            self._proxy = proxy

    def set_proxy(self, proxy):
        self._proxy=proxy
    def _printAllRemoteMethods(self):
        method_signatures = self._proxy.get_method_signatures()
        for method, signature in method_signatures.items():
            print(f" def {method}{signature}:")

    def getAllIpAddresses(self):
        ipList = self._proxy.getAllIpAddresses()
        return ipList


    def enableVirtualStick(self, timeout=2):
        return self._proxy.enableVirtualStick(timeout)


    def testConnection(self, text:str) -> str:
        x,y = self._proxy.testConnection(text)
        return x,y


    def sendVirtualStickAdvancedParam(self, vertical: float, roll: float=0, pitch: float=0, yaw: float=0,
                                      mode_horizontal_speed:bool=True, mode_vertical_speed: bool=False, mode_coordinate_ground: bool=True, mode_yaw_speed: bool=False, minAltitude:float=20.0):
        if vertical<minAltitude:
            roll,pitch=0.0, 0.0
        return self._proxy.sendVirtualStickAdvancedParam(vertical, roll, pitch, yaw, mode_horizontal_speed, mode_vertical_speed, mode_coordinate_ground, mode_yaw_speed)


    def startTakeOff(self, timeout:int=10):
        return self._proxy.startTakeOff(timeout)


    def enableSimulator(self, timeout:int=10, lat=58.1, lon=11.1, gps_num=17, restart:bool=False):
        if restart:
            self.disableSimulator()
            self.disableSimulator()
            self.disableSimulator()
            time.sleep(2)
        ret = self._proxy.enableSimulator(timeout, lat, lon, gps_num)
        ret = self._proxy.enableSimulator(timeout, lat, lon, gps_num)
        time.sleep(1)
        return ret

    def disableSimulator(self, timeout:int=10):
        ret =  self._proxy.disableSimulator(timeout)
        time.sleep(1)
        return ret

    def getLastLocation(self):
        return self._proxy.getLastLocation()

    def getAltitude(self) -> float:
        return self._proxy.getAltitude()

    def getUltrasonicHeight(self) -> float:
        return self._proxy.getUltrasonicHeight()/10.0



    def setVirtualStickAdvancedModeEnabled(self, enable:bool=True):
        return self._proxy.setVirtualStickAdvancedModeEnabled(enable)


    def getAircraftLocation3D(self):
        try:
            lat,lon,alt = self._proxy.getAircraftLocation3D()
            return lat,lon,alt
        except Exception as e:
            print(e)
            return -99.0,-99.0,-99.0

    def getAircraftSpeed(self) -> (float,float,float):
        try:
            x,y,z=self._proxy.getAircraftSpeed()
            return x,y,z
        except:
            return -99.0, -99.0, -99.0
    def getFlightMode(self):
        return self._proxy.getFlightMode()

    def getIsMotorOn(self):
        return self._proxy.getIsMotorOn()

    def getIsFlying(self):
        return self._proxy.getIsFlying()

    def getRcSticks(self) -> (int,int,int,int):
        try:
            lx,ly,rx,ry = self._proxy.getRcSticks()
            return lx,ly,rx,ry
        except:
            return 0,0,0,0

    def getHomeLocation(self):
        lat,lon,alt=self._proxy.getHomeLocation()
        return lat,lon,alt



    def native_SendData(self):
        return self._proxy.native_SendData()

    def setGimbalPitch(self, pitch, duration:float=0):
        return self._proxy.setGimbalPitch(pitch, duration)
    def setGimbalYaw(self, yaw, duration:float=0):
        return self._proxy.setGimbalYaw(yaw, duration)
    def setGimbalAttitude(self, pitch, yaw, roll,
                          pitchIgnored, yawIgnored, rollIgnored,
                          duration, absoluteAngle):
        return self._proxy.setGimbalAttitude(pitch, yaw, roll,
                                             pitchIgnored, yawIgnored, rollIgnored,
                                             duration, absoluteAngle)

    def getDroneType(self):
        return self._proxy.getDroneType()

    def setCoordinatedTurnEnabled(self, enable):
        return self._proxy.setCoordinatedTurnEnabled(enable)

    def setTiltInAttiNormal(self, angle):
        return self._proxy.setTiltInAttiNormal(angle)


    def setKeyLEDsSettings(self, frontLEDsOn, statusIndicatorLEDsOn, rearLEDsOn, navigationLEDsOn):
        return self._proxy.setKeyLEDsSettings(frontLEDsOn, statusIndicatorLEDsOn, rearLEDsOn, navigationLEDsOn)

    def setHomeLocation(self, lat, lon):
        return self._proxy.setHomeLocation(lat,lon)

    def startStreamingFpv(self):
        return self._proxy.startStreamingFpv()

    def startStreamingUdp(self, ip,port):
        return self._proxy.startStreamingUdp(ip,port)

    def startRecord(self):
        return self._proxy.startRecord()
    def stopRecord(self):
        return self._proxy.stopRecord()

    def startShootPhoto(self):
        return self._proxy.startShootPhoto()

    def stopShootPhoto(self):
        return self._proxy.stopShootPhoto()

    def getExternalCacheDirPath(self):
        return self._proxy.getExternalCacheDirPath()

    def getAircraftName(self):
        return self._proxy.getAircraftName()

    def setAircraftName(self, name:str):
        return self._proxy.setAircraftName(name)

    def getRcAndroidGps(self):
        lat,lon,alt,speed,bearing,time,rawTime,fullBiasNanos,age = self._proxy.getRcAndroidGps()
        return lat,lon,alt,speed,bearing,time,rawTime,fullBiasNanos,age

    def getChargeRemainingInPercent(self):
        return self._proxy.getChargeRemainingInPercent()

    def startGoHome(self):
        return self._proxy.startGoHome()
    def formatStorageSD(self):
        return self._proxy.formatStorageSD()

    def updateHtmlNoTouch(self, html:str):
        self._proxy.updateHtmlNoTouch(html)
