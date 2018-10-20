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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminRejectRequest extends AppCompatActivity {

    private RecyclerView mUserResponse;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        mDatabase=FirebaseDatabase.getInstance().getReference().child("RejectRequests");
        mDatabase.keepSynced(true);
        mUserResponse=(RecyclerView)findViewById(R.id.myRecyclerView);
        mUserResponse.setHasFixedSize(true);
        mUserResponse.setLayoutManager(new LinearLayoutManager(this));
        setupFirebaseListener();
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
                    Toast.makeText(AdminRejectRequest.this, "Siging out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminRejectRequest.this,MainActivity.class);
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
        FirebaseRecyclerAdapter<UserResponse,AdminRejectRequest.UserResponseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserResponse, AdminRejectRequest.UserResponseViewHolder>
                (UserResponse.class,R.layout.activity_admin_reject_request, AdminRejectRequest.UserResponseViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(AdminRejectRequest.UserResponseViewHolder userViewHolder, UserResponse model, int position) {
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
        private Button acceptBtn, rejectBtn;
        private String mReason, slotchoosen,userName,dateToStr,timeToStr, roomType,daySlot, whatYoyBooked, nextDateStr;
        int projectorReqd, mikeReqd;
        Encryption encryption = new Encryption();

        public UserResponseViewHolder(View responseView)
        {
            super(responseView);
            mView=itemView;
            this.acceptBtn =(Button)responseView.findViewById(R.id.acceptButton);

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatabaseReference firebaseUserResponse = FirebaseDatabase.getInstance().getReference("UserResponses").child(encryption.md5(userName));
                    final DatabaseReference firebaseUserResponseForAdmin = FirebaseDatabase.getInstance().getReference("RejectRequests");
                    final DatabaseReference acceptRef = FirebaseDatabase.getInstance().getReference("AcceptRequests");
                    UserResponse userResponse = new UserResponse(mReason, slotchoosen, userName, mikeReqd, projectorReqd,whatYoyBooked ,"Accepted",dateToStr,timeToStr,nextDateStr,daySlot);

                    String id = encryption.md5(encryption.md5(userName)+dateToStr+timeToStr);
                    firebaseUserResponse.child(id).child("status").setValue("Accepted");

                    //==================cut and paste booking data fromm PandingRequest to AcceptRequests================
                    acceptRef.child(id).setValue(userResponse);
                    firebaseUserResponseForAdmin.child(id).removeValue();

                    acceptBtn.setVisibility(View.GONE);
                    Toast.makeText(itemView.getContext(),"Request Accepted",Toast.LENGTH_SHORT).show();
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

        public void setSlot(String slot){
            slotchoosen = slot;
            TextView slotBooked = (TextView)itemView.findViewById(R.id.userReqdslot);
            slotBooked.setText(slot);
        }

        public void setProjector(int projector){
            projectorReqd = projector;
            TextView projector_ = (TextView)itemView.findViewById(R.id.ifProjectorReqd);
            projector_.setText(String.valueOf(projector));
        }

        public void setMike(int mike){
            mikeReqd = mike;
            TextView mike_ = (TextView)itemView.findViewById(R.id.ifMikeReqd);
            mike_.setText(String.valueOf(mike));
        }

        public void setStatus(String status){
            //statusOfBooking = status;
            //card.setBackgroundColor(Color.parseColor("#80ff80"));
        }

        public void setNumber(String number ){
            whatYoyBooked = number;
            if(number.substring(0,3).equals("Lab")){
                roomType = "CPLabs";
            }else {
                roomType = "LTs";
            }
            TextView  number_= (TextView)itemView.findViewById(R.id.whatBooked);
            number_.setText(String.valueOf(number));
        }

        public void setuDate(String date1){
            dateToStr = date1;
        }

        public void setuTime(String time1){
            timeToStr = time1;
        }

        public void setuNextDate(String date2){
            nextDateStr = date2;
            TextView date = (TextView)itemView.findViewById(R.id.forBooked);
            date.setText(String.valueOf(date2));
        }

        public  void setDaySlot(String dayTime){
            daySlot = dayTime;
        }
    }
}
