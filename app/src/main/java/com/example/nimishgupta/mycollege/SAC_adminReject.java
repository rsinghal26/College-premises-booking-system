package com.example.nimishgupta.mycollege;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class SAC_adminReject extends AppCompatActivity {


    private RecyclerView mUserResponse;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reject Requests");

        if (isOnline()) {
        } else {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(SAC_adminReject.this).create();

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

        mDatabase = FirebaseDatabase.getInstance().getReference().child("SAC_RejectRequests");
        mDatabase.keepSynced(true);

        mUserResponse = (RecyclerView) findViewById(R.id.myRecyclerView);
        mUserResponse.setHasFixedSize(true);
        mUserResponse.setLayoutManager(new LinearLayoutManager(this));
        setupFirebaseListener();
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(SAC_adminReject.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupFirebaseListener() {
        Log.d("setup firebase listener", "setting up auth state listener");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Log.d("onAuthStateChanged", "signed in " + firebaseUser.getUid());
                } else {
                    Log.d("onAuthStateChanged", "signed out");
                    Toast.makeText(SAC_adminReject.this, "Siging out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SAC_adminReject.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<SAC_response, SAC_adminReject.UserResponseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SAC_response, SAC_adminReject.UserResponseViewHolder>
                (SAC_response.class, R.layout.sac_reject_card, SAC_adminReject.UserResponseViewHolder.class, mDatabase) {
            @Override
            protected void populateViewHolder(SAC_adminReject.UserResponseViewHolder userViewHolder, SAC_response model, int position) {
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
        mUserResponse.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserResponseViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserResponseViewHolder(View responseView) {
            super(responseView);
            mView = itemView;

        }


        public void setReason(String reason) {
            //mReason = reason;
            TextView reasonReqdForBooking = (TextView) itemView.findViewById(R.id.reasonReqdForBooking);
            reasonReqdForBooking.setText(reason);
        }

        public void setUser(String user) {
            //userName = user;
            TextView userWhoBooked = (TextView) itemView.findViewById(R.id.userWhoBooked);
            userWhoBooked.setText(user);
        }

        public void setDay(String day) {
            //nextDateStr = day;
            TextView _day = (TextView) itemView.findViewById(R.id.day);
            _day.setText(day);
        }

        public void setFromTime(String fTime) {
            //fromTime = fTime;
            TextView _fromTime = (TextView) itemView.findViewById(R.id.fromTime);
            _fromTime.setText(fTime);
        }

        public void setToTime(String tTime) {
            // toTime = tTime;
            TextView _toTime = (TextView) itemView.findViewById(R.id.toTime);
            _toTime.setText(tTime);
        }

        public void setStatus(String status) {
            //statusOfBooking = status;
            //card.setBackgroundColor(Color.parseColor("#80ff80"));
        }

        public void setuDate(String date) {
            //uDate = date;
        }

        public void setuTime(String time) {
            //uTime = time;
        }


    }

}
