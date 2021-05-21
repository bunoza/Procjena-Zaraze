package com.bunoza.procjenazaraze2.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bunoza.procjenazaraze2.R;
import com.bunoza.procjenazaraze2.model.LocationsModel;
import com.bunoza.procjenazaraze2.repo.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<AddressHolder> {
    Repository repo;
    Observer<LocationsModel> observer;
    public final String TAG = "RecyclerAdapter";
    List<String> raw = new ArrayList<>();
    ClickListener clickListener;


    public RecyclerAdapter(ClickListener clickListener) {
        repo = Repository.getInstance();
        this.clickListener = clickListener;
        observer = new Observer<LocationsModel>() {
            @Override
            public void onChanged(LocationsModel locationsModel) {
                addData(repo.getLocationsDead());
                notifyDataSetChanged();
//                Log.d(TAG, "onChanged: " + "CHANGE OBSERVED ADAPTER " + repo.getLocationsDead());
            }
        };
        repo.getLocations().observeForever(observer);

    }


    public void addData(LocationsModel locationsModel){
        if(locationsModel != null){
            raw.clear();
            int i;
            for (i = 0; i < Arrays.asList(locationsModel.address.split("/")).size(); i++) {
                raw.add(Arrays.asList(locationsModel.address.split("/")).get(i));
            }
        }
    }


    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_row, parent, false);
        return new AddressHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressHolder holder, int position) {
        if(repo.getLocationsDead() != null)
        holder.setName(raw.get(position));
    }

    @Override
    public int getItemCount() {
            return raw.size();
    }
}
