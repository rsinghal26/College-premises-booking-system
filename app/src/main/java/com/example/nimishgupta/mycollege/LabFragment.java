package com.example.nimishgupta.mycollege;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class LabFragment extends Fragment {

    private Spinner spinner1, spinner;
    
    private String spinnerLabNumber, spinnerLabSlot;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((BottomNavigation) getActivity()).setActionBarTitle("Lab Booking");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lab, container, false);
        //Lab Numbers
        Button searchButton = (Button) v.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ei = new Intent(getActivity(),freeSlotActivity.class);
                startActivity(ei);
            }
        });
        spinner = (Spinner) v.findViewById(R.id.spinner_toChooseLabNo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                                                    android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.lab_number));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Time slot of LAB
        spinner1 = (Spinner) v.findViewById(R.id.spinner_toChooseTimeSlotofLab);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.timeSlotOfLab));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        //TO get selected values from Spnners!!
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerLabNumber = spinner.getSelectedItem().toString();
                spinnerLabSlot = spinner1.getSelectedItem().toString();
                Intent ei = new Intent(getActivity(),freeSlotActivity.class);
                ei.putExtra("type","LAB");
                ei.putExtra("labNumber",spinnerLabNumber);
                ei.putExtra("labSlot",spinnerLabSlot);
                startActivity(ei);
            }
        });

        return v;
    }
}
