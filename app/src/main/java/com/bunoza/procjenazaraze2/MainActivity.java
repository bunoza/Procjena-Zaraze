package com.bunoza.procjenazaraze2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bunoza.procjenazaraze2.db.AppDatabase;
import com.bunoza.procjenazaraze2.inital.ScrollingActivity;
import com.bunoza.procjenazaraze2.repo.Repository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    static AppDatabase db;
    Repository repo;


    private final String TAG = "MainActivity";
    private Button btnContinue;
    private final int REQUEST_CODE_SECOND_ACTIVITY = 100;




    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repo = Repository.getInstance(getApplicationContext());
        if(repo.isDBnull()){
            repo.initDB(getApplicationContext());
        }
        if(repo.getUserCount() != 0 ) {
                setContentView(R.layout.activity_main);
                initUI();
        }else{
            setContentView(R.layout.activity_welcome_screen);
            initWelcomeUI();
        }
    }

    private void initWelcomeUI() {
        btnContinue = findViewById(R.id.btnNastavi);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOldAndAddNewUser();
            }
        });
    }

    private void deleteOldAndAddNewUser(){
        Intent intent = new Intent(MainActivity.this, ScrollingActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SECOND_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SECOND_ACTIVITY){
            if (resultCode == RESULT_OK) {
                setContentView(R.layout.activity_main);
                initUI();
            }
        }

    }

    private void initUI(){
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.fragment_home, R.id.fragment_covid, R.id.fragment_location, R.id.settingsFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}