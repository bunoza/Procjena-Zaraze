package com.bunoza.procjenazaraze2.ui.covid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bunoza.procjenazaraze2.R;
import com.bunoza.procjenazaraze2.model.CovidDB;

public class CovidFragment extends Fragment {

    private CovidViewModel covidViewModel;
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isConnected;


    TextView tvHrSlucajevi, tvHrIzlijeceni, tvHrUmrli, tvZupSlucajevi, tvZupIzlijeceni, tvZupUmrli;
    Button details;
    Spinner zupanijeSpinner;
    private final String TAG = "COVIDFRAGMENT";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        covidViewModel = new ViewModelProvider(this).get(CovidViewModel.class);


        return inflater.inflate(R.layout.fragment_covid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);


        covidViewModel.getRepository().getShouldSetRefreshFalse().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(getActivity() != null){
                    ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();
                }
                if(isConnected) {
                    covidViewModel.getRepository().getData();
                }else{
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "Nemoguć pristup internetu", Toast.LENGTH_SHORT).show();
                }
            }
        });





        covidViewModel.getRepository().getLatestCases().observe(getViewLifecycleOwner(), new Observer<CovidDB>() {
            @Override
            public void onChanged(CovidDB covidDB) {
                if(covidDB != null){
                    tvHrSlucajevi.setText(String.format(getString(R.string.slucajevi_u_hr), covidDB.hr_zarazeni));
                    tvHrIzlijeceni.setText(String.format(getString(R.string.ozdravljeni_hr), covidDB.hr_izlijeceni));
                    tvHrUmrli.setText(String.format(getString(R.string.umrli_hr), covidDB.hr_umrli));
                    tvZupSlucajevi.setText(String.format(getString(R.string.zarazeni_obz), covidDB.zup_zarazeni));
                    tvZupIzlijeceni.setText(String.format(getString(R.string.ozdravljeni_obz), covidDB.zup_izlijeceni));
                    tvZupUmrli.setText(String.format(getString(R.string.umrli_obz), covidDB.zup_umrli));
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initUI(View view) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        tvHrSlucajevi = view.findViewById(R.id.tvCovidHrNovi);
        tvHrIzlijeceni = view.findViewById(R.id.tvCovidHrIzlijeceni);
        tvHrUmrli = view.findViewById(R.id.tvCovidHrUmrli);
        tvZupSlucajevi = view.findViewById(R.id.tvCovidZupanijaZarazeni);
        tvZupIzlijeceni = view.findViewById(R.id.tvCovidZupanijaIzlijeceni);
        tvZupUmrli = view.findViewById(R.id.tvCovidZupanijaUmrli);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        details = view.findViewById(R.id.btnWeb);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.koronavirus.hr/podaci/489"));
                startActivity(browserIntent);
            }
        });

        zupanijeSpinner = view.findViewById(R.id.spinnerZupanija);
        zupanijeSpinner.setSelection(Integer.parseInt(sharedPreferences.getString("zupanija", "10")));
        zupanijeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                covidViewModel.notifySpinnerChanged(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}