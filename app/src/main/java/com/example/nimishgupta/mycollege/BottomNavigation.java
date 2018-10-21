package com.example.nimishgupta.mycollege;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;

public class BottomNavigation extends AppCompatActivity {
    private Toolbar mToolBar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_LT:
                    transaction.replace(R.id.content,new LectureFragment()).commit();
                    return true;
                case R.id.navigation_Labs:
                    transaction.replace(R.id.content,new LabFragment()).commit();
                    return true;
                case R.id.navigation_SAC:
                    transaction.replace(R.id.content,new SACFragment()).commit();
                    return true;
                case R.id.navigation_developers:
                    transaction.replace(R.id.content,new DeveloperFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content,new LectureFragment()).commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    // ================= Top right corner menu code======================
    public void UserSignout(){
        Log.d("onClick","Signing out");
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(BottomNavigation.this, "Siging out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BottomNavigation.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void Feedback(){
        startActivity(new Intent(BottomNavigation.this,feedback.class));

    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_user,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        switch (id){
            case R.id.signout:
                UserSignout();
                break;
            case R.id.feedback:
                Feedback();
                break;
        }
        return true;
    }

}