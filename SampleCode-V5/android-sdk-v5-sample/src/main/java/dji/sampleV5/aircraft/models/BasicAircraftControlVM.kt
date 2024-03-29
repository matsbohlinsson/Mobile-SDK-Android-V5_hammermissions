package dji.sampleV5.aircraft.models

import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.RemoteControllerKey
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.sdk.keyvalue.value.common.Velocity3D
import dji.sdk.keyvalue.value.flightcontroller.FlightMode
import dji.sdk.keyvalue.key.GimbalKey
import dji.sdk.keyvalue.key.co_v.KeyRotateBySpeed
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
    }

    fun startLanding(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyStartAutoLanding.create().action({
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

    fun setGimbalPitch(pitch: Double, duration: Double) {
        val rot = GimbalAngleRotation();
        rot.mode = GimbalAngleRotationMode.ABSOLUTE_ANGLE
        rot.pitch = pitch
        rot.duration = duration
        rot.yawIgnored=true
        rot.rollIgnored=true
        GimbalKey.KeyRotateByAngle.create().action(rot, {}, { e: IDJIError -> })
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

    fun setGimbalYaw(yaw: Double, duration: Double) {
        val rot = GimbalAngleRotation();
        rot.mode = GimbalAngleRotationMode.ABSOLUTE_ANGLE
        rot.yaw = yaw
        rot.duration = duration
        rot.pitchIgnored=true
        rot.rollIgnored=true
        GimbalKey.KeyRotateByAngle.create().action(rot, {}, { e: IDJIError -> })
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
    fun getFlightMode() = FlightControllerKey.KeyFlightMode.create().get(FlightMode.ATTI);

}