package com.bunoza.procjenazaraze2.ui.location;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import com.bunoza.procjenazaraze2.adapter.RecyclerAdapter;
import com.bunoza.procjenazaraze2.model.LocationsModel;
import com.bunoza.procjenazaraze2.repo.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LocationViewModel extends AndroidViewModel {

    private  Observer<LocationsModel> observer;
    private Repository repo;
    public final String TAG = "LOCATION VIEWMODEL";
    public MutableLiveData<Boolean> areLocationsPopulated;
    Context context;
    SharedPreferences sharedPreferences;



    public LocationViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        repo = Repository.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        areLocationsPopulated = new MutableLiveData<>();
        observer = new Observer<LocationsModel>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(LocationsModel locationsModel) {
                areLocationsPopulated.setValue(locationsModel != null);
            }
        };
        repo.getLocations().observeForever(observer);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void checkTimestamps(){
        if(repo.getLocationsDead() != null){
            List<String> addresses = new ArrayList<>(Arrays.asList(repo.getLocationsDead().address.split("/")));
            List<String> timestamps = new ArrayList<>(Arrays.asList(repo.getLocationsDead().timestamp.split("/")));
            for (int i = addresses.size() - 1; i >= 0; i--) {
                if ((new Date().getTime() - Long.parseLong(timestamps.get(i))) > Long.parseLong(sharedPreferences.getString("deleteInterval", "43200000"))) {
                    addresses.remove(i);
                    timestamps.remove(i);
                }
            }
            if (addresses.size() == 0) {
                repo.deleteLocationData();
            } else {
                String tempAddresses = String.join("/", addresses);
                String tempTimestamps = String.join("/", timestamps);
                LocationsModel temp = new LocationsModel(tempTimestamps, tempAddresses);
                Log.d(TAG, "provjera timestamps " + temp.toString());
                repo.insertData(temp);
            }
        }
    }

    public LiveData<Boolean> getAreLocationsPopulated(){
        return areLocationsPopulated;
    }


    @Override
    protected void onCleared() {
        repo.getLocations().removeObserver(observer);
        super.onCleared();
    }

}