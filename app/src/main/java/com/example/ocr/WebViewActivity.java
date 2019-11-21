package com.example.ocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WebViewActivity extends AppCompatActivity
{

    //Variable Delecration
    WebView webView;
    BottomNavigationView bottomNavigationView;
    String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        //Status Bar Color
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.dark_blue));
        }

        //Hide ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        webView = (WebView)findViewById(R.id.webView);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);

        //Getting Search Value form MainActivity
        searchQuery = getIntent().getExtras().getString("Query");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://www.google.com/search?q="+searchQuery);
        webView.setWebViewClient(new WebViewClient());

        //BottomSheet Navigation
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.home:
                        webView.loadUrl("https://www.google.com/search?q="+searchQuery);
                        break;
                    case R.id.reload:
                        webView.reload();
                        break;
                    case R.id.next:
                        if(webView.canGoForward())
                        {
                            webView.goForward();
                        }
                        break;
                    case R.id.previous:
                        if (webView.canGoBack())
                        {
                            webView.goBack();
                        }
                }
            }
        });



    }

    @Override
    public void onBackPressed()
    {
        if (webView.copyBackForwardList().getCurrentIndex() > 0) {
            webView.goBack();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

