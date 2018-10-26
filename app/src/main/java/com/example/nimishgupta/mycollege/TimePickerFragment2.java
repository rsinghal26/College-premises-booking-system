package com.example.nimishgupta.mycollege;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment2 extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    public TimePickerFragment2() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        TextView tv = (TextView) getActivity().findViewById(R.id.toTextView);
        String aMpM = "AM";
        if(hourOfDay >11) {
            aMpM = "PM";
        }
        int currentHour;
        if(hourOfDay>11) {
            currentHour = hourOfDay - 12;
        } else {
            currentHour = hourOfDay;
        }
        tv.setText("");
        tv.setText(tv.getText()+ String.valueOf(currentHour)
                + " : " + String.valueOf(minute) + " " + aMpM );

    }
}
