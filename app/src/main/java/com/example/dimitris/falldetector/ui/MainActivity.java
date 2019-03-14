package com.example.dimitris.falldetector.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.dimitris.falldetector.R;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";

    private Button btnSet;
    private Button btnStart;
    private Button btnHistory;
    private List<ContactModel> contactModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnSet = (Button) findViewById(R.id.btn_set);
        btnSet.setOnClickListener(this);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);

        btnHistory = (Button) findViewById(R.id.btn_history);
        btnHistory.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                contactModels = (List<ContactModel>) data.getSerializableExtra("list");
                Log.e("list", contactModels.toString());
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_set:
                intent = new Intent(this, SetActivity.class);
                startActivityForResult(intent, 111);
                return;
            case R.id.btn_start:
                intent = new Intent(this, StartActivity.class);
                intent.putExtra("ContactList", (Serializable) contactModels);
                startActivity(intent);
                return;
            case R.id.btn_history:
                intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                return;
        }
    }

}
