package com.example.dimitris.falldetector.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dimitris.falldetector.Constants;
import com.example.dimitris.falldetector.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SetActivity extends AppCompatActivity {

    public static final String TAG = "SetActivity";
    private Button mBttnDone;
    PhoneAdapter phoneAdapter;
    RecyclerView recycleContact;
    int posSelected = 0;
    List<ContactModel> contactList;
    SharedPreferences sharedPreferences;
    String contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);


        sharedPreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        // use last contact's data
        String jsonText = sharedPreferences.getString("key", null);
        List<ContactModel> mContactList = new Gson().fromJson(jsonText, new TypeToken<List<ContactModel>>() {}.getType());
        contactList = new ArrayList<>();
        recycleContact = findViewById(R.id.recycleContact);
        recycleContact.setNestedScrollingEnabled(false);
        recycleContact.setLayoutManager(new LinearLayoutManager(this));
        phoneAdapter = new PhoneAdapter(contactList, getApplicationContext(), new PhoneAdapter.onItemClickClick() {
            @Override
            public void addItem(ContactModel contact) {
                posSelected = contact.getPos();
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 4);
            }

            @Override
            public void removeItem(ContactModel contact) {
                contact.setPhone("");
                contact.setName(contact.getName());
                contact.setPos(contact.getPos());
                contactList.set(posSelected, contact);
                phoneAdapter.notifyItemChanged(contact.getPos());
            }
        });
        recycleContact.setAdapter(phoneAdapter);

        if (mContactList == null) {
            for (int i = 0; i < 3; i++) {
                contactList.add(new ContactModel("Số điện thoại " + (i + 1), "", i));
            }
        } else {
            contactList.addAll(mContactList);
        }
        phoneAdapter.notifyDataSetChanged();
        mBttnDone = (Button) findViewById(R.id.bttn_done);
        mBttnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Gson gson = new Gson();
                    String jsonText = gson.toJson(contactList);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("key", jsonText);
                    editor.apply();
                } catch (Exception e) {
                    Log.e(TAG, "onClick: error during setting code or phone number");
                    Toast.makeText(getApplicationContext(), "Error during initializing contact", Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent();
                intent.putExtra("list", (Serializable) contactList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 4) {
            Uri phone_uri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(phone_uri, projection,
                    null, null, null);
            cursor.moveToFirst();
            int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            contactNumber = cursor.getString(numberColumnIndex);
            ContactModel contact = new ContactModel();
            contact.setPhone(contactNumber);
            contact.setName("Số điện thoại " + (posSelected + 1));
            contact.setPos(posSelected);
            contactList.set(posSelected, contact);
            Log.e("contactList", contactList.toString());
            phoneAdapter.notifyItemChanged(posSelected);
        } else {
            Toast.makeText(this, "No result", Toast.LENGTH_LONG).show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
