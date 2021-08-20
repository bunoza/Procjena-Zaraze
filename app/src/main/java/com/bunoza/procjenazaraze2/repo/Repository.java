package com.bunoza.procjenazaraze2.repo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.bunoza.procjenazaraze2.api.RetrofitClient;
import com.bunoza.procjenazaraze2.api.RetrofitInterface;
import com.bunoza.procjenazaraze2.db.AppDatabase;
import com.bunoza.procjenazaraze2.model.Approximation;
import com.bunoza.procjenazaraze2.model.CovidDB;
import com.bunoza.procjenazaraze2.model.CovidZadnjiPodaci;
import com.bunoza.procjenazaraze2.model.CovidZadnjiZupanije;
import com.bunoza.procjenazaraze2.model.LocationsModel;
import com.bunoza.procjenazaraze2.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static Repository repository;
    private final String TAG = "Repository";
    private Call<List<CovidZadnjiPodaci>> callCovidZadnjiPodaci;
    private MutableLiveData<CovidZadnjiPodaci> covidZadnjiPodaciSingleM = new MutableLiveData<>();
    private Call<List<CovidZadnjiZupanije>> callCovidZadnjiPodaciZupanije;
    private MutableLiveData<CovidZadnjiZupanije> covidZadnjiPodaciZupanijeSinglem = new MutableLiveData<>();
    private int spinnerItemPosition;
    MutableLiveData<CovidDB> covidCurrent;
    SharedPreferences sharedPreferences;
    Boolean FIRST_API_COMPLETED, SECOND_API_COMPLETED = false;
    static Context context;
    MutableLiveData<Boolean> shouldSetRefreshingFalse = new MutableLiveData<>();
    public MutableLiveData<Boolean> areLocationsPopulated;

    public void setSpinnerItemPosition(int spinnerItemPosition) {
        this.spinnerItemPosition = spinnerItemPosition;
        getData();
        storeData(spinnerItemPosition);
    }

    AppDatabase db;

    public static Repository getInstance(){
        if(repository == null){
            repository = new Repository();
        }

        return repository;
    }

    public static Repository getInstance(Context context){
        if(repository == null){
            repository = new Repository(context);

        }

        return repository;
    }

    public void initDB(Context context){
        db = Room.databaseBuilder(context, AppDatabase.class, "database").allowMainThreadQueries().build();
    }

    public boolean isDBnull(){
        Log.d(TAG, String.valueOf("isDBnull: " + db == null));
        return db == null;
    }

    public void insertUser(User user){
        db.userDao().insert(user);
    }

    public void deleteUser(){
        db.userDao().delete(db.userDao().getAll().getValue().get(0));
    }

    private Repository() {
        getData();

    }
    private Repository(Context context) {
        this.context = context;
        getData();
    }

    public LiveData<List<User>> getAll(){
        return db.userDao().getAll();
    }

    public void deleteLocationData(){
        if(db.locationsDao().getAll() != null)
        db.locationsDao().eraseTableData();
    }

    public void storeData(int spinnerItemPosition){
        try{
            db.covidDBDao().insert(new CovidDB(covidZadnjiPodaciSingleM.getValue().getSlucajeviHrvatska(),
                    covidZadnjiPodaciSingleM.getValue().getIzlijeceniHrvatska(),
                    covidZadnjiPodaciSingleM.getValue().getUmrliHrvatska(),
                    covidZadnjiPodaciZupanijeSinglem.getValue().getPodaciDetaljno().get(spinnerItemPosition).getBrojZarazenih(),
                    covidZadnjiPodaciZupanijeSinglem.getValue().getPodaciDetaljno().get(spinnerItemPosition).getBrojAktivni(),
                    covidZadnjiPodaciZupanijeSinglem.getValue().getPodaciDetaljno().get(spinnerItemPosition).getBrojUmrlih()));
        }catch (Exception e){
            Log.e(TAG, "storeData: " + e );
        }
    }


    public void getData() {

        shouldSetRefreshingFalse.setValue(false);
        RetrofitInterface retrofitInterface = RetrofitClient.getRetrofitInterface();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        callCovidZadnjiPodaci = retrofitInterface.getLatest();
        callCovidZadnjiPodaciZupanije = retrofitInterface.getByCounty();

        Callback<List<CovidZadnjiPodaci>> callback = new Callback<List<CovidZadnjiPodaci>>() {
            @Override
            public void onResponse(Call<List<CovidZadnjiPodaci>> call, Response<List<CovidZadnjiPodaci>> response) {
                Log.d(TAG, "onResponse: ODGOVOR DRZAVA");
                if (response.isSuccessful() && response.body() != null) {
                    covidZadnjiPodaciSingleM.setValue(new CovidZadnjiPodaci(response.body()));
                    FIRST_API_COMPLETED = true;
                }
            }

            @Override
            public void onFailure(Call<List<CovidZadnjiPodaci>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t);
                FIRST_API_COMPLETED = false;
                shouldSetRefreshingFalse.setValue(true);
            };
        };

        Callback<List<CovidZadnjiZupanije>> callbackZupanije = new Callback<List<CovidZadnjiZupanije>>() {
            @Override
            public void onResponse(Call<List<CovidZadnjiZupanije>> call, Response<List<CovidZadnjiZupanije>> response) {
                Log.d(TAG, "onResponse: ODGOVOR ZUPANIJA");
                if (response.isSuccessful() && response.body() != null) {
                    covidZadnjiPodaciZupanijeSinglem.setValue(new CovidZadnjiZupanije(response.body()));
                    SECOND_API_COMPLETED = true;
                    if(FIRST_API_COMPLETED){
                        FIRST_API_COMPLETED = false;
                        SECOND_API_COMPLETED = false;
                        covidCurrent = new MutableLiveData<>();
                        covidCurrent.setValue(new CovidDB(covidZadnjiPodaciSingleM.getValue().getSlucajeviHrvatska(),
                                covidZadnjiPodaciSingleM.getValue().getIzlijeceniHrvatska(),
                                covidZadnjiPodaciSingleM.getValue().getUmrliHrvatska(),
                                covidZadnjiPodaciZupanijeSinglem.getValue().getPodaciDetaljno().get(Integer.parseInt(sharedPreferences.getString("zupanija", "10"))).getBrojZarazenih(),
                                covidZadnjiPodaciZupanijeSinglem.getValue().getPodaciDetaljno().get(Integer.parseInt(sharedPreferences.getString("zupanija", "10"))).getBrojAktivni(),
                                covidZadnjiPodaciZupanijeSinglem.getValue().getPodaciDetaljno().get(Integer.parseInt(sharedPreferences.getString("zupanija", "10"))).getBrojUmrlih()));
                        shouldSetRefreshingFalse.setValue(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CovidZadnjiZupanije>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t);
                SECOND_API_COMPLETED = false;
                shouldSetRefreshingFalse.setValue(false);
            }
        };

        callCovidZadnjiPodaci.enqueue(callback);
        callCovidZadnjiPodaciZupanije.enqueue(callbackZupanije);

    }

    public void insertApprox(Approximation approximation){
        if(db != null){
            db.approxDao().insert(approximation);
        }
    }

    public List<Approximation> getApproxDead(){
        return db.approxDao().getAll();
    }

    public void deleteLastApproximation(){
        db.approxDao().delete(db.approxDao().getAll().get(db.approxDao().getAll().size()-1));
    }

    public void checkApproxCount(){
        if(db.approxDao().getAll().size() > 0){
            if(db.approxDao().getAll().size() > 7){
                int diff = db.approxDao().getAll().size() - 7;
                for(int i = 0; i < diff; i++){
                    db.approxDao().delete(db.approxDao().getAll().get(0));
                }
            }
        }
    }


    public void insertData(LocationsModel locationsModel){
        Log.d(TAG, "insertData: " + locationsModel.address);
        db.locationsDao().insert(locationsModel);
    }

    public LiveData<CovidDB> getLatestCases(){
        return db.covidDBDao().getAll();
    }

    public LiveData<List<Approximation>> getLatestApproximations(){
        return db.approxDao().getAllLive();
    }

    public void deleteLocation(LocationsModel locationsModel){
        db.locationsDao().delete(locationsModel);
    }

    public LiveData<List<LocationsModel>> getLocations(){
            return db.locationsDao().getAllLive();
    }
    public ArrayList<LocationsModel> getLocationsDead(){
        return new ArrayList<>(db.locationsDao().getAll());
    }
    public LiveData<Boolean> getShouldSetRefreshFalse(){
        return shouldSetRefreshingFalse;
    }

    public boolean isUserPopulated(){
        boolean temp = db.userDao().getAll().getValue() != null;
        Log.d(TAG, "isUserPopulated: " + temp);
        return temp;
    }

    public List<User> getUsers(){
        if(db.userDao().getAllDead() != null)
            return db.userDao().getAllDead();
        return null;
    }

    public int getUserCount(){
        return db.userDao().getElementCount();
    }

}
