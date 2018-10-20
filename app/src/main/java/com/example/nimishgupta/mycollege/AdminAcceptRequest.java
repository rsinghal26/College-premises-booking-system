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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminAcceptRequest extends AppCompatActivity {

    private RecyclerView mUserResponse;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
//    Date today = new Date();
//    SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
//    String newDateToStr = date.format(today);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_accept_view);

        if(isOnline()) {
        }
        else{
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(AdminAcceptRequest.this).create();
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

        mDatabase=FirebaseDatabase.getInstance().getReference().child("AcceptRequests");
        mDatabase.keepSynced(true);
        mUserResponse=(RecyclerView)findViewById(R.id.myRecyclerView);
        mUserResponse.setHasFixedSize(true);
        mUserResponse.setLayoutManager(new LinearLayoutManager(this));
        setupFirebaseListener();
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(AdminAcceptRequest.this, "No Internet connection!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(AdminAcceptRequest.this, "Siging out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminAcceptRequest.this,MainActivity.class);
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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<UserResponse,AdminAcceptRequest.UserResponseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserResponse, AdminAcceptRequest.UserResponseViewHolder>
                (UserResponse.class,R.layout.accept_card, AdminAcceptRequest.UserResponseViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(AdminAcceptRequest.UserResponseViewHolder userViewHolder, UserResponse model, int position) {
//                if(!(model.getuDate().equals(newDateToStr))){
//                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("LTs").child(model.getWhatBooked()).child(model.getDaySlot()).child(model.getSlotChoosen());
//                    rootRef.setValue("A");
//                    Toast.makeText(AdminAcceptRequest.this,newDateToStr,Toast.LENGTH_SHORT).show();
//                }
                userViewHolder.setReason(model.getReason());
                userViewHolder.setSlot(model.getSlotChoosen());
                userViewHolder.setMike(model.getMike());
                userViewHolder.setProjector(model.getProjector());
                userViewHolder.setUser(model.getUserName());
                userViewHolder.setNumber(model.getWhatBooked());
                userViewHolder.setStatus(model.getStatus());
                userViewHolder.setuNextDate(model.getuNextDate());
                userViewHolder.setuDate(model.getuDate());
                userViewHolder.setuTime(model.getuTime());
                userViewHolder.setDaySlot(model.getDaySlot());

            }
        };
        mUserResponse.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UserResponseViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserResponseViewHolder(View responseView)
        {
            super(responseView);
            mView=itemView;
        }

        public void setReason(String reason){

            TextView reasonReqdForBooking = (TextView)itemView.findViewById(R.id.reasonReqdForBooking);
            reasonReqdForBooking.setText(reason);
        }

        public void setUser(String user){

            TextView userWhoBooked = (TextView)itemView.findViewById(R.id.userWhoBooked);
            userWhoBooked.setText(user);
        }

        public void setSlot(String slot){

            TextView slotBooked = (TextView)itemView.findViewById(R.id.userReqdslot);
            slotBooked.setText(slot);
        }

        public void setProjector(int projector){

            TextView projector_ = (TextView)itemView.findViewById(R.id.ifProjectorReqd);
            projector_.setText(String.valueOf(projector));
        }

        public void setMike(int mike){

            TextView mike_ = (TextView)itemView.findViewById(R.id.ifMikeReqd);
            mike_.setText(String.valueOf(mike));
        }

        public void setStatus(String status){
            //statusOfBooking = status;
            //card.setBackgroundColor(Color.parseColor("#80ff80"));
        }

        public void setNumber(String number ){

            TextView number_= (TextView)itemView.findViewById(R.id.whatBooked);
            number_.setText(String.valueOf(number));
        }

        public void setuDate(String date1){

        }

        public void setuTime(String time1){

        }

        public void setuNextDate(String date2){

            TextView date = (TextView)itemView.findViewById(R.id.forBooked);
            date.setText(String.valueOf(date2));
        }

        public  void setDaySlot(String dayTime){

        }
    }
}
