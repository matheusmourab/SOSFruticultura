package com.google.codelabs.mdc.java.shrine.utils.holders;

import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.codelabs.mdc.java.shrine.R;

public class ProductCardViewHolder extends RecyclerView.ViewHolder {

    public ImageView productImage;
    public TextView productTitle;
    public TextView productDescription;
    public CheckBox favoriteButton;
    public MaterialCardView productCard;

    public ProductCardViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.product_image);
        productTitle = itemView.findViewById(R.id.product_title);
        productDescription = itemView.findViewById(R.id.product_description);
        favoriteButton = itemView.findViewById(R.id.favorite_button);
        productCard = itemView.findViewById(R.id.product_card);
    }
}
