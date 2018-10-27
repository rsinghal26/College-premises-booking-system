package com.example.nimishgupta.mycollege;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SAC_adminActivity extends AppCompatActivity {

    private RecyclerView mUserResponse;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);
        getSupportActionBar().setTitle("SAC Requests");

        if(isOnline()) {
        }
        else{
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(SAC_adminActivity.this).create();

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

        mDatabase=FirebaseDatabase.getInstance().getReference().child("SAC_bookings");
        mDatabase.keepSynced(true);

        mUserResponse=(RecyclerView)findViewById(R.id.myRecyclerView);
        mUserResponse.setHasFixedSize(true);
        mUserResponse.setLayoutManager(new LinearLayoutManager(this));
        setupFirebaseListener();
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(SAC_adminActivity.this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

   //  ================= Top right corner menu code======================
    public void AdminSignout(){
        Log.d("onClick","Signing out");
        FirebaseAuth.getInstance().signOut();
    }
    public void AcceptedSlots(){
        startActivity(new Intent(SAC_adminActivity.this,SAC_adminAccept.class));
        //Toast.makeText(AdminViewActivity.this,"Accepted Slots",Toast.LENGTH_SHORT).show();
    }
    public void RejectedSlots(){
        startActivity(new Intent(SAC_adminActivity.this,SAC_adminReject.class));

    }
    public void Feedback(){
        startActivity(new Intent(SAC_adminActivity.this,feedback.class));

    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_admin,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        switch (id){
            case R.id.signout:
                AdminSignout();
                break;
            case R.id.feedback:
                Feedback();
                break;
            case R.id.acceped:
                AcceptedSlots();
                break;
            case R.id.rejected:
                RejectedSlots();
                break;
        }

        return true;
    }

    //=================== check for online=====================

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
                    Toast.makeText(SAC_adminActivity.this, "Siging out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SAC_adminActivity.this,MainActivity.class);
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

        FirebaseRecyclerAdapter<SAC_response,UserResponseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<SAC_response, UserResponseViewHolder>
                (SAC_response.class,R.layout.sac_card, UserResponseViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(UserResponseViewHolder userViewHolder, SAC_response model, int position) {
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
        private Button acceptBtn, rejectBtn;
        private String mReason, slotchoosen,userName, fromTime,toTime,uDate, uTime, nextDateStr;
//        int projectorReqd, mikeReqd;
        Encryption encryption = new Encryption();

        public UserResponseViewHolder(View responseView)
        {
            super(responseView);
            mView=itemView;
            this.acceptBtn =(Button)responseView.findViewById(R.id.acceptButton);
            this.rejectBtn = (Button)responseView.findViewById(R.id.rejectButton);

            //Toast.makeText(itemView.getContext(),"KAAM KR RAHA HAI",Toast.LENGTH_SHORT).show();


            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Conformation");
                    builder.setMessage("Are you sure you want to Accept this request?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            final DatabaseReference firebaseUserResponse = FirebaseDatabase.getInstance().getReference("UserResponses_SAC").child(encryption.md5(userName));
                            final DatabaseReference firebaseUserResponseForAdmin = FirebaseDatabase.getInstance().getReference("SAC_bookings");
                            final DatabaseReference acceptRef = FirebaseDatabase.getInstance().getReference("SAC_AcceptRequests");
                            SAC_response sac_response = new SAC_response(mReason, userName, "Accepted",fromTime,toTime,uDate, uTime,nextDateStr);

                            String uid = encryption.md5(encryption.md5(userName)+fromTime+toTime);
                            firebaseUserResponse.child(uid).child("status").setValue("Accepted");

                            //==================cut and paste booking data fromm PandingRequest to AcceptRequests================
                            acceptRef.child(uid).setValue(sac_response);
                            firebaseUserResponseForAdmin.child(uid).removeValue();

                            acceptBtn.setVisibility(View.GONE);
                            Toast.makeText(itemView.getContext(),"Request Accepted",Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
//
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Conformation");
                    builder.setMessage("Are you sure you want to Reject this request?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            final DatabaseReference firebaseUserResponse = FirebaseDatabase.getInstance().getReference("UserResponses_SAC").child(encryption.md5(userName));
                            final DatabaseReference firebaseUserResponseForAdmin = FirebaseDatabase.getInstance().getReference("SAC_bookings");
                            final DatabaseReference rejectRef = FirebaseDatabase.getInstance().getReference("SAC_RejectRequests");
                            SAC_response sac_response = new SAC_response(mReason, userName, "Rejected",fromTime,toTime,uDate, uTime,nextDateStr);

                            String uid = encryption.md5(encryption.md5(userName)+fromTime+toTime);
                            firebaseUserResponse.child(uid).child("status").setValue("Rejected");

                            //==================cut and paste booking data fromm PandingRequest to RejectRequests================
                            rejectRef.child(uid).setValue(sac_response);
                            firebaseUserResponseForAdmin.child(uid).removeValue();

                            rejectBtn.setVisibility(View.GONE);
                            Toast.makeText(itemView.getContext()," Request Rejected",Toast.LENGTH_SHORT).show();

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }


        public void setReason(String reason){
            mReason = reason;
            TextView reasonReqdForBooking = (TextView)itemView.findViewById(R.id.reasonReqdForBooking);
            reasonReqdForBooking.setText(reason);
        }

        public void setUser(String user){
            userName = user;
            TextView userWhoBooked = (TextView)itemView.findViewById(R.id.userWhoBooked);
            userWhoBooked.setText(user);
        }

        public void setDay(String day){
            nextDateStr = day;
            TextView _day = (TextView)itemView.findViewById(R.id.day);
            _day.setText(day);
        }

        public void setFromTime(String fTime){
            fromTime = fTime;
            TextView _fromTime = (TextView)itemView.findViewById(R.id.fromTime);
            _fromTime.setText(fTime);
        }

        public void setToTime(String tTime){
            toTime = tTime;
            TextView _toTime = (TextView)itemView.findViewById(R.id.toTime);
            _toTime.setText(tTime);
        }

        public void setStatus(String status){
            //statusOfBooking = status;
            //card.setBackgroundColor(Color.parseColor("#80ff80"));
        }

        public void setuDate(String date){
            uDate = date;
        }

        public void setuTime(String time){
            uTime = time;
        }


    }

}
