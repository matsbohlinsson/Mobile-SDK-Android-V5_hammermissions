package dji.sampleV5.aircraft.models;

import static dji.raw.jni.JNIRawData.native_RegisterObserver;
import static dji.v5.common.utils.CallbackUtils.onSuccess;

import android.location.Location;
import android.util.Log;

import dji.raw.jni.callback.Listener;
import dji.sdk.keyvalue.key.FlightControllerKey;
import dji.sdk.keyvalue.key.RemoteControllerKey;
import dji.sdk.keyvalue.value.common.LocationCoordinate3D;
import dji.sdk.keyvalue.value.common.Velocity3D;
import dji.sdk.keyvalue.value.flightcontroller.*;
import dji.v5.common.callback.CommonCallbacks;

import dji.v5.common.error.IDJIError;
import dji.raw.jni.JNIRawData.*;
import dji.v5.manager.aircraft.simulator.InitializationSettings;
import dji.v5.manager.aircraft.virtualstick.VirtualStickManager;
import dji.v5.manager.aircraft.virtualstick.VirtualStickState;
import dji.v5.manager.aircraft.virtualstick.VirtualStickStateListener;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import dji.sdk.keyvalue.key.FlightControllerKey;
import dji.sdk.keyvalue.value.common.EmptyMsg;
import dji.v5.common.callback.CommonCallbacks;
import dji.v5.common.error.IDJIError;
import dji.sampleV5.aircraft.models.BasicAircraftControlVM;
import dji.sampleV5.aircraft.models.SimulatorVM;
import dji.sdk.keyvalue.value.common.LocationCoordinate2D;

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
        return droneMover;
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
    public static String sendVirtualStickAdvancedParam(double vertical, double roll, double pitch, double yaw, boolean mode_horizontal_speed, boolean mode_vertical_speed, boolean mode_coordinate_ground, boolean mode_yaw_speed) {
        VirtualStickFlightControlParam controlParam=new VirtualStickFlightControlParam();
        if (mode_vertical_speed)
            controlParam.setVerticalControlMode(VerticalControlMode.VELOCITY);
        else
            controlParam.setVerticalControlMode(VerticalControlMode.POSITION);

        if (mode_horizontal_speed)
            controlParam.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
        else
            controlParam.setRollPitchControlMode(RollPitchControlMode.POSITION);

        if (mode_coordinate_ground)
            controlParam.setRollPitchCoordinateSystem(FlightCoordinateSystem.GROUND);
        else
            controlParam.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);

        if (mode_yaw_speed)
            controlParam.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
        else
            controlParam.setYawControlMode(YawControlMode.ANGLE);

        controlParam.setVerticalThrottle(vertical);
        controlParam.setYaw(yaw);
        controlParam.setRoll(roll);
        controlParam.setPitch(pitch);
        VirtualStickManager.getInstance().sendVirtualStickAdvancedParam(controlParam);
        return "OK";
    }

    public static String setVirtualStickAdvancedModeEnabled(boolean enabled) {
        VirtualStickManager.getInstance().setVirtualStickAdvancedModeEnabled(enabled);
        return "OK";
    }

    public static List<String> getAllIpAddresses() {
        List<String> ipAddresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    // Filtering out loopback addresses and link-local addresses
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        ipAddresses.add(inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddresses;
    }
    public String startTakeOff(int timeout) {
        // Initialize a CountDownLatch with a count of 1
        CountDownLatch latch = new CountDownLatch(1);
        final String[] result = new String[1]; // To capture the result

        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.startTakeOff(new CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>() {
            @Override
            public void onSuccess(EmptyMsg emptyMsg) {
                result[0] = "start takeOff onSuccess."; // Capture success result
                latch.countDown(); // Decrement the latch count to allow the main thread to proceed
            }

            @Override
            public void onFailure(IDJIError error) {
                result[0] = "start takeOff onFailure, " + error; // Capture failure result
                latch.countDown(); // Decrement the latch count
            }
        });

        try {
            boolean completed = latch.await(timeout, TimeUnit.SECONDS); // Wait for the callback or timeout
            if (!completed) {
                // Timeout occurred
                return "Operation timed out.";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            return "Thread was interrupted.";
        }

        return result[0]; // Return the captured result
    }
    public String startMotor(int timeout) {
        // Initialize a CountDownLatch with a count of 1
        CountDownLatch latch = new CountDownLatch(1);
        final String[] result = new String[1]; // To capture the result

        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.startMotor(new CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>() {
            @Override
            public void onSuccess(EmptyMsg emptyMsg) {
                result[0] = "startMotor onSuccess."; // Capture success result
                latch.countDown(); // Decrement the latch count to allow the main thread to proceed
            }

            @Override
            public void onFailure(IDJIError error) {
                result[0] = "startMotor onFailure, " + error; // Capture failure result
                latch.countDown(); // Decrement the latch count
            }
        });

        try {
            boolean completed = latch.await(timeout, TimeUnit.SECONDS); // Wait for the callback or timeout
            if (!completed) {
                // Timeout occurred
                return "Operation timed out.";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            return "Thread was interrupted.";
        }

        return result[0]; // Return the captured result
    }

    public String enableSimulator(int timeout, double lat, double lon, int gps_num) {
        SimulatorVM simulatorVM = new SimulatorVM();
        LocationCoordinate2D coordinate2D = new LocationCoordinate2D(lat, lon);
        InitializationSettings initializationSettings = InitializationSettings.createInstance(coordinate2D, gps_num);
        simulatorVM.enableSimulator(initializationSettings, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onSuccess() {
                //ToastUtils.showToast("start Success");
            }

            @Override
            public void onFailure(IDJIError error) {
                //ToastUtils.showToast("start Failed" + error.description());
            }
        });
        return "OK";
    }
    public boolean isSimulatorEnabled() {
        SimulatorVM simulatorVM = new SimulatorVM();
        return simulatorVM.isSimulatorEnabled();
    }

    public Location getLastLocation() {
        Location location = dji.v5.utils.common.LocationUtil.getLastLocation();
        return location;
    }

    public LocationCoordinate3D getAircraftLocation3D() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getAircraftLocation3D();
    }

    public Velocity3D getAircraftSpeed() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getAircraftSpeed();
    }
    public boolean getIsMotorOn() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getIsMotorOn();
    }
    public boolean getIsFlying() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getIsFlying();
    }
    public FlightMode getFlightMode() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getFlightMode();
    }

    public String native_SendData() {
        // Define the listener inline, implementing the onUpdateValue method to print the values
        Listener listener = new Listener() {
            @Override
            public void onUpdateValue(long var1, int var3, int var4, int var5, int var6, byte[] var7) {
                // Implement the logic to handle the callback data. For example, printing the values.
                Log.d("qq", "Received onUpdateValue callback with values:");
                Log.d("qq", "var1 (long): " + var1);
                Log.d("qq", "var3 (int): " + var3);
                Log.d("qq", "var4 (int): " + var4);
                Log.d("qq", "var5 (int): " + var5);
                Log.d("qq", "var6 (int): " + var6);
                Log.d("qq", "var7 (byte[]): ");
                if (var7 != null) {
                    for (byte b : var7) {
                        System.out.print(b + " ");
                    }
                    System.out.println(); // Add a new line after printing the byte array
                } else {
                    System.out.println("null");
                }
            }
        };
        // Assuming var0, var1, and var2 are the parameters you need to pass along with the listener
        int var0 = 0; // Set appropriately
        int var1 = 0; // Set appropriately
        int var2 = 0; // Set appropriately
        // Register the observer with the native method
        long registrationResult = native_RegisterObserver(var0, var1, var2, listener);
        // Optionally, handle the registration result
        Log.d("qq","Registration result: " + registrationResult);
        return "OK";
    }

    }




