package com.example.nimishgupta.mycollege;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class freeSlotActivity extends AppCompatActivity {

    private ListView mListView;
    private Button submit;
    private ArrayList<String> infoValues = new ArrayList<>();
    private ArrayList<String> infoKeys = new ArrayList<>();
    private EditText reasonForBooking;
    private CheckBox mikeCBox, projectorCBox;
    private ProgressDialog progressDialog;
    DatabaseReference fromFirebase;
    DatabaseReference databaseReference;
    String labNumber,labSlot, ltno, ltSlot;
    Encryption encryption = new Encryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_slot);
        mListView = (ListView)findViewById(R.id.listiew);
        getSupportActionBar().setTitle("Availability");

        if(isOnline()) {
        }
        else{
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(freeSlotActivity.this).create();

                alertDialog.setTitle("Warning!!");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again.");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();
            } catch (Exception e) {
//                Log.d(Constants.TAG, "Show Dialog: " + e.getMessage());
            }
            return;
        }

        if(!checkTimeIntervel()){
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(freeSlotActivity.this).create();

                alertDialog.setTitle("Warning!!");
                alertDialog.setMessage("Booking system can only available between 8:00 A.M. to 6:00 P.M.");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                alertDialog.show();
            } catch (Exception e) {
//                Log.d(Constants.TAG, "Show Dialog: " + e.getMessage());
            }
            return;
        }

        //List adapter------------------------------------------------------------
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,infoKeys){

            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);
                if(position %2 == 1)
                {
                    // Set a background color for ListView regular row/item
                    view.setBackgroundColor(Color.parseColor("#ffcccc"));
                }
                else
                {
                    // Set the background color for alternate row/item
                    view.setBackgroundColor(Color.parseColor("#ffe6e6"));
                }
                return view;
            }
        };

        mListView.setAdapter(arrayAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(freeSlotActivity.this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showInputBox(infoKeys.get(position),position);
            }
        });


        if(getIntent().hasExtra("type")){

            String value = getIntent().getExtras().getString("type");
            progressDialog.setMessage("Fetching available slots...");
            progressDialog.show();
            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            };
            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 2000);


            if(value.equals("LAB")){

                labNumber = getIntent().getExtras().getString("labNumber");
                labSlot = getIntent().getExtras().getString("labSlot");

                TextView heading = findViewById(R.id.textHeading);
                heading.setText("Available Slots for "+labNumber);

                if(labSlot.equals("Morning (8:00 am-12:00 noon)")){
                    labSlot="Morning";
                }
                else {
                    labSlot = "AfterNoon";
                }
                fromFirebase = databaseReference.child("CPLabs").child(labNumber).child(labSlot);
                fromFirebase.addChildEventListener(new ChildEventListener() {
                @Override
                    public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

                        String value = dataSnapshot.getValue(String.class);

                        if (value.equals("A")) {
                            infoValues.add(value);
                            String key = dataSnapshot.getKey();
                            infoKeys.add(key);
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                        String value = dataSnapshot.getValue(String.class);
                        String key = dataSnapshot.getKey();
                        int index = infoKeys.indexOf(key);
                        infoValues.set(index, value);
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            else if(value.equals("LT")){
                ltno = getIntent().getExtras().getString("LTNumber");
                ltSlot = getIntent().getExtras().getString("LTSlot");

                TextView heading = findViewById(R.id.textHeading);
                heading.setText("Available Slots for "+ltno);

                if(ltSlot.equals("Morning (8:00 am-12:00 noon)")){
                    ltSlot="Morning";
                }
                else if(ltSlot.equals("AfterNoon (12:00 noon-5:00 pm)"))
                    ltSlot="AfterNoon";
                else
                    ltSlot = "Evening";

                fromFirebase = databaseReference.child("LTs").child(ltno).child(ltSlot);
                fromFirebase.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

                        String value = dataSnapshot.getValue(String.class);

                        if (value.equals("A")) {
                            infoValues.add(value);
                            String key = dataSnapshot.getKey();
                            infoKeys.add(key);
                        }
                        arrayAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildChanged(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {
                        String value = dataSnapshot.getValue(String.class);
                        String key = dataSnapshot.getKey();
                        int index = infoKeys.indexOf(key);
                        infoValues.set(index, value);
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot, @Nullable String s) {

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }


    }

    public boolean checkTimeIntervel() {

        Date today = new Date();
        SimpleDateFormat time = new SimpleDateFormat("kk:mm:ss");
        String timeToString = time.format(today);
        try {
            String string1 = "8:00:00";
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String string2 = "18:00:00";
            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            String someRandomTime = timeToString;

            Date d = new SimpleDateFormat("HH:mm:ss").parse(someRandomTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

//    For checking  Internet Connection...
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(freeSlotActivity.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void showInputBox(final String newValue,int index){
        final Dialog dialog = new Dialog(freeSlotActivity.this);
        dialog.setTitle("Enter Details..");
        dialog.setContentView(R.layout.input_box);

        reasonForBooking = (EditText)dialog.findViewById(R.id.reason);
        submit = (Button)dialog.findViewById(R.id.submitButton);
        mikeCBox = (CheckBox)dialog.findViewById(R.id.checkBoxMike);
        projectorCBox = (CheckBox)dialog.findViewById(R.id.checkBoxProjector);
        submit.setHeight(100);
        submit.setWidth(200);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mikeReqd=0, projectorReqd=0;
                String statusOfBooking = "Pending";

                Date today = new Date();
                SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                String dateToStr = date.format(today);
                String timeToStr = time.format(today);

                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                String nextDateStr = format1.format(c.getTime());

                String mReason = reasonForBooking.getText().toString();
                SharedPreferences sp = freeSlotActivity.this.getSharedPreferences("com.example.nimishgupta.mycollege",MODE_PRIVATE);
                String userName = sp.getString("userID","default");
                if(getIntent().hasExtra("type")) {
                    String value = getIntent().getExtras().getString("type");
                    if (value.equals("LAB")) {
                        if (!TextUtils.isEmpty(mReason)) {
                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("CPLabs").child(labNumber).child(labSlot).child(newValue);
                            //Toast.makeText(freeSlotActivity.this,"share data "+userName,Toast.LENGTH_SHORT).show();

                            final DatabaseReference firebaseUserResponse = FirebaseDatabase.getInstance().getReference("UserResponses").child(encryption.md5(userName));
                            final DatabaseReference firebaseUserResponseForAdmin = FirebaseDatabase.getInstance().getReference("PendingRequests");

                            rootRef.setValue("NA");
                            String id = encryption.md5(encryption.md5(userName)+dateToStr+timeToStr);
                            if (mikeCBox.isChecked()) mikeReqd = 1;
                            if (projectorCBox.isChecked()) projectorReqd = 1;
                            UserResponse userResponse = new UserResponse(mReason, newValue, userName, mikeReqd, projectorReqd,labNumber ,statusOfBooking,dateToStr,timeToStr,nextDateStr,labSlot);
                            firebaseUserResponse.child(id).setValue(userResponse);
                            firebaseUserResponseForAdmin.child(id).setValue(userResponse);
                            Toast.makeText(freeSlotActivity.this, "Request send to Admin", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(freeSlotActivity.this,"Enter your reason", Toast.LENGTH_LONG).show();
                        }
                    }

                    else {
                        if (!TextUtils.isEmpty(newValue) && !TextUtils.isEmpty(mReason)) {
                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("LTs").child(ltno).child(ltSlot).child(newValue);
                            final DatabaseReference firebaseUserResponse = FirebaseDatabase.getInstance().getReference("UserResponses").child(encryption.md5(userName));
                            final DatabaseReference firebaseUserResponseForAdmin = FirebaseDatabase.getInstance().getReference("PendingRequests");

                            rootRef.setValue("NA");
                            String id = encryption.md5(encryption.md5(userName)+dateToStr+timeToStr);//firebaseUserResponse.push().getKey();
                            if (mikeCBox.isChecked()) mikeReqd = 1;
                            if (projectorCBox.isChecked()) projectorReqd = 1;
                            UserResponse userResponse = new UserResponse(mReason, newValue, userName, mikeReqd, projectorReqd, ltno, statusOfBooking,dateToStr,timeToStr,nextDateStr,ltSlot);
                            firebaseUserResponse.child(id).setValue(userResponse);
                            firebaseUserResponseForAdmin.child(id).setValue(userResponse);
                            Toast.makeText(freeSlotActivity.this, newValue + " slot is booked", Toast.LENGTH_LONG).show();

                            finish();
                        }else {
                            Toast.makeText(freeSlotActivity.this, "Enter your reason", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        dialog.show();
    }

}