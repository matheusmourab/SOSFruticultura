package com.google.codelabs.mdc.java.shrine.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.model.ImageEntry;
import com.google.codelabs.mdc.java.shrine.view.FullScreamViewPagerActivity;
import com.google.codelabs.mdc.java.shrine.utils.ImageRequester;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter implements Serializable {

    private Context context;
    private ArrayList<ImageEntry> images;
    private int position = 0;
    private boolean isFullScream;
    private TextView imageLegendTextView;

    public ImageAdapter(Context context, ArrayList<ImageEntry> images, TextView imageLegendTextView, boolean isFullScream) {
        this.context = context;
        this.images = images;
        this.isFullScream = isFullScream;
        this.imageLegendTextView = imageLegendTextView;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(linearLayout);

        final PhotoView photoView = new PhotoView(context);
        ImageRequester.getInstance().setImageFromFile(photoView, images.get(position).getImageName());
        photoView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        photoView.setScaleType(ImageView.ScaleType.CENTER);
        if (!isFullScream) {
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fullScreamIntent = new Intent(container.getContext(), FullScreamViewPagerActivity.class);
                    fullScreamIntent.putExtra("images", new ArrayList<>(images));
                    fullScreamIntent.putExtra("position", position);
                    container.getContext().startActivity(fullScreamIntent);

                }
            });
        }
        linearLayout.addView(photoView);

        TextView textView = new TextView(context);
        textView.setText(images.get(position).getImageDescription());
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
        textView.setGravity(Gravity.CENTER);
        linearLayout.addView(textView);

        return linearLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object obj) {
        container.removeView((View) obj);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        this.position = position;
        if (imageLegendTextView != null) {
            imageLegendTextView.setText(images.get(position).getImageDescription());
        }
    }
}
