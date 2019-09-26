package com.google.codelabs.mdc.java.shrine.utils.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.codelabs.mdc.java.shrine.R;

public class InfoViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout title;
    public TextView titleText;
    public ImageView titleDropDownIcon;
    public LinearLayout description;
    public ImageView descriptionImage;
    public TextView descriptionImageTitle;
    public TextView descriptionText;

    public InfoViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.info_title);
        titleText = itemView.findViewById(R.id.info_title_text);
        titleDropDownIcon = itemView.findViewById(R.id.info_title_dropdown_icon);
        description = itemView.findViewById(R.id.info_description);
        descriptionImage = itemView.findViewById(R.id.info_description_image);
        descriptionImageTitle = itemView.findViewById(R.id.info_description_image_title);
        descriptionText = itemView.findViewById(R.id.info_description_text);
    }
}
