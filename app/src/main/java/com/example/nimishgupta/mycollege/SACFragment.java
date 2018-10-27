package com.example.nimishgupta.mycollege;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class SACFragment extends Fragment {

    private TextView fromTextView, toTextView, fromSample, toSample ;
    private EditText reasonText;
    private Button submit, yourBookins;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((BottomNavigation) getActivity()).setActionBarTitle("SAC Booking");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sac, container, false);

        submit  = (Button) view.findViewById(R.id.submitBtn);

        Button fromTimeBtn = (Button)view.findViewById(R.id.fromTimeBtn);
        fromTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment  = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"from Time picker");

            }
        });

        Button toTimeBtn = (Button)view.findViewById(R.id.toTimeBtn);
        toTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment  = new TimePickerFragment2();
                newFragment.show(getFragmentManager()," to Time picker");
            }
        });

        fromTextView = (TextView)view.findViewById(R.id.fromTextView);
        toTextView = (TextView)view.findViewById(R.id.toTextView);

        fromSample = (TextView)view.findViewById(R.id.fromSample);
        toSample = (TextView)view.findViewById(R.id.toSample);
        reasonText = (EditText)view.findViewById(R.id.reason);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date today = new Date();
                SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                String dateToStr = date.format(today);
                String timeToStr = time.format(today);

                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, 1);
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                String nextDateStr = format1.format(c.getTime());

                String fromTime = fromTextView.getText().toString();
                String toTime = toTextView.getText().toString();
                String reason = reasonText.getText().toString();
                String fromSampleVar = fromSample.getText().toString();
                String toSampleVar = toSample.getText().toString();

                if(reason.equals("") || toTime.equals("") || fromTime.equals("")){
                    Toast.makeText(getActivity(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
                }else{
                    int from, to;
                    try{
                        from = Integer.parseInt(fromSampleVar);
                        to = Integer.parseInt(toSampleVar);

                        if(from >= to){
                            Toast.makeText(getActivity(),"Invalid Time interval" , Toast.LENGTH_SHORT).show();
                        }else {

                            SharedPreferences sp = getActivity().getSharedPreferences("com.example.nimishgupta.mycollege",MODE_PRIVATE);
                            String userName = sp.getString("userID","default");
                            Encryption encryption = new Encryption();
                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("SAC_bookings");
                            final DatabaseReference firebaseUserResponse = FirebaseDatabase.getInstance().getReference("UserResponses_SAC").child(encryption.md5(userName));
                            String id = encryption.md5(encryption.md5(userName)+fromTime+toTime);

                            SAC_response sacResponse = new SAC_response(reason,userName,"Pending",fromTime,toTime,dateToStr,timeToStr,nextDateStr);
                            firebaseUserResponse.child(id).setValue(sacResponse);
                            rootRef.child(id).setValue(sacResponse);
                            Toast.makeText(getActivity(), "Booking request has been submitted", Toast.LENGTH_LONG).show();

                        }
                    }catch(NumberFormatException ex){ // handle your exception

                    }
                }
            }
        });


        yourBookins = (Button)view.findViewById(R.id.yourBookings);
        yourBookins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),your_SAC_booking.class));
            }
        });


        return view;
    }

}
