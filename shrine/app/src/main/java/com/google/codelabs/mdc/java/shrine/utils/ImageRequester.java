package com.google.codelabs.mdc.java.shrine.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.codelabs.mdc.java.shrine.application.ShrineApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageRequester {
    private static ImageRequester instance = null;
    public static final String IMAGESPATH = Environment.getExternalStorageDirectory().toString() + File.separator + "ManualDoAbacaxi";
    private final Context context;

    private ImageRequester() {
        context = ShrineApplication.getAppContext();
    }

    public static ImageRequester getInstance() {
        if (instance == null) {
            instance = new ImageRequester();
        }
        return instance;
    }

    public Bitmap setImageFromDrawable(ImageView productImage, String imageName) {
        int resourceId = productImage.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        if (imageBitmap != null)
            Glide.with(context).load(imageBitmap).into(productImage);
        return imageBitmap;
    }

    public void setImageFromFile(ImageView productImage, String imageName) {
        File file = new File(IMAGESPATH, imageName);
        if (file.exists()) {
            Glide.with(context).load(file).into(productImage);
        } else {
            Bitmap imageBitmap = this.setImageFromDrawable(productImage, imageName);
            if (imageBitmap != null && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    FileOutputStream fOut = new FileOutputStream(file);
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setImageFromByteArray(ImageView productImage, byte[] imageBytes) {
        Glide.with(context).load(imageBytes).into(productImage);
    }
}