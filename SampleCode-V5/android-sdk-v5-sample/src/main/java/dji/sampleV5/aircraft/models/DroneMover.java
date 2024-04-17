package dji.sampleV5.aircraft.models;

import static dji.raw.jni.JNIRawData.native_RegisterObserver;
import static dji.v5.common.utils.CallbackUtils.onSuccess;
import static dji.v5.ux.map.MapWidgetModel.INVALID_COORDINATE;

import android.location.GnssClock;
import android.location.GnssMeasurementsEvent;
import android.location.Location;
import android.util.Log;

import dji.raw.jni.callback.Listener;
import dji.sdk.keyvalue.value.common.LocationCoordinate3D;
import dji.sdk.keyvalue.value.common.Velocity3D;
import dji.sdk.keyvalue.value.flightcontroller.*;
import dji.v5.common.callback.CommonCallbacks;

import dji.v5.common.error.IDJIError;
import dji.v5.manager.aircraft.simulator.InitializationSettings;
import dji.v5.manager.aircraft.virtualstick.VirtualStickManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.TimeUnit;

import dji.sdk.keyvalue.value.common.EmptyMsg;
import dji.sdk.keyvalue.value.common.LocationCoordinate2D;
import dji.v5.utils.common.ContextUtil;
import dji.v5.utils.common.DiskUtil;
import dji.v5.ux.core.util.DataProcessor;
import dji.v5.ux.ui.WebOverlay_no_touch;

public class DroneMover {
    static DroneMover droneMover = null;
    float _pitch = 0;
    float _roll = 0;
    float _yaw = 179;
    float desiredFlyingHeight = 20;
    private final DataProcessor<LocationCoordinate2D> homeLocationDataProcessor =
            DataProcessor.create(new LocationCoordinate2D(INVALID_COORDINATE, INVALID_COORDINATE));

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
        VirtualStickFlightControlParam controlParam = new VirtualStickFlightControlParam();
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

