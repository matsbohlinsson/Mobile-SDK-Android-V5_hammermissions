package dji.sampleV5.aircraft.models

import android.util.Log
import com.dji.wpmzsdk.common.utils.kml.model.Location2D
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.RemoteControllerKey
import dji.sdk.keyvalue.key.OcuSyncKey
import dji.sdk.keyvalue.key.AirLinkKey
import dji.sdk.keyvalue.key.BatteryKey
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.sdk.keyvalue.value.common.Velocity3D
import dji.sdk.keyvalue.value.flightcontroller.FlightMode
import dji.sdk.keyvalue.key.GimbalKey
import dji.sdk.keyvalue.key.co_v.KeyRotateBySpeed
import dji.sdk.keyvalue.value.common.LocationCoordinate2D
import dji.sdk.keyvalue.value.common.LocationCoordinate3D
import dji.sdk.keyvalue.value.flightcontroller.DroneType
import dji.sdk.keyvalue.value.flightcontroller.FlyToOperationType
import dji.sdk.keyvalue.value.flightcontroller.FlyToPointInfo
import dji.sdk.keyvalue.value.flightcontroller.LEDsSettings
import dji.sdk.keyvalue.value.gimbal.GimbalAngleRotation
import dji.sdk.keyvalue.value.gimbal.GimbalAngleRotationMode
import dji.sdk.keyvalue.value.gimbal.GimbalMode
import dji.sdk.keyvalue.value.gimbal.GimbalSpeedRotation
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.et.action
import dji.v5.et.create
import dji.v5.et.get
import dji.v5.et.set

class BasicAircraftControlVM : DJIViewModel() {

    fun startTakeOff(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        dji.v5.utils.common.LocationUtil.getLastLocation()
        FlightControllerKey.KeyStartTakeoff.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })
        Log.d("DroneType", DroneType.DJI_MINI_2.value().toString());
    }

    fun startLanding(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyStartAutoLanding.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })
    }
    fun setHomepoint(loc: Location2D, callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyHomeLocationUsingCurrentRemoteControllerLocation.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })
    }

    fun startMotor(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyTurnOnTheMotor.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })
    }


    fun setGimbalMode(free: Boolean) {
        if (free)
            GimbalKey.KeyGimbalMode.create().set(GimbalMode.FREE, {}, {})
        else
            GimbalKey.KeyGimbalMode.create().set(GimbalMode.YAW_FOLLOW, {}, {})
    }

    fun setGimbalAttitude(pitch: Double, yaw: Double, roll: Double,
                           pitchIgnored: Boolean, yawIgnored: Boolean, rollIgnored: Boolean,
                           duration: Double,
                           absoluteAngle:Boolean
                           ) {
        val rotation = GimbalAngleRotation();
        if (absoluteAngle)
            rotation.mode = GimbalAngleRotationMode.ABSOLUTE_ANGLE
        else
            rotation.mode = GimbalAngleRotationMode.RELATIVE_ANGLE
        rotation.pitch = pitch
        rotation.yaw = yaw
        rotation.roll = roll
        rotation.pitchIgnored = pitchIgnored
        rotation.yawIgnored = yawIgnored
        rotation.rollIgnored = rollIgnored
        rotation.duration = duration
        GimbalKey.KeyRotateByAngle.create().action(rotation, {}, { e: IDJIError -> })
    }
    
    fun getAircraftLocation3D() = FlightControllerKey.KeyAircraftLocation3D.create().get();

    fun getAltitude() = FlightControllerKey.KeyAltitude.create().get();
    fun getUltrasonicHeight() = FlightControllerKey.KeyUltrasonicHeight.create().get();
    fun getStickLeftHorizontal() = RemoteControllerKey.KeyStickLeftHorizontal.create().get();
    fun getStickLeftVertical() = RemoteControllerKey.KeyStickLeftVertical.create().get();
    fun getStickRightHorizontal() = RemoteControllerKey.KeyStickRightHorizontal.create().get();
    fun getStickRightVertical() = RemoteControllerKey.KeyStickRightVertical.create().get();


    fun getAircraftSpeed() = FlightControllerKey.KeyAircraftVelocity.create().get( Velocity3D(0.0, 0.0, 0.0))
    fun getHomeLocation() = FlightControllerKey.KeyHomeLocation.create().get();
    fun getTakeoffLocationAltitude() = FlightControllerKey.KeyTakeoffLocationAltitude.create().get(0.0);

    fun getIsMotorOn() = FlightControllerKey.KeyAreMotorsOn.create().get(false);
    fun getIsFlying() = FlightControllerKey.KeyIsFlying.create().get(false);
    fun getFlightMode() = FlightControllerKey.KeyFlightMode.create().get();
    fun getDroneType(): String {
        return FlightControllerKey.KeyDroneType.create().get().toString()
    }
    fun setCoordinatedTurnEnabled(enable: Boolean) = FlightControllerKey.KeyCoordinatedTurnEnabled.create().set(false,{}, { e: IDJIError -> Log.e("QQ", e.description())})
    fun getGroundStationModeEnabled(): Boolean? {
        return FlightControllerKey.KeyGroundStationModeEnabled.create().get()
    }
    fun getVirtualStickControlModeEnabled(): Boolean? {
        return FlightControllerKey.KeyVirtualStickControlModeEnabled.create().get()
    }
    fun setKeyLEDsSettings( frontLEDsOn:Boolean, statusIndicatorLEDsOn:Boolean, rearLEDsOn:Boolean, navigationLEDsOn:Boolean) = FlightControllerKey.KeyLEDsSettings.create().
    set(LEDsSettings(frontLEDsOn, statusIndicatorLEDsOn, rearLEDsOn, navigationLEDsOn))
    fun setHomeLocation(lat :Double, lon: Double) {
        FlightControllerKey.KeyHomeLocation.create().set(LocationCoordinate2D(lat, lon))
    }

    //fun getProto4() = FlightControllerKey.FlyToPointInfo.create().set(FlyToPointInfo().point)
//KeyCoordinatedTurnEnabled
    //KeyTiltInAttiNormal
    //FlyToPointInfo
    // KeyFlightControlAdvancedParameterRange
}