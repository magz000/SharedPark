package com.sharepark.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sharepark.R;
import com.sharepark.util.WebServiceHelper;

public class Terms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        assert toolBar != null;
        toolBar.setNavigationIcon(R.drawable.md_nav_back);
        toolBar.setTitle("Terms of Use");
        setSupportActionBar(toolBar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        SharedPreferences preferences = this.getSharedPreferences("com.pickapark", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        WebView mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(preferences.getString("webservice", WebServiceHelper.web_tip)+"/pickapark_terms.html");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
