package com.example.nimishgupta.mycollege;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    private Button msignupBtn;
    private FirebaseAuth mfirebaseAuth;
    private EditText email, pwd, cnfpwd;
    private ProgressDialog progressDialog;
    private TextView signinTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        msignupBtn = (Button)findViewById(R.id.submitButtonsignup);
        email = (EditText)findViewById(R.id.signupEmail);
        signinTextView = (TextView) findViewById(R.id.clicksignin);
        pwd = (EditText)findViewById(R.id.signupPwd);
        cnfpwd = (EditText)findViewById(R.id.signcnfpwd);
        mfirebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(SignUp.this);

        signinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this,MainActivity.class));
                finish();
            }
        });


        msignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String memail = email.getText().toString();
                    String mpwd = pwd.getText().toString();
                    String mcnfpwd = cnfpwd.getText().toString();

                    if(memail.isEmpty() || mpwd.isEmpty() || mcnfpwd.isEmpty()){
                        Toast.makeText(SignUp.this,"Please enter all the details!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        StringBuffer buffer = new StringBuffer(memail);
                        buffer.reverse();

                        if(buffer.substring(0,13).equals("ni.ca.tiimnl@")){
                            if(mpwd.equals(mcnfpwd)){
                                progressDialog.setMessage("Registering User...");
                                progressDialog.show();
                                mfirebaseAuth.createUserWithEmailAndPassword(memail, mpwd)
                                        .addOnCompleteListener(SignUp.this,new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    sendEmailVerification();
                                                }
                                                else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SignUp.this,"Registration Failed",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(SignUp.this,"Passwords don't match",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(SignUp.this,"Please use lnmiit id",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
            }
        });

    }


    private void sendEmailVerification(){
        final FirebaseUser firebaseUser = mfirebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUp.this,"Verification link has been sent, click to register.",Toast.LENGTH_SHORT).show();
                        mfirebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(SignUp.this,MainActivity.class));
                    }
                    else {
                        Toast.makeText(SignUp.this, "Verification mail hasn't been send.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
