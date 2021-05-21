package com.bunoza.procjenazaraze2.ui.home;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.bunoza.procjenazaraze2.MainActivity;
import com.bunoza.procjenazaraze2.R;
import com.bunoza.procjenazaraze2.model.User;
import com.bunoza.procjenazaraze2.repo.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private static final String TAG = "HOMEFRAGMENT";
    TextView location, covidHR, covidZUP, procjena;
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Repository repo = Repository.getInstance();


        location = view.findViewById(R.id.text_home);
        covidHR = view.findViewById(R.id.tvCovidDanasHR);
        covidZUP = view.findViewById(R.id.tvCovidDanasZUP);
        procjena = view.findViewById(R.id.procjena);

        homeViewModel.getApproximation().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                procjena.setText(String.format("%.2f%%", aDouble));
            }
        });
        homeViewModel.getLastLocation().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                location.setText(s);
                Log.d(TAG, "onChanged: " + s);
            }
        });
        homeViewModel.getCovidHr().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                covidHR.setText("Broj slučajeva danas: " + integer.toString());
            }
        });
        homeViewModel.getCovidZup().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                covidZUP.setText("Broj slučajeva danas u županiji " + (getResources().getStringArray(R.array.zupanije))[Integer.parseInt(sharedPreferences.getString("zupanija", "10"))] + ": " + integer.toString());
            }
        });
        homeViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                ((MainActivity) getActivity()).getSupportActionBar().setTitle("Dobar dan, " + user.ime);
            }
        });
        repo.storeData(Integer.parseInt(sharedPreferences.getString("zupanija", "10")));
//        homeViewModel.checkTimestamps();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        homeViewModel.repo.getData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.checkTimestamps();
    }
}