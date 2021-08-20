package com.bunoza.procjenazaraze2.api;


import androidx.lifecycle.MutableLiveData;

import com.bunoza.procjenazaraze2.model.CovidZadnjiPodaci;
import com.bunoza.procjenazaraze2.model.CovidZadnjiZupanije;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {
    @GET("json/?action=podaci")
    Call<List<CovidZadnjiPodaci>> getLatest();

    @GET("json/?action=po_danima_zupanijama")
    Call<List<CovidZadnjiZupanije>> getByCounty();
}


