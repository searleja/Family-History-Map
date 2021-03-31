package com.example.familymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Toolbar;

import com.example.familymap.login.LoginFragment;

public class MainActivity extends AppCompatActivity {

    public static final String ACTIVITY_NAME = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = new LoginFragment();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit();


    }

    //use fragment manager for login here
}