package com.example.nimishgupta.mycollege;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class your_SAC_booking extends AppCompatActivity {

    private RecyclerView mUserSide;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Encryption encryption = new Encryption();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_developer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your SAC Requests");

        if(isOnline()) {
        }
        else{
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(your_SAC_booking.this).create();
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
        }

        SharedPreferences sp = your_SAC_booking.this.getSharedPreferences("com.example.nimishgupta.mycollege",MODE_PRIVATE);
        String userName = sp.getString("userID","default");

        mDatabase=FirebaseDatabase.getInstance().getReference().child("UserResponses_SAC").child(encryption.md5(userName));;
        mDatabase.keepSynced(true);
        mUserSide=(RecyclerView)findViewById(R.id.myRecyclerView);
        mUserSide.setHasFixedSize(true);
        mUserSide.setLayoutManager(new LinearLayoutManager(this));
        setupFirebaseListener();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(your_SAC_booking.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private void setupFirebaseListener(){
        Log.d("setup firebase listener","setting up auth state listener");
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    Log.d("onAuthStateChanged","signed in "+firebaseUser.getUid() );
                }
                else {
                    Log.d("onAuthStateChanged","signed out");
                    Toast.makeText(your_SAC_booking.this, "Siging out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(your_SAC_booking.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }


    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<SAC_response,your_SAC_booking.UserResponseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SAC_response, your_SAC_booking.UserResponseViewHolder>
                (SAC_response.class,R.layout.sac_card_foruser, your_SAC_booking.UserResponseViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(your_SAC_booking.UserResponseViewHolder userViewHolder, SAC_response model, int position) {
                userViewHolder.setReason(model.getReason());
                userViewHolder.setUser(model.getUserName());
                userViewHolder.setDay(model.getuNextDate());
                userViewHolder.setFromTime(model.getFromTime());
                userViewHolder.setToTime(model.getToTime());
                userViewHolder.setStatus(model.getStatus());
                userViewHolder.setuDate(model.getuDate());
                userViewHolder.setuTime(model.getuTime());
            }
        };
        mUserSide.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserResponseViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserResponseViewHolder(View responseView)
        {
            super(responseView);
            mView=itemView;
        }


        public void setReason(String reason){
            //mReason = reason;
            TextView reasonReqdForBooking = (TextView)itemView.findViewById(R.id.reasonReqdForBooking);
            reasonReqdForBooking.setText(reason);
        }

        public void setUser(String user){
            //userName = user;
            TextView userWhoBooked = (TextView)itemView.findViewById(R.id.userWhoBooked);
            userWhoBooked.setText(user);
        }

        public void setDay(String day){
            TextView _day = (TextView)itemView.findViewById(R.id.day);
            _day.setText(day);
        }

        public void setFromTime(String fTime){
            //fromTime = fTime;
            TextView _fromTime = (TextView)itemView.findViewById(R.id.fromTime);
            _fromTime.setText(fTime);
        }

        public void setToTime(String tTime){
            //toTime = tTime;
            TextView _toTime = (TextView)itemView.findViewById(R.id.toTime);
            _toTime.setText(tTime);
        }

        public void setStatus(String status){
            TextView status_ = (TextView)itemView.findViewById(R.id.status);
            if(status.equals("Rejected")){
                status_.setTextColor(Color.parseColor("#e60000"));
            }else if(status.equals("Accepted")){
                status_.setTextColor(Color.parseColor("#33cc33"));
            }else{
                status_.setTextColor(Color.parseColor("#cccc00"));
            }
            status_.setText(String.valueOf(status));
        }

        public void setuDate(String date){}

        public void setuTime(String time){}


    }
}
