package com.example.nimishgupta.mycollege;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText getEmail, getPwd;
    Button signButton, msignup;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getEmail = (EditText) findViewById(R.id.email);
        getPwd = (EditText) findViewById(R.id.password);
        signButton = (Button) findViewById(R.id.loginButton);
        msignup = (Button)findViewById(R.id.signupBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(MainActivity.this);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user !=null){
            finish();
            String loggedInUser = user.getEmail().toString();
            Toast.makeText(MainActivity.this,"Logged in as "+loggedInUser,Toast.LENGTH_SHORT).show();
            if(loggedInUser.equals("rjt1447@gmail.com")){
                startActivity(new Intent(MainActivity.this,AdminViewActivity.class));
            }else {
                startActivity(new Intent(MainActivity.this,BottomNavigation.class));
            }
        }

        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail, mPwd;
                mEmail = getEmail.getText().toString();
                mPwd = getPwd.getText().toString();
                if(mEmail.isEmpty() || mPwd.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter all details",Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                    Validate(mEmail,mPwd);
            }
        });
        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUp.class));
            }
        });
    }
    private void Validate(String username, String password){
        progressDialog.setMessage("Logged In...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    checkEmailVerified();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Login Problem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkEmailVerified(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean emailFlag = firebaseUser.isEmailVerified();
        String loggedInUser = firebaseUser.getEmail().toString();

        Toast.makeText(MainActivity.this,"Logged in as "+loggedInUser,Toast.LENGTH_SHORT).show();
        if (emailFlag){
            finish();
            sharedPreferences = this.getSharedPreferences("com.example.nimishgupta.mycollege",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userID",loggedInUser);
            editor.apply();

            //String userName = sharedPreferences.getString("userID","default");
            //Toast.makeText(MainActivity.this,"share data "+userName,Toast.LENGTH_SHORT).show();
            if(loggedInUser.equals("rjt1447@gmail.com")){
                startActivity(new Intent(MainActivity.this,AdminViewActivity.class));
            }else {
                startActivity(new Intent(MainActivity.this,BottomNavigation.class));
            }

        }
        else {
            Toast.makeText(MainActivity.this,"Verify Your Email",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }


}

