package com.example.dimitris.falldetector.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.dimitris.falldetector.Alarm;
import com.example.dimitris.falldetector.core.Accelerometer;
import com.example.dimitris.falldetector.Constants;
import com.example.dimitris.falldetector.core.Plot;
import com.example.dimitris.falldetector.R;
import com.example.dimitris.falldetector.event.EventChart;
import com.example.dimitris.falldetector.event.EventTimer;
import com.example.dimitris.falldetector.event.EventTimerFinished;
import com.github.mikephil.charting.charts.LineChart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StartActivity extends AppCompatActivity {

    public static final String TAG = "StartActivity";

    private SwitchCompat mSwitchCompat;
    private LineChart mLineChart;
    private Plot mPlot;
    double latitude, longitude;
    private LocationManager locationManager;
    private LocationListener listener;
    Button btnCancel;
    private List<ContactModel> contactModels;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sharedPreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        mSwitchCompat = (SwitchCompat) findViewById(R.id.switch1);
        btnCancel = (Button) findViewById(R.id.cancel);
        mLineChart = (LineChart) findViewById(R.id.chart);

        mPlot = new Plot(mLineChart);
        mPlot.setUp();
        EventBus.getDefault().register(this);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        String jsonText = sharedPreferences.getString("key", null);
        contactModels = new Gson().fromJson(jsonText, new TypeToken<List<ContactModel>>() {
        }.getType());
        startService(new Intent(StartActivity.this, SensorBackgroundService.class));
        mSwitchCompat.setChecked(true);

        // set accelerometer sensor
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(StartActivity.this, SensorBackgroundService.class));
                } else {
                    stopService(new Intent(StartActivity.this, SensorBackgroundService.class));
                }
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.e("gps ", longitude + ":::::" + latitude);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();

        if (ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager != null) {
            List<String> providers = locationManager.getAllProviders();
            for (String provider : providers) {
                locationManager.requestLocationUpdates(provider, 5000, 0, listener);
            }
            //locationManager.requestLocationUpdates("gps", 5000, 0, listener);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateLineChart(EventChart eventChart) {
        mPlot.addEntry(eventChart.getValues());
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void eventCheck(EventTimerFinished eventTimerFinished) {
//        if (eventTimerFinished.getEvent().equals(Constants.ACTION_EVENT_CHECKED)) {
//            mSwitchCompat.setChecked(false);
//        }
//    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventTimerStart(EventTimer e) {
        btnCancel.setText(String.format("%02d", e.getMinutes())
                + ":" + String.format("%02d", e.getSecond()));
        //here you can have your logic to set text to edittext
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("stop", true);
                edit.apply();
                btnCancel.setText("SMS Alert Cancelled");
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventTimerFinished(EventTimerFinished e) {
        if (e.getEvent().equals(Constants.ACTION_EVENT_TIMER_FINISHED)) {
            int permissionCheck = ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.SEND_SMS);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        StartActivity.this, new String[]{Manifest.permission.SEND_SMS}, 123);
            }
            btnCancel.setText("SMS Alert Activated");
            sendSms();
        }
    }


    public void sendSms() {
        SmsManager sms = SmsManager.getDefault();
        String message = "Your loved ones are falling at coordinates: %.5f , %.5f , " +
                "link : http://maps.google.com/?q=%.5f,%.5f";
        message = String.format(Locale.US, message, latitude, longitude, latitude, longitude);
        try {
            if (contactModels != null) {
                for (ContactModel contactModel : contactModels) {
                    if (!contactModel.getPhone().isEmpty()) {
                        sms.sendTextMessage(contactModel.getPhone(), null, message, null, null);
                        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sendSms();
                } else {
                    Log.d("TAG", "Call Permission Not Granted");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
