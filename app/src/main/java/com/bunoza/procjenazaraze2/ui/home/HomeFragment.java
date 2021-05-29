package com.bunoza.procjenazaraze2.ui.home;

import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.bunoza.procjenazaraze2.model.Approximation;
import com.bunoza.procjenazaraze2.model.User;
import com.bunoza.procjenazaraze2.repo.Repository;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private static final String TAG = "HOMEFRAGMENT";
    TextView location, covidHR, covidZUP, procjena, prosjek, preporuka;
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
        prosjek = view.findViewById(R.id.tvProsjek);
        preporuka = view.findViewById(R.id.tvPreporuka);

        homeViewModel.getApproximation().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                procjena.setText(String.format("%.2f%%", aDouble));
                setValidColor(aDouble);
            }
        });
        homeViewModel.lastApproximations.observe(getViewLifecycleOwner(), new Observer<List<Approximation>>() {
            @Override
            public void onChanged(List<Approximation> approximations) {
                Double sum = 0.0;
                for(int i = 0; i < approximations.size(); i++){
                    sum += approximations.get(i).value;
                }
                if(approximations.size() == 1){
                    prosjek.setText(String.format("Prosjek procjene zaraze unazad %d dan: %.2f%%", approximations.size(), sum / approximations.size()));
                }else{
                    prosjek.setText(String.format("Prosjek procjene zaraze unazad %d dana: %.2f%%",approximations.size(), sum/approximations.size()));
                }
                if(sum / approximations.size() < 15){
                    preporuka.setText(getResources().getString(R.string.nizak_prosjek));
                    preporuka.setTextColor(Color.rgb(0,200,0));
                }else if( sum/approximations.size() < 40){
                    preporuka.setText(getResources().getString(R.string.medium_prosjek));
                    procjena.setTextColor(Color.rgb(255,165,0));
                }else{
                    preporuka.setText(getResources().getString(R.string.high_prosjek));
                    procjena.setTextColor(Color.RED);
                }
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

    private void setValidColor(Double num) {
        if(num < 15){
            procjena.setTextColor(Color.rgb(0,200,0));
        }else if(num < 40){
            procjena.setTextColor(Color.rgb(255,165,0));
        }else {
            procjena.setTextColor(Color.RED);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.repo.getData();
        homeViewModel.checkTimestamps();
    }
}