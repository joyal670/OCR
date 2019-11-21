package com.example.ocr;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class SavedFiles extends AppCompatActivity
{
    //Variable Delecration
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_files);

        //Setting Status Bar Color
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.dark_blue));
        }

        //Setting Actionbar Title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Saved Files");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196F3")));

        lv = (ListView) findViewById(R.id.fileNameTv);

        ShowFiles();

    }

    //Showing Availble files in ListView
    private void ShowFiles()
    {
        final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OCR";
        File directory = new File(path);
        File[] files = directory.listFiles();
        final String[] NameOfFiles = new String[files.length];
        for (int i=0; i<files.length; i++)
        {

            NameOfFiles[i] = files[i].getName();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,NameOfFiles);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    String path = parent.getItemAtPosition(position).toString();
                    openFile(path);
                }
            });
        }
    }

    //While click on ListView
    private void openFile(String path)
    {
        Intent intent = new Intent(this,DataViewActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);

    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
