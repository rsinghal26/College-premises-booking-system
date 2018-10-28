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
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;


public class DeveloperFragment extends Fragment {

    private RecyclerView mUserSide;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Encryption encryption = new Encryption();
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((BottomNavigation) getActivity()).setActionBarTitle("Your LT's/LAB's Booking");

        if(isOnline()) {

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Fetching Data...");
            progressDialog.show();
            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            };
            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 2000);
        }
        else{
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();

                alertDialog.setTitle("Warning!!");
                alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again.");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.show();
            } catch (Exception e) {
//                Log.d(Constants.TAG, "Show Dialog: " + e.getMessage());
            }
        }

        // Inflate the layout for this fragment
        SharedPreferences sp = getContext().getSharedPreferences("com.example.nimishgupta.mycollege",MODE_PRIVATE);
        String userName = sp.getString("userID","default");

        View view = inflater.inflate(R.layout.fragment_developer, container, false);
        mDatabase=FirebaseDatabase.getInstance().getReference().child("UserResponses").child(encryption.md5(userName));
        mUserSide=(RecyclerView)view.findViewById(R.id.myRecyclerView);
        mUserSide.setHasFixedSize(true);
        mUserSide.setLayoutManager(new LinearLayoutManager(getContext()));
        setupFirebaseListener();

        return view;
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "Siging out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sp = this.getActivity().getSharedPreferences("com.example.nimishgupta.mycollege",MODE_PRIVATE);
        final String userName = sp.getString("userID","default");

        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<UserResponse,DeveloperFragment.UserResponseViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserResponse, UserResponseViewHolder>
                (UserResponse.class,R.layout.user_side_card, DeveloperFragment.UserResponseViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(DeveloperFragment.UserResponseViewHolder userViewHolder, UserResponse model, int position) {

                userViewHolder.setReason(model.getReason());
                userViewHolder.setSlot(model.getSlotChoosen());
                userViewHolder.setMike(model.getMike());
                userViewHolder.setProjector(model.getProjector());
                userViewHolder.setNumber(model.getWhatBooked());
                userViewHolder.setStatus(model.getStatus());
                userViewHolder.setuDate(model.getuDate());
                userViewHolder.setuTime(model.getuTime());
                userViewHolder.setuNextDate(model.getuNextDate());
            }
        };
        mUserSide.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    public static class UserResponseViewHolder extends RecyclerView.ViewHolder
    {
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
        public void setNumber(String number ){
            TextView  number_= (TextView)itemView.findViewById(R.id.whatBooked);
            number_.setText(String.valueOf(number));
        }
        public void setuDate(String date1){
            TextView date = (TextView)itemView.findViewById(R.id.dateBooked);
            date.setText(String.valueOf(date1));
        }
        public void setuTime(String time1){
            TextView time = (TextView)itemView.findViewById(R.id.timeBooked);
            time.setText(String.valueOf(time1));
        }
        public void setuNextDate(String date2){
            TextView date = (TextView)itemView.findViewById(R.id.forBooked);
            date.setText(String.valueOf(date2));
        }
    }
}
