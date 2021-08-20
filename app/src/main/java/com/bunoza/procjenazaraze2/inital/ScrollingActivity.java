package com.bunoza.procjenazaraze2.inital;
import android.app.Activity;
import android.os.Bundle;

import com.bunoza.procjenazaraze2.R;
import com.bunoza.procjenazaraze2.model.User;
import com.bunoza.procjenazaraze2.repo.Repository;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class ScrollingActivity extends AppCompatActivity {

    EditText etIme, etPrezime;
    RadioButton rbMusko, rbZensko, rbNeodredeno, rbPusenjeDa, rbAstmaDa, rbLijekoviDa;
    DatePicker dpDate;
    Spinner spinner;
    Button btnContinue;
    Repository repo;
    boolean insertedFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        initUI();
    }

    private void initUI() {
        etIme = findViewById(R.id.etIme);
        etPrezime = findViewById((R.id.etPrezime));
        rbMusko = findViewById(R.id.musko);
        rbZensko = findViewById(R.id.zensko);
        rbNeodredeno = findViewById(R.id.neodredeno);
        rbPusenjeDa = findViewById(R.id.pusenjeDa);
        rbLijekoviDa = findViewById(R.id.lijekoviDa);
        dpDate = findViewById(R.id.dpDate);
        spinner = findViewById(R.id.spinner);
        btnContinue = findViewById(R.id.btnNastavi);


        btnContinue.setOnClickListener(view -> {
            String ime = etIme.getText().toString();
            String prezime = etPrezime.getText().toString();
            char spol;
            if(rbMusko.isChecked()){ spol = 'M'; }
            else if(rbZensko.isChecked()){ spol = 'Z'; }
            else{ spol = 'X'; }
            String datum = dpDate.getDayOfMonth() +"/"+ (dpDate.getMonth()+1) +"/"+dpDate.getYear();
            boolean pusenje;
            pusenje = rbPusenjeDa.isChecked();
            boolean lijekovi;
            lijekovi = rbLijekoviDa.isChecked();
            int tempIndex = spinner.getSelectedItemPosition();
            String posao = getResources().getStringArray(R.array.poslovi_array)[tempIndex];
            repo = Repository.getInstance();
            insertedFlag = false;
            if (repo.isUserPopulated()) { repo.deleteUser(); }
            repo.insertUser(new User(ime, prezime, spol, datum, pusenje, lijekovi, posao));
            setResult(Activity.RESULT_OK);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}