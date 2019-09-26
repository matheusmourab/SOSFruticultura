package com.google.codelabs.mdc.java.shrine.utils.holders;

import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.codelabs.mdc.java.shrine.R;

public class CultureCardViewHolder extends RecyclerView.ViewHolder {

    public ImageView cultureImage;
    public TextView cultureName;
    public MaterialCardView cultureCard;

    public CultureCardViewHolder(@NonNull View itemView) {
        super(itemView);
        cultureImage = itemView.findViewById(R.id.culture_image);
        cultureName = itemView.findViewById(R.id.culture_name);
        cultureCard = itemView.findViewById(R.id.culture_card);
    }
}
