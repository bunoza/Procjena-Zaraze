package com.bunoza.procjenazaraze2.ui.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.bunoza.procjenazaraze2.MainActivity;
import com.bunoza.procjenazaraze2.R;
import com.bunoza.procjenazaraze2.inital.ScrollingActivity;
import com.bunoza.procjenazaraze2.location_provider.GoogleService;
import com.bunoza.procjenazaraze2.model.LocationsModel;
import com.bunoza.procjenazaraze2.model.User;
import com.bunoza.procjenazaraze2.repo.Repository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.LOCATION_SERVICE;

public class SettingsFragment extends PreferenceFragmentCompat {

    private final int REQUEST_CODE_SECOND_ACTIVITY = 100;
    private final String TAG = "PreferenceFragment";
    public static final String MY_PREFS_NAME = "prefs";
    boolean boolean_permission;
    private static final int REQUEST_PERMISSIONS = 100;
    ListPreference interval;
    Repository repo;
    Intent intent;
    LocationManager locationManager;
    Preference delete;
    Preference profil;
    ListPreference deleteInterval;
    boolean isGPSEnable = false;
    private SwitchPreferenceCompat gpsSwitch;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        gpsSwitch = getPreferenceManager().findPreference("gpsSwitch");
        interval = getPreferenceManager().findPreference("interval");
        delete = getPreferenceManager().findPreference("delete");
        profil = getPreferenceScreen().findPreference("profil");
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        repo = Repository.getInstance();
        intent = new Intent(getActivity(), GoogleService.class);
        Log.d(TAG, "onCreatePreferences: " + boolean_permission);
        fn_permission();
        Log.d(TAG, "onCreatePreferences: " + boolean_permission);


        delete.setOnPreferenceClickListener(preference -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Želite obrisati lokacije?")
                    .setMessage("Obrisani podaci ne mogu se vratiti.")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> repo.
                            deleteLocationData())
                    .setNegativeButton("Odustani", null)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .show();
            return false;
        });

        profil.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), ScrollingActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SECOND_ACTIVITY);
                return false;
            }
        });

        interval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d(TAG, "onPreferenceChange: " + newValue);
                if(gpsSwitch.isChecked()) {
                    getActivity().stopService(intent);
                    gpsSwitch.setChecked(false);
                    return true;
                }
                return true;
            }
        });

        gpsSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(newValue.equals(true)){
                if(getBooleanPermission() ){
                    if(isGPSEnable){
                        if (getActivity() != null) {
                            gpsSwitch.setChecked(true);
                            getActivity().startService(intent);
                            Log.d(TAG, "onPreferenceChange: START SERVICE");
                            return true;
                        }
                    }else{
                        Toast.makeText(getContext(), "Omogućite GPS lokaciju", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    fn_permission();
                    Toast.makeText(getContext(), "Dozvolite pristup lokaciji", Toast.LENGTH_SHORT).show();

                    return false;
                }
            }else{
                if(getActivity() != null)
                    getActivity().stopService(intent);
                Log.d(TAG, "onPreferenceChange: STOP SERVICE");
            }
            return true;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repo.getAll().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                profil.setSummary(repo.getUsers().get(0).ime + " " + repo.getUsers().get(0).prezime);
            }
        });
    }

    private boolean getBooleanPermission() {
        return boolean_permission;
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS);
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean_permission = true;

            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Service is already running", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getContext(), "Dozvola je potrebna za dohvaćanje lokacije",
                    Toast.LENGTH_LONG).show();

        }
    }

}