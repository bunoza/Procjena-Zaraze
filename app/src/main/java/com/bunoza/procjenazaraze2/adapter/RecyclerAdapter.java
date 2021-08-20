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
    Observer<List<LocationsModel>> observer;
    public final String TAG = "RecyclerAdapter";
    List<String> raw = new ArrayList<>();
    ClickListener clickListener;


    public RecyclerAdapter(ClickListener clickListener) {
        repo = Repository.getInstance();
        this.clickListener = clickListener;
        observer = locationsModels -> {
            addData(repo.getLocationsDead());
            notifyDataSetChanged();
        };
        repo.getLocations().observeForever(observer);
    }

    public void addData(ArrayList<LocationsModel> locationsModel){
        if(locationsModel != null){
            raw.clear();
            int i;
            for(i = 0; i < repo.getLocationsDead().size(); i++){
                raw.add(repo.getLocationsDead().get(repo.getLocationsDead().size() -1 -i).address);
            }
        }
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_row, parent,
                false);
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
