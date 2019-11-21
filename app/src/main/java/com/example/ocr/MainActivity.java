package com.example.ocr;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    //Variable Declaretions

    EditText mResultEt;
    TextView imageViewET;
    ImageView mPreviewIv;
    Bitmap bitmap;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final  int STORAGE_REQUEST_CODE = 400;
    private static final  int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final  int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    File MyDir;
    String userFileName;
    String tempEtData;
    String query;

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.dark_blue));
        }

        //To get Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mResultEt = findViewById(R.id.resultEt);
        mPreviewIv = findViewById(R.id.imageIv);
        imageViewET = findViewById(R.id.imageViewET);


        //For Permissions
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //Requesting for permissions at the installation of application
        requestForAllPermissions();

        //Action Buttons
        FloatingActionButton fb1 = findViewById(R.id.addPhotos);
        FloatingActionButton fb2 = findViewById(R.id.viewResult);
        FloatingActionButton fb3 = findViewById(R.id.Save);
        FloatingActionButton fb4 = findViewById(R.id.Clear);

        fb1.setOnClickListener(new View.OnClickListener()
       {
           @Override
           public void onClick(View v)
           {
               showImageImportDialog();
           }
       });
        fb2.setOnClickListener(new View.OnClickListener()
       {
            @Override
            public void onClick(View v)
            {
                SearchResult();
            }
        });
        fb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();

            }
        });
        fb4.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                clearImage();
            }
        });

    }

    private void saveData()
    {
        //Creating a new EditText input for entering filename
        final EditText inputData = new EditText(this);

        //Creating a variable for get content of Edittext
        final String temp =  mResultEt.getText().toString();

        //Checking if temp variable is empty or not
        if(!temp.isEmpty())
        {
            //If temp variable is not empty
            //create a new Dialog box for entering file name
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Please Provide File Name");
            dialog.setMessage("Enter File Name");
            dialog.setView(inputData);

            //Set a Postive Button for Dialog
            dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                            //Creating a Directory named OCR
                            MyDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OCR");
                            MyDir.mkdirs();

                            //Creating a File
                            //userFileName variable for getting file name
                            userFileName = inputData.getText().toString();
                            /*
                            SharedPreferences mypre1 = getSharedPreferences("msg1",0);
                            SharedPreferences.Editor editor1 = mypre1.edit();
                            editor1.putString("FileName",userFileName);
                            editor1.commit();
                            */

                            SharedPreferences mypre = getSharedPreferences("msg",0);
                            SharedPreferences.Editor editor = mypre.edit();
                            editor.putString("Value",temp);
                            editor.commit();

                            File file = new File(MyDir, userFileName+ ".txt");

                            //Writing data to file
                            //temEtData variable for getting EditText Data
                            FileOutputStream fos = new FileOutputStream(file);
                            tempEtData = mResultEt.getText().toString();
                            fos.write(tempEtData.getBytes());

                            //Closing file
                            fos.close();

                            FileOutputStream outStream = null;
                            File MyImgDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OCR/Images");
                            MyImgDir.mkdirs();
                            File outFile = new File(MyImgDir, userFileName+".jpg");
                            outStream = new FileOutputStream(outFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.flush();
                            outStream.close();

                            Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                        }
                        catch (java.io.IOException e)
                        {
                            e.printStackTrace();
                        }
                }
            });

            //Set a Negative Button for Dialog
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            //Showing Dialog Box
            dialog.create().show();
        }

        //If  temp variable is empty
        else
        {
            Toast.makeText(this, "Please provide some Data", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.Settings)
        {
            openSettings();
        }
        if (id == R.id.OpenBrowser)
        {
            openBrower();
        }
        if( id == R.id.SavedFiles)
        {
            openSavedFiles();
        }
        if( id == R.id.AboutMe)
        {
            about_mine();
        }
        if (id == R.id.Exit)
        {
            showExitAlertDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void about_mine()
    {
        //Intent to About Activity
        Intent intent = new Intent(this,about_mine.class);
        startActivity(intent);
    }

    private void openSavedFiles()
    {
        //Intent to SavedFiles Activity
        Intent intent = new Intent(this,SavedFiles.class);
        startActivity(intent);
        finish();
    }

    private void openSettings()
    {
        //Intent to Settins Activity
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestForAllPermissions()
    {
        //Check for Camera Permission
        if (!checkCameraPermission())
        {
            //If not camera Permission is Enabled
            requestCameraPermission();
        }

        //Check for Starage Permission
        if (!checkStoragePermission())
        {
            //If not storage permission is Enabled
            requestStoragePermission();
        }
    }

    private void SearchResult()
    {
        //Inetnt to WebView Activity
        //Get search data from Edittext
        query = mResultEt.getText().toString();

        SharedPreferences mypre = getSharedPreferences("msg",0);
        SharedPreferences.Editor editor = mypre.edit();
        editor.putString("Value",query);
        editor.commit();

        Intent webViewIntent = new Intent(this,WebViewActivity.class);
        webViewIntent.putExtra("Query",query);
        startActivity(webViewIntent);
        finish();

    }

    private void openBrower()
    {
        //Opening an External Browser
        //Get search data from Edittext
        String temp = mResultEt.getText().toString();

        //Start Intent
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY,temp);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void clearImage()
    {
        //Clearing Image and Text
            imageViewET.setVisibility(View.VISIBLE);
            mPreviewIv.setImageResource(0);
            mResultEt.setText("");
            Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show();
    }

    private void showExitAlertDialog()
    {
        //For Exiting Application
        //Creating a new Dialog Box
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure want to Exit?");

        //Setting Postive and Negative Button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Showing Dialog Box
        builder.create().show();
    }

    private void showImageImportDialog()
    {
        //For Adding images to Application
        //Two ways of adding images
        //Either from Camera or from Gallery
        //Creating a Dialog Box
        String[] items = {" Camera", " Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    //If user click on Camera
                    //Check for Camera Permission
                    if (!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                    {
                        pickCamera();
                    }
                }
                if (which == 1)
                {
                    //If user click on Gallery
                    if (!checkStoragePermission())
                    {
                        requestStoragePermission();
                    }
                    else
                    {
                        pickGallery();
                    }
                }
            }
        });

        //Showing Dialog Box
        dialog.create().show();
    }

    private boolean checkCameraPermission()
    {
        //Checking for Camera Permission
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission()
    {
        //Requsting for Camera Permission
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission()
    {
        //Checking for Storage Permission
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission()
    {
        //Requesting for CameraPermission
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //On Granting Permissions
        switch (requestCode)
        {
            //For Camera
            case  CAMERA_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!cameraAccepted && writeStorageAccepted)
                    {
                        Toast.makeText(this,"Permissions Denied",Toast.LENGTH_SHORT).show();
                    }
                }
            //For Gallery
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!writeStorageAccepted)
                    {
                        Toast.makeText(this,"Permissions Denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    private void pickCamera()
    {
        //Opening Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    private void pickGallery()
    {
        //Opeing Gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //After opening Camera or Gallery
        //For Camera
        if( requestCode == IMAGE_PICK_CAMERA_CODE)
        {
            if( resultCode == RESULT_OK)
            {
                //For Cropping Image
                image_uri = CropImage.getPickImageResultUri(this,data);
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMultiTouchEnabled(true)
                        .start(this);
            }
        }
        //For Gallery
        else if (requestCode == IMAGE_PICK_GALLERY_CODE)
        {
            //Try Block is for if can't load image form storage
            try
            {
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true)
                    .start(this);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //For Cropping Image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                //Set cropped image to ImageView
                imageViewET.setVisibility(View.INVISIBLE);
                mPreviewIv.setImageURI(result.getUri());

                //Creating a Drawable bitmap for storing cropped image
                BitmapDrawable bitmapDrawable = (BitmapDrawable) mPreviewIv.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                //Opening a Dialog Box for Selecting Text Decoder Method
                selectDecoder();
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void selectDecoder()
    {
        //Creating Dialog Box
        String[] items = {" Google Service", " Firebase"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Decode Method");
        dialog.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    //Google play-services-vision Decoding
                    TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (recognizer.isOperational())
                    {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = recognizer.detect(frame);
                        StringBuffer sb = new StringBuffer();
                        for (int i=0; i<items.size(); i++)
                        {
                            TextBlock myItem = items.valueAt(i);
                            sb.append(myItem.getValue());
                            sb.append("\n");
                        }
                        mResultEt.setText(sb.toString());
                    }
                }


                if (which == 1)
                {
                    //firebasecode Decoding
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
                    textRecognizer.processImage(image)

                            //Sucess To Process Image
                            .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>()
                            {
                                @Override
                                public void onSuccess(FirebaseVisionText result)
                                {

                                    String resultText = result.getText();
                                    for (FirebaseVisionText.TextBlock block: result.getTextBlocks())
                                    {
                                        String blockText = block.getText();
                                        Float blockConfidence = block.getConfidence();
                                        List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                        Point[] blockCornerPoints = block.getCornerPoints();
                                        Rect blockFrame = block.getBoundingBox();

                                        for (FirebaseVisionText.Line line: block.getLines())
                                        {
                                            String lineText = line.getText();
                                            Float lineConfidence = line.getConfidence();
                                            List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                            Point[] lineCornerPoints = line.getCornerPoints();
                                            Rect lineFrame = line.getBoundingBox();

                                            for (FirebaseVisionText.Element element: line.getElements())
                                            {
                                                String elementText = element.getText();
                                                Float elementConfidence = element.getConfidence();
                                                List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                                                Point[] elementCornerPoints = element.getCornerPoints();
                                                Rect elementFrame = element.getBoundingBox();

                                                mResultEt.setText(resultText);
                                            }
                                        }
                                    }
                                }
                            })
                            //Failed To Process Image
                            .addOnFailureListener(
                            new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Failed to Recognise Text", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

        //Showing Dialog Box
        dialog.create().show();
    }

    @Override
    public void onBackPressed()
    {
        //On Back pressed in MainActivity
        //Call exiting application Dialog method
        showExitAlertDialog();
    }

    @Override
    protected void onResume()
    {
        //Getting Last Stored text value using Shared Preferences
        super.onResume();
        SharedPreferences mypre = getSharedPreferences("msg",0);
        String Value = mypre.getString("Value",null);
        mResultEt.setText(Value);
/*
        SharedPreferences mypre1 = getSharedPreferences("msg1",0);
        String FileName = mypre1.getString("FileName",null);

        try
        {
            File MyImgDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/OCR/Images");
            File fileImg = new File(MyImgDir, FileName+".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(fileImg));
            mPreviewIv.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
*/

    }

}
