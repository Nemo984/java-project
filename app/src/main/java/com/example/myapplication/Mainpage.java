package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.app.Dialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
public class Mainpage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Dialog myDialog;
    EditText editText;
    private TextView dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navi);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Home()).commit();
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        myDialog = new Dialog(this);
    }

    private void showDate(){
        DatePickerDialog datePickerDialog=new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void ShowPopup(View v){
        TextView txtclose;
        myDialog.setContentView(R.layout.pop_menu);
        txtclose =(TextView) myDialog.findViewById(R.id.textView3);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        Places.initialize(getApplicationContext(), "AIzaSyAQDtDk9VFC_mTpq16k5PvTvSD-WHC7RLY");
        editText = (EditText) myDialog.findViewById(R.id.edit_text);
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldsList = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldsList).build(Mainpage.this);
                startActivityForResult(intent, 100);
            }
        });

        dateText = (TextView) myDialog.findViewById(R.id.dateTextTest);
        myDialog.findViewById(R.id.date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });

        myDialog.show();
    }




    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new Home();
                            break;
                        case R.id.nav_map:
                            selectedFragment = new MapsFragment();
                            break;
                        case R.id.nav_info:
                            selectedFragment = new info();
                            break;
                        case R.id.nav_time:
                            selectedFragment = new search();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        dateText = (TextView) myDialog.findViewById(R.id.dateTextTest);
        String date=day+"/"+month+"/"+year;
        dateText.setText(date);
    }
}