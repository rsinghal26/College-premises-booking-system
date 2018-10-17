package com.example.nimishgupta.mycollege;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminViewActivity extends AppCompatActivity {

    private RecyclerView mUserResponse;
    private DatabaseReference mDatabase;

    ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Button signout;//, acceptBtn, rejectBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mDatabase=FirebaseDatabase.getInstance().getReference().child("UserResponses");
        setContentView(R.layout.activity_admin_view);
//        setContentView(R.layout.blog_row);
        mDatabase.keepSynced(true);
        mUserResponse=(RecyclerView)findViewById(R.id.myRecyclerView);
        mUserResponse.setHasFixedSize(true);
        mUserResponse.setLayoutManager(new LinearLayoutManager(this));
//        acceptBtn = (Button)findViewById(R.id.acceptButton);
//        rejectBtn = (Button)findViewById(R.id.rejectButton);
        signout = (Button)findViewById(R.id.adminSignout);
        progressBar = (ProgressBar)findViewById(R.id.adminSignoutProgressBar);
        setupFirebaseListener();
        signout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d("onClick","Signing out");
                FirebaseAuth.getInstance().signOut();
            }
        });
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
                    Toast.makeText(AdminViewActivity.this, "Siging out", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(AdminViewActivity.this,MainActivity.class);
                    progressBar.setVisibility(View.INVISIBLE);
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
        FirebaseRecyclerAdapter<UserResponse,UserResponseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserResponse, UserResponseViewHolder>
                (UserResponse.class,R.layout.blog_row, UserResponseViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(UserResponseViewHolder userViewHolder, UserResponse model, int position) {
                userViewHolder.setReason(model.getReason());
                userViewHolder.setSlot(model.getSlotChoosen());
                userViewHolder.setMike(model.getMike());
                userViewHolder.setProjector(model.getProjector());
                userViewHolder.setUser(model.getUserName());
                userViewHolder.setNumber(model.getWhatBooked());
                userViewHolder.setStatus(model.getStatus());
//                userViewHolder.
            }
        };

        mUserResponse.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UserResponseViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Button acceptBtn;

        public UserResponseViewHolder(View responseView)
        {
            super(responseView);
            mView=itemView;
            this.acceptBtn =(Button)responseView.findViewById(R.id.acceptButton);
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(AdminViewActivity.this,"Kaam kr raha hai",Toast.LENGTH_SHORT).show();
                    
                }
            });
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
            TextView status_ = (TextView)itemView.findViewById(R.id.ifMikeReqd);
            //status_.setText(String.valueOf(status));
        }
        public void setNumber(String number ){
            TextView  number_= (TextView)itemView.findViewById(R.id.whatBooked);
            number_.setText(String.valueOf(number));
        }
    }


}