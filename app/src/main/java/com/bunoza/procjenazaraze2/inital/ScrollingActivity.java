package com.bunoza.procjenazaraze2.inital;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bunoza.procjenazaraze2.MainActivity;
import com.bunoza.procjenazaraze2.R;
import com.bunoza.procjenazaraze2.model.User;
import com.bunoza.procjenazaraze2.repo.Repository;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;


import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private static final String TAG = "UNOS PODATAKA";

    EditText etIme, etPrezime;
    RadioButton rbMusko, rbZensko, rbNeodredeno, rbPusenjeDa, rbAstmaDa, rbLijekoviDa;
    DatePicker dpDate;
    Spinner spinner;
    Button btnNastavi;
    Repository repo;
    boolean insertedFlag = false;
    private final int REQUEST_CODE_SECOND_ACTIVITY = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//        toolBarLayout.setTitle(getTitle());
        initUI();


    }

    private void initUI() {
        etIme = findViewById(R.id.etIme);
        etPrezime = findViewById((R.id.etPrezime));
        rbMusko = findViewById(R.id.musko);
        rbZensko = findViewById(R.id.zensko);
        rbNeodredeno = findViewById(R.id.neodredeno);
        rbPusenjeDa = findViewById(R.id.pusenjeDa);
        rbAstmaDa = findViewById(R.id.astmaDa);
        rbLijekoviDa = findViewById(R.id.lijekoviDa);
        dpDate = findViewById(R.id.dpDate);
        spinner = findViewById(R.id.spinner);
        btnNastavi = findViewById(R.id.btnNastavi);


        btnNastavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try{
                    String ime = etIme.getText().toString();
                    String prezime = etPrezime.getText().toString();
                    char spol;
                    if(rbMusko.isChecked()){
                        spol = 'M';
                    }
                    else if(rbZensko.isChecked()){
                        spol = 'Z';
                    }
                    else{
                        spol = 'X';
                    }
                    String datum = dpDate.getDayOfMonth() +"/"+ (dpDate.getMonth()+1) +"/"+dpDate.getYear();
                    boolean pusenje;
                    if(rbPusenjeDa.isChecked()){
                        pusenje = true;
                    }
                    else{
                        pusenje = false;
                    }
                    boolean astma;
                    if(rbAstmaDa.isChecked()){
                        astma = true;
                    }
                    else{
                        astma = false;
                    }
                    boolean lijekovi;
                    if(rbLijekoviDa.isChecked()){
                        lijekovi = true;
                    }
                    else{
                        lijekovi = false;
                    }
                    int tempIndex = spinner.getSelectedItemPosition();
                    String posao = getResources().getStringArray(R.array.poslovi_array)[tempIndex];
//                    Log.d(TAG, "onClick: Ime:" + ime);
//                    Log.d(TAG, "onClick: Prezime:" + prezime);
//                    Log.d(TAG, "onClick: Spol:" + spol);
//                    Log.d(TAG, "onClick: Datum:" + datum);
//                    Log.d(TAG, "onClick: Posao:" + posao);

                    repo = Repository.getInstance();

                    insertedFlag = false;
                if (repo.isUserPopulated()) {
                    repo.deleteUser();

                }
                repo.insertUser(new User(ime, prezime, spol, datum, pusenje, astma, lijekovi, posao));


//                    List<User> listaTemp = new ArrayList<>(repo.);
//
//                    Log.d(TAG, "IZ BAZE:" + listaTemp.size());
//                    Log.d(TAG, "onClick:" + listaTemp.get(0).toString());

//                }catch (SQLiteConstraintException sql){
//                    Toast.makeText(ScrollingActivity.this, "Korisnik već dodan!", Toast.LENGTH_SHORT).show();
//                }catch(Exception e){
//                    Toast.makeText(ScrollingActivity.this, "Unesite ispravne podatke!", Toast.LENGTH_SHORT).show();
//                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}