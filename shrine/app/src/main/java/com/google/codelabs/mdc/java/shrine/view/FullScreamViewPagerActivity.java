package com.google.codelabs.mdc.java.shrine.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.model.ImageEntry;
import com.google.codelabs.mdc.java.shrine.utils.adapters.ImageAdapter;

import java.util.ArrayList;

public class FullScreamViewPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_fullscream_viewpager_activity);
        ViewPager fullScreamViewPager = findViewById(R.id.fullscream_viewpager);
        Intent callingActivityIntent = getIntent();
        if (callingActivityIntent != null) {
            ArrayList<ImageEntry> images = (ArrayList<ImageEntry>) callingActivityIntent.getSerializableExtra("images");
            int position = (int) callingActivityIntent.getSerializableExtra("position");
            ImageAdapter imageAdapter = new ImageAdapter(this, images, null, true);
            fullScreamViewPager.setAdapter(imageAdapter);
            fullScreamViewPager.setCurrentItem(position);
        }
    }
}
