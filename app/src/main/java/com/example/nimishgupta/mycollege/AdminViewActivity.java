package com.example.nimishgupta.mycollege;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
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
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        mDatabase=FirebaseDatabase.getInstance().getReference().child("PendingRequests");
        mDatabase.keepSynced(true);
        mUserResponse=(RecyclerView)findViewById(R.id.myRecyclerView);
        mUserResponse.setHasFixedSize(true);
        mUserResponse.setLayoutManager(new LinearLayoutManager(this));
        setupFirebaseListener();
    }

    // ================= Top right corner menu code======================
    public void AdminSignout(){
        Log.d("onClick","Signing out");
        FirebaseAuth.getInstance().signOut();
    }
    public void AcceptedSlots(){
        startActivity(new Intent(AdminViewActivity.this,AdminAcceptRequest.class));
        //Toast.makeText(AdminViewActivity.this,"Accepted Slots",Toast.LENGTH_SHORT).show();
    }
    public void RejectedSlots(){
        startActivity(new Intent(AdminViewActivity.this,AdminRejectRequest.class));
        //Toast.makeText(AdminViewActivity.this,"Rejected Slots",Toast.LENGTH_SHORT).show();

    }
    public void PendingSlots(){
        Toast.makeText(AdminViewActivity.this,"Pending Slots",Toast.LENGTH_SHORT).show();

    }
    public void Feedback(){
        Toast.makeText(AdminViewActivity.this,"Coming soon",Toast.LENGTH_SHORT).show();

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
            case R.id.pending:
                PendingSlots();
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
                    Intent intent = new Intent(AdminViewActivity.this,MainActivity.class);
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
        private String mReason, slotchoosen,userName, dateToStr,timeToStr, roomType,daySlot, whatYoyBooked, nextDateStr;
        int projectorReqd, mikeReqd;
        Encryption encryption = new Encryption();

        public UserResponseViewHolder(View responseView)
        {
            super(responseView);
            mView=itemView;
            this.acceptBtn =(Button)responseView.findViewById(R.id.acceptButton);
            this.rejectBtn = (Button)responseView.findViewById(R.id.rejectButton);

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatabaseReference firebaseUserResponse = FirebaseDatabase.getInstance().getReference("UserResponses").child(encryption.md5(userName));
                    final DatabaseReference firebaseUserResponseForAdmin = FirebaseDatabase.getInstance().getReference("PendingRequests");
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

            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DatabaseReference firebaseUserResponse = FirebaseDatabase.getInstance().getReference("UserResponses").child(encryption.md5(userName));
                    final DatabaseReference firebaseUserResponseForAdmin = FirebaseDatabase.getInstance().getReference("PendingRequests");
                    final DatabaseReference rejectRef = FirebaseDatabase.getInstance().getReference("RejectRequests");
                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference(roomType).child(whatYoyBooked).child(daySlot).child(slotchoosen);
                    UserResponse userResponse = new UserResponse(mReason, slotchoosen, userName, mikeReqd, projectorReqd,whatYoyBooked ,"Rejected",dateToStr,timeToStr,nextDateStr,daySlot);

                    String id = encryption.md5(encryption.md5(userName)+dateToStr+timeToStr);
                    firebaseUserResponse.child(id).child("status").setValue("Rejected");

                    //==================cut and paste booking data fromm PandingRequest to RejectRequests================
                    rejectRef.child(id).setValue(userResponse);
                    firebaseUserResponseForAdmin.child(id).removeValue();
                    rootRef.setValue("A");

                    rejectBtn.setVisibility(View.GONE);
                    Toast.makeText(itemView.getContext()," Request Rejected",Toast.LENGTH_SHORT).show();

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