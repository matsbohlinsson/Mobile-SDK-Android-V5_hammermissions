package dji.sampleV5.aircraft.models;

import android.content.Context;
import android.location.GnssMeasurementsEvent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

public class GpsListener {

    private static LocationManager locationManager;
    private static Context context;

    // Static variables to hold the latest updates
    public static volatile Location latestLocation = null;
    public static volatile GnssMeasurementsEvent latestGnssMeasurementsEvent = null;
    public static volatile long latestTimestamp = SystemClock.elapsedRealtimeNanos();

    public static void initialize(Context appContext) {
        context = appContext;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Request updates from GPS as fast as possible
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener);

        // Listen to raw GNSS measurements for the most accurate clock
        locationManager.registerGnssMeasurementsCallback(gnssMeasurementsCallback, null);
    }

    public static long latestUpdateAge() {
        return SystemClock.elapsedRealtimeNanos() - latestTimestamp;
    }

    private static final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latestLocation = location;
            latestTimestamp = SystemClock.elapsedRealtimeNanos();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private static final GnssMeasurementsEvent.Callback gnssMeasurementsCallback = new GnssMeasurementsEvent.Callback() {
        @Override
        public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
            latestGnssMeasurementsEvent = eventArgs;
        }
    };
}
