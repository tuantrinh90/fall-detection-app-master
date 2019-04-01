package com.example.dimitris.falldetector.ui;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.dimitris.falldetector.Alarm;
import com.example.dimitris.falldetector.Constants;
import com.example.dimitris.falldetector.R;
import com.example.dimitris.falldetector.core.Accelerometer;
import com.example.dimitris.falldetector.event.EventChart;
import com.example.dimitris.falldetector.event.EventTimer;
import com.example.dimitris.falldetector.event.EventTimerFinished;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.util.Date;


public class SensorBackgroundService extends Service {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    CountDownTimer timer;
    Accelerometer accelerometer;
    private SharedPreferences sharedPreferences;
    private String ANDROID_CHANNEL_ID = "ANDROID_CHANNEL_ID";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometer = new Accelerometer(mSensorManager, mSensor, mHandler);
        accelerometer.startListening();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(1, notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(1, notification);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accelerometer.stopListening();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void timerFall() {
        timer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                EventBus.getDefault().post(new EventTimer(minutes, seconds));
                if (sharedPreferences.getBoolean("stop", false)) {
                    timer.cancel();
                }
            }

            public void onFinish() {
                EventBus.getDefault().post(new EventTimerFinished(Constants.ACTION_EVENT_TIMER_FINISHED));
            }

        }.start();
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_CHANGED:
                    float value = msg.getData().getFloat(Constants.VALUE);
                    EventBus.getDefault().post(new EventChart(value));
                    break;
                case Constants.MESSAGE_EMERGENCY:
                    Alarm.call(getApplicationContext());
                    timerFall();
                    String newHistory = DateFormat.getDateTimeInstance().format(new Date()) + "\n";
                    // save history to shared preferences
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String oldHistory = sharedPreferences.getString(Constants.History, null); // get previous history
                    editor.putString(Constants.History, oldHistory + newHistory);
                    editor.commit();
                    // stop listening the sensor
                    //EventBus.getDefault().post(new EventTimerFinished(Constants.ACTION_EVENT_CHECKED));
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}

