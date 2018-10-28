package com.example.nimishgupta.mycollege;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class feedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar().setTitle("App Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button submit = (Button)findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText query = (EditText)findViewById(R.id.query);
                String message = query.getText().toString();
                SharedPreferences sp = feedback.this.getSharedPreferences("com.example.nimishgupta.mycollege",MODE_PRIVATE);
                String userName = sp.getString("userID","default");

                String uriText =
                        "mailto:developer.cpb@gmail.com" +
                                "?subject=" + Uri.encode("Query regarding CPB app") +
                                "&body=" + Uri.encode(message);

                Uri uri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                startActivity(Intent.createChooser(sendIntent, "Send email"));
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }
        // other menu select events may be present here

        return super.onOptionsItemSelected(item);
    }
}