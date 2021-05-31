package com.bunoza.procjenazaraze2.ui.location;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bunoza.procjenazaraze2.R;
import com.bunoza.procjenazaraze2.adapter.ClickListener;
import com.bunoza.procjenazaraze2.adapter.RecyclerAdapter;
import com.bunoza.procjenazaraze2.model.LocationsModel;

import java.util.ArrayList;

public class LocationFragment extends Fragment implements ClickListener{

    private LocationViewModel locationViewModel;
    public final String TAG = "LOCATION FRAGMENT";
    private RecyclerView recycler;
    private RecyclerAdapter adapter;
    TextView textView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        locationViewModel =
                new ViewModelProvider(this).get(LocationViewModel.class);

        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        textView = view.findViewById(R.id.tvAdrese);
        recycler = view.findViewById(R.id.rvRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter(this);
        recycler.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation());
        recycler.addItemDecoration(dividerItemDecoration);

        locationViewModel.getAreLocationsPopulated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    textView.setVisibility(View.GONE);
                }else{
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Za dohvaćanje lokacije potrebno je upaliti GPS");
                }
            }
        });



    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        new AlertDialog.Builder(getContext())
                .setTitle("Upute")
                .setMessage("Kako bi se lokacije prikazivale potrebno je omogućiti dohvaćanje lokacije u postavkama. " +
                        "Pritiskom na lokaciju, pokreću se Google karte koje traže pritisnutu lokaciju. ")
                .setPositiveButton(android.R.string.yes, null)
//                .setIcon(R.drawable.ic_baseline_info_24)
                .show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(String s) {
        try {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + s);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }catch(Exception e){
            Toast.makeText(getContext(), "Nije moguće pokrenuti Google Maps", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
//        locationViewModel.checkTimestamps();
    }
}