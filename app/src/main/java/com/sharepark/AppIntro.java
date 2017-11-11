package com.sharepark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.sharepark.activities.Login;

public class AppIntro extends AppIntro2 {
    private SharedPreferences.Editor editor;

    @Override
    public void init(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences("firstRun", MODE_PRIVATE);
        editor = prefs.edit();

        Boolean firstRun;
        if(prefs.contains("first_run")) {
            firstRun = prefs.getBoolean("first_run", false);
            if (!firstRun) {
                finish();
                startActivity(new Intent(AppIntro.this, Login.class));
            }
        }
        else{
            firstRun = prefs.getBoolean("first_run", false);
            editor.apply();
        }
        addSlide(SampleSlide.newInstance(R.layout.intro_slide0));
        addSlide(SampleSlide.newInstance(R.layout.intro_slide1));
        addSlide(SampleSlide.newInstance(R.layout.intro_slide2));
        addSlide(SampleSlide.newInstance(R.layout.intro_slide3));
    }

    private void loadMainActivity() {
        finish();

    }

    @Override
    public void onDonePressed() {
        editor.putBoolean("first_run", false);
        editor.commit();
        finish();
        startActivity(new Intent(AppIntro.this, Login.class));
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onSlideChanged() {

    }

    public void getStarted(View v) {
        loadMainActivity();
    }
}
