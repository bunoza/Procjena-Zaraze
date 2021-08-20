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
import com.bunoza.procjenazaraze2.model.Approximation;
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
    Observer<List<LocationsModel>> observerLocation;
    Observer<CovidDB> observerCovid;
    Observer<List<User>> observerUser;
    MutableLiveData<String> lastLocation;
    MutableLiveData<Integer> covidHr;
    MutableLiveData<Integer> covidZup;
    MutableLiveData<User> user;
    MutableLiveData<Double> approximation;
    Observer<List<LocationsModel>> observer;
    Observer<List<Approximation>> approxObserver;
    MutableLiveData<List<Approximation>> lastApproximations;
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
        lastApproximations = new MutableLiveData<>();
        context = application.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        observerLocation = locationsModel -> {
            if(repo.getLocationsDead() != null && repo.getLocationsDead().size() > 0){
                lastLocation.setValue("Zadnja lokacija:  " + repo.getLocationsDead().get(repo.getLocationsDead().size()-1).address);
                setNewApproximation();
            }
        };
        observerCovid = covidDB -> {
            if(covidDB != null){
                covidHr.setValue(covidDB.hr_zarazeni);
                covidZup.setValue(covidDB.zup_zarazeni);
            }
        };

        observerUser = users -> {
            user.setValue(users.get(0));
            setNewApproximation();
        };
        observer = locationsModel -> {
            if(locationsModel != null){
                setNewApproximation();
            }
        };

        approxObserver = approximations -> lastApproximations.setValue(approximations);

        repo.getLocations().observeForever(observer);
        repo.getLocations().observeForever(observerLocation);
        repo.getLatestCases().observeForever(observerCovid);
        repo.getAll().observeForever(observerUser);
        repo.getLatestApproximations().observeForever(approxObserver);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void checkTimestamps(){
//        if(repo.getLocationsDead() != null){
//            List<String> addresses = new ArrayList<>(Arrays.asList(repo.getLocationsDead().address.split("/")));
//            List<String> timestamps = new ArrayList<>(Arrays.asList(repo.getLocationsDead().timestamp.split("/")));
//            for (int i = addresses.size() - 1; i >= 0; i--) {
//                if ((new Date().getTime() - Long.parseLong(timestamps.get(i))) > Long.parseLong(sharedPreferences.getString("deleteInterval", "43200000"))) {
//                    addresses.remove(i);
//                    timestamps.remove(i);
//                }
//            }
//            if (addresses.size() == 0) {
//                repo.deleteLocationData();
//            } else {
//                String tempAddresses = String.join("/", addresses);
//                String tempTimestamps = String.join("/", timestamps);
//                LocationsModel temp = new LocationsModel(tempTimestamps, tempAddresses);
//                Log.d(TAG, "fn_update: db!=null " + temp.toString());
//                repo.insertData(temp);
//            }
//        }
//    }

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
                List<String> datumi = Arrays.asList(user.getValue().datum.split("/"));
                age = getAge(Integer.parseInt(datumi.get(2)), Integer.parseInt(datumi.get(1)),
                        Integer.parseInt(datumi.get(0)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (age < 15) {
                sum += 5;  } else if (age < 30) {
                sum += 10; } else if (age < 45) {
                sum += 15; } else if (age < 60) {
                sum += 20; } else {
                sum += 25;
            }
            if (user.getValue().pusenje) {
                sum += 10;
            }
            if (user.getValue().lijekovi) {
                sum += 15;
            }
            List<String> list = new ArrayList<String>(Arrays.asList(context.getResources()
                    .getStringArray(R.array.poslovi_array)));
            int index = list.indexOf(user.getValue().posao)  ;
            sum += Integer.parseInt(Arrays.asList(context.getResources()
                    .getStringArray(R.array.posloviValues_array)).get(index));
        }

        if(covidZup.getValue() != null){
            sum += (covidZup.getValue()*1.0)/10;
        }

        if(repo.getLocationsDead() != null){
            ArrayList<LocationsModel> list = new ArrayList<>(repo.getLocationsDead());
            sum *= list.size();
            if(list.size() == 1 || list.size() == 0){
                sum = 0;
            }
        }else {
            sum = 0;
        }
        sum /= 200.0 /3;
        sum = (1)/(1+Math.exp(-(-3.5+(sum))));
        if(sum >= 0.999){
            sum = 0.999;
        }
        approximation.setValue(sum*100);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(Calendar.getInstance().getTimeInMillis()));
        if(repo.getApproxDead().size() > 0){
            if(!dateString.equals(repo.getApproxDead().get(repo.getApproxDead().size()-1).date)){
                repo.deleteLocationData();
                sum = 0;
                repo.insertApprox(new Approximation(sum, dateString));
            }else if(dateString.equals(repo.getApproxDead().get(repo.getApproxDead().size()-1).date)
                    && sum > repo.getApproxDead().get(repo.getApproxDead().size()-1).value){
                repo.deleteLastApproximation();
                repo.insertApprox(new Approximation(sum, dateString));
            }
        }else {
            repo.insertApprox(new Approximation(sum, dateString));
        }
        repo.checkApproxCount();
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
        repo.getLatestApproximations().removeObserver(approxObserver);
        super.onCleared();
    }

}