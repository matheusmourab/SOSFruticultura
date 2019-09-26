package com.google.codelabs.mdc.java.shrine.utils.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.codelabs.mdc.java.shrine.utils.holders.ProductCardViewHolder;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.utils.ImageRequester;
import com.google.codelabs.mdc.java.shrine.model.ProductEntry;

import java.util.ArrayList;

public class ProductCardRecyclerViewAdapter extends RecyclerView.Adapter<ProductCardViewHolder> {

    private ArrayList<ProductEntry> productList;

    public ProductCardRecyclerViewAdapter(ArrayList<ProductEntry> productList) {
        this.productList = new ArrayList<>(productList);
    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shr_product_card, parent, false);
        return new ProductCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardViewHolder holder, int position) {
        if (this.productList != null && position < this.productList.size()) {
            ProductEntry product = this.productList.get(position);

            holder.productCard.setTag(product);
            holder.favoriteButton.setTag(product);
            holder.favoriteButton.setChecked(product.isFavorite());
            holder.productTitle.setText(product.getTitle());
            holder.productDescription.setText(product.getType());

            if (product.getImages() != null && product.getImages().size() > 0) {
                ImageRequester.getInstance().setImageFromFile(holder.productImage, product.getImages().get(0).getImageName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.productList.size();
    }

    public RecyclerView.Adapter setList(ArrayList<ProductEntry> productList) {
        this.productList = productList;
        return this;
    }

    public ArrayList<ProductEntry> getList() {
        return this.productList;
    }
}
