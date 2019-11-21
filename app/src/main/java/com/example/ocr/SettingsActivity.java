package com.example.ocr;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity
{
    //Variable Delecration
    RadioGroup radioGroup;
    TextView GoogleService,FireBase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Status Bar Color
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.dark_blue));
        }

        //ActionBar Title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196F3")));

        GoogleService = (TextView) findViewById(R.id.GoogleService);
        FireBase = (TextView) findViewById(R.id.FireBase);

        //Clicking on Radio Buttons
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.radio1:
                        GoogleService.setVisibility(View.VISIBLE);
                        FireBase.setVisibility(View.GONE);
                        break;

                    case R.id.radio2:
                        FireBase.setVisibility(View.VISIBLE);
                        GoogleService.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
