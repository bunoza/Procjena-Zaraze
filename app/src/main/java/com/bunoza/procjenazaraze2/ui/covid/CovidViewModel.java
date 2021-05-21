package com.bunoza.procjenazaraze2.ui.covid;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunoza.procjenazaraze2.MainActivity;
import com.bunoza.procjenazaraze2.api.RetrofitClient;
import com.bunoza.procjenazaraze2.api.RetrofitInterface;
import com.bunoza.procjenazaraze2.model.CovidZadnjiPodaci;
import com.bunoza.procjenazaraze2.model.CovidZadnjiZupanije;
import com.bunoza.procjenazaraze2.repo.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CovidViewModel extends ViewModel {

    private final String TAG = "CovidViewModel";
    private List<CovidZadnjiPodaci> covidZadnjiPodaciM;
    private Call<List<CovidZadnjiPodaci>> callCovidZadnjiPodaci;
    private MutableLiveData<CovidZadnjiPodaci> covidZadnjiPodaciSingleM;
    private Call<List<CovidZadnjiZupanije>> callCovidZadnjiPodaciZupanije;
    private MutableLiveData<CovidZadnjiZupanije> covidZadnjiPodaciZupanijeSinglem;
    private Repository repo;

    public CovidViewModel() {
        repo = Repository.getInstance();
    }

    public Repository getRepository(){
        return this.repo;
    }

    public void notifySpinnerChanged(int i){
        repo.setSpinnerItemPosition(i);
    }
    public LiveData<Boolean> getShouldSetRefreshFalse(){
        return repo.getShouldSetRefreshFalse();
    }
}