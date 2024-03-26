package dji.sampleV5.aircraft.models

import com.dji.wpmzsdk.common.utils.kml.model.LocationCoordinate3D
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.RemoteControllerKey
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.sdk.keyvalue.value.common.Velocity3D
import dji.sdk.keyvalue.value.flightcontroller.FlightMode
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.et.action
import dji.v5.et.create
import dji.v5.et.get

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