    public String setGimbalAttitude(double pitch, double yaw, double roll,
                                  boolean pitchIgnored, boolean yawIgnored, boolean rollIgnored,
                                  double duration, boolean absoluteAngle) {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.setGimbalAttitude(pitch, yaw, roll,
                pitchIgnored, yawIgnored, rollIgnored,
                duration, absoluteAngle);
        return "";
    }
    public String setGimbalMode(boolean free) {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.setGimbalMode(free);
        return "";
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

    public String disableSimulator(int timeout) {
        SimulatorVM simulatorVM = new SimulatorVM();
        simulatorVM.disableSimulator(new CommonCallbacks.CompletionCallback() {
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

    public Location getLastLocation() {
        Location location = dji.v5.utils.common.LocationUtil.getLastLocation();
        return location;
    }

    public LocationCoordinate3D getAircraftLocation3D() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getAircraftLocation3D();
    }

    public double getAltitude() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getAltitude();
    }

    public int getUltrasonicHeight() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getUltrasonicHeight();
    }

    public int[] getRcSticks() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        int[] sticks = new int[4];
        sticks[0] = basicAircraftControlVM.getStickLeftHorizontal();
        sticks[1] = basicAircraftControlVM.getStickLeftVertical();
        sticks[2] = basicAircraftControlVM.getStickRightHorizontal();
        sticks[3] = basicAircraftControlVM.getStickRightVertical();
        return sticks;
    }
    public int getStickLeftHorizontal() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getStickLeftHorizontal();
    }

    public int getStickLeftVertical() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getStickLeftVertical();
    }

    public int getStickRightHorizontal() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getStickRightHorizontal();
    }

    public int getStickRightVertical() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getStickRightVertical();
    }

    public double[] getHomeLocation() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        LocationCoordinate2D loc = basicAircraftControlVM.getHomeLocation();
        double[] home = new double[3];
        home[0] = loc.getLatitude();
        home[1] = loc.getLongitude();
        home[2] = getTakeoffLocationAltitude();
        return home;
    }

    public double getTakeoffLocationAltitude() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getTakeoffLocationAltitude();
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

    // KeyHeightAboveSeaLevel
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
        Log.d("qq", "Registration result: " + registrationResult);
        return "OK";
    }

    public String setHomeLocation(double lat, double lon) {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.setHomeLocation(lat,lon);
        return "OK";
    }

    public String getDroneType() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getDroneType();
    }

    public String setCoordinatedTurnEnabled(boolean enable) {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.setCoordinatedTurnEnabled(enable);
        return "OK";
    }


    public String setKeyLEDsSettings(Boolean frontLEDsOn, Boolean statusIndicatorLEDsOn, Boolean rearLEDsOn, Boolean navigationLEDsOn) {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.setKeyLEDsSettings(frontLEDsOn, statusIndicatorLEDsOn, rearLEDsOn, navigationLEDsOn);
        return "OK";
    }

    public String[] testConnection(String s) {
        String[] return_s=new String[2];
        return_s[0]="Hello from Dronemover Android ";
        return_s[1]=s;

        return return_s;
    }

    public String startStreamingFpv() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.startStreamingFpv();
        return "OK";
    }

    public String startStreamingUdp(String ip, Integer port) {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.startStreamingUdp(ip, port);
        return "OK";
    }

    public String updateHtmlNoTouch(String html) {
        WebOverlay_no_touch.getInstance().loadData(html, "text/html", null);
        return "OK";
    }


    public String getFpvFrameFilePath() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getFpvFrameFilePath();
    }

    public String startRecord() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.startRecord();
        return "OK";
    }
    public String stopRecord() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.stopRecord();
        return "OK";
    }
    public String startShootPhoto() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.startShootPhoto();
        return "OK";
    }
    public String stopShootPhoto() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        basicAircraftControlVM.stopShootPhoto();
        return "OK";
    }
    public String getAircraftName() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getAircraftName();
    }
    public String setAircraftName(String name) {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.setAircraftName(name);
    }

    public String getExternalCacheDirPath() {
        return DiskUtil.getExternalCacheDirPath(ContextUtil.getContext(), "");
    }
    public Integer getChargeRemainingInPercent() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.getChargeRemainingInPercent();
    }
    public double[] getRcAndroidGps() {
        double[] gpsInfo = new double[9];
        Location loc = GpsListener.latestLocation;
        GnssMeasurementsEvent raw = GpsListener.latestGnssMeasurementsEvent;
        GnssClock gnssClock = raw.getClock();
        long timeNanos = gnssClock.getTimeNanos();
        long fullBiasNanos = gnssClock.hasFullBiasNanos() ? gnssClock.getFullBiasNanos() : 0;

        // Calculate GPS time in milliseconds since the GPS epoch (January 6, 1980)
        long gpsTimeMillis = (timeNanos - fullBiasNanos) / 1000000;
        // Adjust for the GPS epoch
        //long gpsEpochMillis = calculateGpsEpochMillis();
        //long currentTimeMillis = gpsEpochMillis + gpsTimeMillis;
        gpsInfo[0] = loc.getLatitude();
        gpsInfo[1] = loc.getLongitude();
        gpsInfo[2] = loc.getAltitude();
        gpsInfo[3] = loc.getSpeed();
        gpsInfo[4] = loc.getBearing();
        gpsInfo[5] = (double)loc.getTime();
        gpsInfo[6] = (double)gpsTimeMillis;
        gpsInfo[7] = (double)gnssClock.getFullBiasNanos();
        gpsInfo[8] = GpsListener.latestUpdateAge();

        return gpsInfo;
    }

    public String startGoHome() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.startGoHome();
    }

    public String formatStorageSD() {
        BasicAircraftControlVM basicAircraftControlVM = new BasicAircraftControlVM();
        return basicAircraftControlVM.formatStorageSD();
    }





  /*
    public String enableGnssMeasurements() {
        {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return TODO;
            }
            locationManager.registerGnssMeasurementsCallback(new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                    for (GnssMeasurement measurement : eventArgs.getMeasurements()) {
                        // Process the GNSS measurements, including time information
                        long timeInNs = measurement.getReceivedSvTimeNanos();
                        // Convert and use the time as needed
                    }
                }

                @Override
                public void onStatusChanged(int status) {
                    // Handle status changes
                }
            });
    }
*/
    }




