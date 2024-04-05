package dji.sampleV5.aircraft.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;


import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CommonUI {
    static public void hideSystemUI(AppCompatActivity window) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        PowerManager pm = (PowerManager) window.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MavicMax:");
        wl.acquire();

    }

    static public void hideSystemUI(Activity window) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        PowerManager pm = (PowerManager) window.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MavicMax:");
        wl.acquire();

    }

    static public void hideSystemUI(FragmentActivity window) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        PowerManager pm = (PowerManager) window.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MavicMax:");
        wl.acquire();

    }

    static public void show_dialog(Activity activity, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.show();
    }

    static TextToSpeech tts=null;
    static Activity activity;
    static public void init(Activity activity){
        CommonUI.activity = activity;
        if (tts == null) {
            tts = new TextToSpeech(activity, status -> {
                // TODO Auto-generated method stub
                if (status != TextToSpeech.SUCCESS) {
                    Log.e("MavicMax", "error");
                    return;
                }
                tts.setLanguage(Locale.US);
                tts.speak("", TextToSpeech.QUEUE_ADD, null);

            } );
            return;
        }
    }
    static public void text_to_speech(String text){
        if (tts == null)
            return;
        CommonUI.activity.runOnUiThread(()->{tts.setLanguage(Locale.US);tts.setSpeechRate(0.8F);});
        CommonUI.activity.runOnUiThread(()->{tts.speak(text, TextToSpeech.QUEUE_ADD, null);});

    }

    static public void text_to_speech_with_pitch_speed(String text, float pitch, float rate){
        if (tts == null)
            return;
        CommonUI.activity.runOnUiThread(()->{tts.setLanguage(Locale.US);tts.setSpeechRate(rate);tts.setPitch(pitch);});
        CommonUI.activity.runOnUiThread(()->{tts.speak(text, TextToSpeech.QUEUE_ADD, null);});

    }

    static public void send_email(String address, String subject, String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {address});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        /*
        File root = Environment.getExternalStorageDirectory();
        String pathToMyAttachedFile = "temp/attachement.xml";
        File file = new File(root, pathToMyAttachedFile);
        if (!file.exists() || !file.canRead()) {
            return;
        }
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

         */
        CommonUI.activity.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }

    static public void send_sms(String number, String message) {
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(number, null, message, null, null);
    }

    static public class Beeper {
        private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        int freq;
        int durationMs;
        int silentMs;
        int loop;
        ScheduledFuture<?> s;
        ToneGenerator toneG =  new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        public void Beeper() {
        }

        void set(int freq, int durationMs, int silentMs, int loop){
            this.freq=freq;
            this.durationMs=durationMs;
            this.silentMs=silentMs;
            this.loop=loop;
            if (s!=null)
                s.cancel(true);
            s=scheduledExecutorService.schedule( this::beep, 0, TimeUnit.MILLISECONDS);
        }

        public void stop() {
            s.cancel(true);
            s = null;
        }

        public void beep (){
            try {
                toneG.startTone(this.freq, this.durationMs);
                if (--this.loop > 0)
                    s=scheduledExecutorService.schedule( this::beep, durationMs + silentMs, TimeUnit.MILLISECONDS);
            }
            catch (Exception e) {
            }
        }
        }


    static Beeper beeper[] =  {new Beeper(),new Beeper(),new Beeper(),new Beeper(),new Beeper()};
    static public void beep(int index, int freq, int durationMs, int silentMs, int loop) {
        beeper[index].set(freq, durationMs, silentMs, loop);
    }
}
