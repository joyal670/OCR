package com.example.ocr;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class DataViewActivity extends AppCompatActivity
{
    //Variable Declerations
    TextView tv,dv;
    ImageView iv;

    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        //Status Bar Color
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.dark_blue));
        }

        //Hide ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        tv = (TextView) findViewById(R.id.savedTv);
        dv = (TextView) findViewById(R.id.nameTv);
        iv = (ImageView) findViewById(R.id.imageInDataView);

        //Get name of selected file
        filename = getIntent().getExtras().getString("path");
        dv.setText(filename);

        //calls methods
        showImage();
        showText();


    }

    //Getting and Setting Text
    private void showText()
    {
        try
        {
            File myDir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OCR");
            File file = new File(myDir,filename);

            FileInputStream fin = new FileInputStream(file);
            InputStreamReader inputStream = new InputStreamReader(fin);
            BufferedReader bufferedReader = new BufferedReader(inputStream);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            fin.close();
            inputStream.close();

            tv.setText(stringBuilder.toString());

        } catch (java.io.IOException e)
        {
            e.printStackTrace();
        }

    }

    //Getting and Setting Image
    private void showImage()
    {
        try
        {
            String newfilename = filename.replace(".txt",".jpg");
            File MyImgDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OCR/Images");
            File fileImg = new File(MyImgDir, newfilename);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(fileImg));
            iv.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this,SavedFiles.class);
        startActivity(intent);
        finish();
    }
}
