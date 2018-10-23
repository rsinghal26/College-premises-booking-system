package com.example.nimishgupta.mycollege;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class feedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar().setTitle("App Feedback");


        Button submit = (Button)findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText query = (EditText)findViewById(R.id.query);
                String message = query.getText().toString();
                SharedPreferences sp = feedback.this.getSharedPreferences("com.example.nimishgupta.mycollege",MODE_PRIVATE);
                String userName = sp.getString("userID","default");

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL,new String[] {"16ucs151@lnmiit.ac.in"});
                email.putExtra(Intent.EXTRA_SUBJECT,"Query regarding CPB app from " + userName);
                email.putExtra(Intent.EXTRA_TEXT,message);

                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose app to send email"));
            }
        });
    }
}