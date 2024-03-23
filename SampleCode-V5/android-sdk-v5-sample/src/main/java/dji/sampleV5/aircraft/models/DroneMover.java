package dji.sampleV5.aircraft.models;

import dji.sdk.keyvalue.key.FlightControllerKey;
import dji.sdk.keyvalue.key.RemoteControllerKey;
import dji.sdk.keyvalue.value.flightcontroller.*;
import dji.v5.common.callback.CommonCallbacks;

import dji.v5.common.error.IDJIError;
import dji.raw.jni.JNIRawData.*;
import dji.v5.manager.aircraft.virtualstick.VirtualStickManager;
import dji.v5.manager.aircraft.virtualstick.VirtualStickState;
import dji.v5.manager.aircraft.virtualstick.VirtualStickStateListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import dji.sdk.keyvalue.key.FlightControllerKey;
import dji.sdk.keyvalue.value.common.EmptyMsg;
import dji.v5.common.callback.CommonCallbacks;
import dji.v5.common.error.IDJIError;

public class DroneMover {
    static DroneMover droneMover = null;
    float _pitch = 0;
    float _roll = 0;
    float _yaw = 179;
    float desiredFlyingHeight = 20;

    public static DroneMover getInstance() {
        if (droneMover == null) {
            droneMover = new DroneMover();
        }
        //dji.raw.jni.JNIRawData.native_SendData()
        return droneMover;
    }

    public void startTakeOff() {
    }

    public static String enableVirtualStick(int timeout) {
            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicReference<String> completionMessage = new AtomicReference<>("enableVirtualStick timed out; no response");

            VirtualStickManager.getInstance().enableVirtualStick(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onSuccess() {
                    completionMessage.set("enableVirtualStick success.");
                    latch.countDown();
                }

                @Override
                public void onFailure(IDJIError error) {
                    completionMessage.set("enableVirtualStick error: " + error.toString());
                    latch.countDown();
                }
            });

            try {
                // Wait for the countdown to reach zero (i.e., for the callback to be called) or for 1 second to pass
                boolean completedBeforeTimeout = latch.await(timeout, TimeUnit.SECONDS);
                if (!completedBeforeTimeout) {
                    // If the callback did not complete before the timeout, you can handle it accordingly here
                    // Note: The default message is already set for the timeout scenario.
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                return "Thread was interrupted.";
            }

            return completionMessage.get();
        }


    public static String disableVirtualStick(int timeout) {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> completionMessage = new AtomicReference<>("enableVirtualStick timed out; no response");

        VirtualStickManager.getInstance().disableVirtualStick(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onSuccess() {
                completionMessage.set("disableVirtualStick success.");
                latch.countDown();
            }

            @Override
            public void onFailure(IDJIError error) {
                completionMessage.set("enableVirtualStick error: " + error.toString());
                latch.countDown();
            }
        });

        try {
            // Wait for the countdown to reach zero (i.e., for the callback to be called) or for 1 second to pass
            boolean completedBeforeTimeout = latch.await(timeout, TimeUnit.SECONDS);
            if (!completedBeforeTimeout) {
                // If the callback did not complete before the timeout, you can handle it accordingly here
                // Note: The default message is already set for the timeout scenario.
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            return "Thread was interrupted.";
        }

        return completionMessage.get();
    }

    // course: float=None, speed: float=0, height: float=None, heading: float=None, lowest_flying_altitude: float=20, duration=1.5
    // course: float=None, speed: float=0, height: float=None, heading: float=None, lowest_flying_altitude: float=20, duration=1.5
    // throttle=float(height), roll=float(_pitch), pitch=float(_roll), yaw=float(heading), duration=duration
    public static void sendVirtualStickAdvancedParam(double altitude, double roll, double pitch, double yaw, double heading) {
        VirtualStickFlightControlParam controlParam=new VirtualStickFlightControlParam();
        controlParam.setYawControlMode(YawControlMode.ANGLE);
        controlParam.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
        controlParam.setVerticalControlMode(VerticalControlMode.POSITION);
        controlParam.setRollPitchCoordinateSystem(FlightCoordinateSystem.GROUND);

        controlParam.setVerticalThrottle(altitude);
        controlParam.setYaw(yaw);
        controlParam.setRoll(roll);
        controlParam.setPitch(yaw);
        VirtualStickManager.getInstance().sendVirtualStickAdvancedParam(controlParam);
    }


}




