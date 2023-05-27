package com.example.womensafetyapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.womensafetyapp.Model.Contacts;
import com.example.womensafetyapp.View.Contact_RecyclerViewAdapter;
import com.example.womensafetyapp.ViewModel.ContactViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int IGNORE_BATTERY_OPTIMIZATION_REQUEST = 1002;

    Button button;
    Button button1;
    Button add_contact;
    Contact_RecyclerViewAdapter contactRecyclerViewAdapter;
    RecyclerView recyclerView;

    static List<Contacts> contactsList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         button=findViewById(R.id.Cancel_button);
         button1=findViewById(R.id.Start_button);
         add_contact=findViewById(R.id.add_contact_button);
         recyclerView=(RecyclerView) findViewById(R.id.contact_recycler);



        SensorService sensorService = new SensorService();
        Intent intent = new Intent(this, sensorService.getClass());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel("example.permanence", "Background Service",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.setDescription("SHOW BACKGROUND");
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, 100);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 100);
        }
        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

        ContactViewModel contactViewModel= new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication())
                .create(ContactViewModel.class);

        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        contactViewModel.get_all_contacts().observe(this, new Observer<List<Contacts>>() {
            @Override
            public void onChanged(List<Contacts> contacts) {
                contactsList=contacts;
                Log.d("Recycler check", "onChanged: "+ contacts.size());
                contactRecyclerViewAdapter=new Contact_RecyclerViewAdapter(MainActivity.this,contactsList,contactViewModel);
                contactRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(contactRecyclerViewAdapter);




        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT< Build.VERSION_CODES.P
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.FOREGROUND_SERVICE)
                == PackageManager.PERMISSION_GRANTED){
//                    workManager.enqueue(workRequest);
                    startService(intent);
                }
               else {
                    requestPermissions(new String[]{Manifest.permission.FOREGROUND_SERVICE},100);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
            }
        });
        ActivityResultLauncher<Intent> ActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data=result.getData();
                            Uri contactData = data.getData();
                            CursorLoader cursorLoader= new CursorLoader(MainActivity.this,contactData,null,null,null,null);
                            Cursor c = cursorLoader.loadInBackground();
                            if (c.moveToFirst()) {

                                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                                String hasPhone = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                                String phone = null;
                                try {
                                    if (hasPhone.equalsIgnoreCase("1")) {
                                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                                        phones.moveToFirst();
                                        phone = phones.getString(phones.getColumnIndexOrThrow("data1"));
                                    }
                                    String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                                    contactViewModel.add_contact(new ArrayList<>(Arrays.asList(new Contacts(name,phone))));
                                    contactViewModel.get_all_contacts().observe(MainActivity.this,
                                            new Observer<List<Contacts>>() {
                                        @Override
                                        public void onChanged(List<Contacts> contacts) {
                                            contactsList=contacts;
                                        }
                                    });
                                    contactRecyclerViewAdapter.refresh(contactsList);
                                } catch (Exception ex) {
                                }
                            }

                        }
                    }
                });
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                ActivityResultLauncher.launch(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permissions Denied!\n Can't use the App!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}