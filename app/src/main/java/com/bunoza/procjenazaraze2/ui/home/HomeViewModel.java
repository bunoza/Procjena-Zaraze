package com.bunoza.procjenazaraze2.ui.home;

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

import com.bunoza.procjenazaraze2.R;
import com.bunoza.procjenazaraze2.model.CovidDB;
import com.bunoza.procjenazaraze2.model.LocationsModel;
import com.bunoza.procjenazaraze2.model.User;
import com.bunoza.procjenazaraze2.repo.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class HomeViewModel extends AndroidViewModel {

    private final String TAG = "HomeViewModel";
    Repository repo;
    Observer<LocationsModel> observerLocation;
    Observer<CovidDB> observerCovid;
    Observer<List<User>> observerUser;
    MutableLiveData<String> lastLocation;
    MutableLiveData<Integer> covidHr;
    MutableLiveData<Integer> covidZup;
    MutableLiveData<User> user;
    MutableLiveData<Double> approximation;
    Observer<LocationsModel> observer;
    SharedPreferences sharedPreferences;
    Context context;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repo = Repository.getInstance();
        lastLocation = new MutableLiveData<>();
        covidHr = new MutableLiveData<>();
        covidZup = new MutableLiveData<>();
        user = new MutableLiveData<>();
        approximation = new MutableLiveData<>();
        context = application.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        observerLocation = new Observer<LocationsModel>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(LocationsModel locationsModel) {
                if(repo.getLocationsDead() != null){
                    lastLocation.setValue("Zadnja lokacija:  " + (Arrays.asList(repo.getLocationsDead().address.split(",")).get(0)));
//                    checkTimestamps();
                    setNewApproximation();
                }
            }
        };
        observerCovid = new Observer<CovidDB>() {
            @Override
            public void onChanged(CovidDB covidDB) {
                if(covidDB != null){
                    covidHr.setValue(covidDB.hr_zarazeni);
                    covidZup.setValue(covidDB.zup_zarazeni);
                    setNewApproximation();
                }
            }
        };

        observerUser = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                user.setValue(users.get(0));
                setNewApproximation();
            }
        };
        observer = new Observer<LocationsModel>() {
            @Override
            public void onChanged(LocationsModel locationsModel) {
                if(locationsModel != null){
                    setNewApproximation();
                }
            }
        };
        repo.getLocations().observeForever(observer);
        repo.getLocations().observeForever(observerLocation);
        repo.getLatestCases().observeForever(observerCovid);
        repo.getAll().observeForever(observerUser);
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
                Log.d(TAG, "fn_update: db!=null " + temp.toString());
                repo.insertData(temp);
            }
        }
    }

    public void setNewApproximation(){
        double sum = 0;
        int age = 0;
        if(user.getValue() != null){
            if (user.getValue().spol == 'M') {
                sum += 5;
            } else if (user.getValue().spol == 'Z') {
                sum += 3;
            } else {
                sum += 4;
            }
        }


        if(user.getValue() != null) {
            try {
//                Date date = format.parse(user.getValue().datum);
                List<String> datumi = Arrays.asList(user.getValue().datum.split("/"));
                age = getAge(Integer.parseInt(datumi.get(2)), Integer.parseInt(datumi.get(1)), Integer.parseInt(datumi.get(0)));

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (age < 15) {
                sum += 5;
            } else if (age < 30) {
                sum += 10;
            } else if (age < 45) {
                sum += 15;
            } else if (age < 60) {
                sum += 20;
            } else {
                sum += 25;
            }
            Log.d(TAG, "setNewApproximation: " + age);

            if (user.getValue().pusenje) {
                sum += 10;
            }
            if (user.getValue().astma) {
                sum += 5;
            }
            if (user.getValue().lijekovi) {
                sum += 15;
            }

            List<String> list = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.poslovi_array)));
//            Log.d(TAG, "index: " + Arrays.toString(context.getResources().getStringArray(R.array.poslovi_array)));
            int index = list.indexOf(user.getValue().posao)  ;
//            Log.d(TAG, "index: " + index);
            sum += Integer.parseInt(Arrays.asList(context.getResources().getStringArray(R.array.posloviValues_array)).get(index));
//            Log.d(TAG, "poslije posla: " + sum);
        }

        if(covidZup.getValue() != null){
            sum += (covidZup.getValue()*1.0)/10;
        }

        if(repo.getLocationsDead() != null){
            List<String> list = new ArrayList<>(Arrays.asList(repo.getLocationsDead().address.split("/")));
            sum *= list.size();
            if(list.size() == 1 || list.size() == 0){
                sum = 0;
            }
        }else {
            sum = 0;
        }
        if(sum >= 999.99){
            sum = 999.90;
        }
        approximation.setValue(sum/10);
    }

    private int getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        return age;
    }

    public LiveData<Double> getApproximation(){
        return approximation;
    }

    public LiveData<String> getLastLocation(){
        return lastLocation;
    }
    public LiveData<Integer> getCovidHr(){
        return covidHr;
    }
    public LiveData<Integer> getCovidZup(){
        return covidZup;
    }
    public LiveData<User> getUsers(){
        return user;
    }



    @Override
    protected void onCleared() {
        repo.getLocations().removeObserver(observerLocation);
        repo.getLocations().removeObserver(observer);
        repo.getLatestCases().removeObserver(observerCovid);
        repo.getAll().removeObserver(observerUser);
        super.onCleared();
    }

}