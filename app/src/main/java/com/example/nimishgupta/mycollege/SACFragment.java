package com.example.nimishgupta.mycollege;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;



public class SACFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((BottomNavigation) getActivity()).setActionBarTitle("SAC Booking");

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_sac, container, false);
        Button button = (Button)view.findViewById(R.id.button_sac);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Clicked",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}