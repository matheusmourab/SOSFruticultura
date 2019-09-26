package com.google.codelabs.mdc.java.shrine.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.utils.ImageRequester;
import com.google.codelabs.mdc.java.shrine.utils.adapters.ImageAdapter;

public class FullScreamImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_fullscream_imageview_activity);
        PhotoView fullScreamImgeView = findViewById(R.id.fullscream_imageview);
        Intent callingActivityIntent = getIntent();
        if (callingActivityIntent != null) {
            String imageName = (String) callingActivityIntent.getSerializableExtra("image");
            ImageRequester.getInstance().setImageFromDrawable(fullScreamImgeView, imageName);
        }
    }
}
