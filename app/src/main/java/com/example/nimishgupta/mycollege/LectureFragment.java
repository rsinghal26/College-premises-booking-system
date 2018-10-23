package com.example.nimishgupta.mycollege;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.app.Fragment;



public class LectureFragment extends Fragment {

    private Spinner spinner1, spinner;
    boolean doubleBackToExitPressedOnce = false;
    private Button searchButton;
    private String spinnerSize, spinnerSlot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((BottomNavigation) getActivity()).setActionBarTitle("Lecture Hall Booking");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lecture, container, false);
        searchButton = (Button) v.findViewById(R.id.searchButton);
        //First spinner for Size OF LT
        spinner = (Spinner) v.findViewById(R.id.spinner_toChooseLTsize);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1,getResources().getStringArray( R.array.lectureHallSize));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Spinner for Time Slot
        spinner1 = (Spinner)v.findViewById(R.id.spinner_toChooseTimeSlot);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.timeSlotofLT));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerSize = spinner.getSelectedItem().toString();
                spinnerSlot = spinner1.getSelectedItem().toString();
                Intent ei = new Intent(getActivity(),freeSlotActivity.class);
                ei.putExtra("type","LT");
                ei.putExtra("LTNumber",spinnerSize);
                ei.putExtra("LTSlot",spinnerSlot);
                startActivity(ei);

            }
        });
        return v;

    }
}