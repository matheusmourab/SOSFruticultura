package com.google.codelabs.mdc.java.shrine.utils.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.model.CultureEntry;
import com.google.codelabs.mdc.java.shrine.utils.holders.CultureCardViewHolder;
import com.google.codelabs.mdc.java.shrine.utils.ImageRequester;

import java.util.ArrayList;

public class CultureCardRecyclerViewAdapter extends RecyclerView.Adapter<CultureCardViewHolder> {

    private ArrayList<CultureEntry> cultureList;

    public CultureCardRecyclerViewAdapter(ArrayList<CultureEntry> cultureList) {
        if (cultureList != null) {
            this.cultureList = new ArrayList<>(cultureList);
        } else {
            this.cultureList = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public CultureCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shr_culture_card, parent, false);
        return new CultureCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull CultureCardViewHolder holder, int position) {
        if (this.cultureList != null && position < this.cultureList.size()) {
            CultureEntry culture = this.cultureList.get(position);

            holder.cultureCard.setTag(culture);
            holder.cultureName.setText(culture.getName());
            if (culture.getImage() != null) {
                ImageRequester.getInstance().setImageFromFile(holder.cultureImage, culture.getImage().getImageName());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (this.cultureList == null)
            return 0;
        return this.cultureList.size();
    }

    public RecyclerView.Adapter setList(ArrayList<CultureEntry> cultureList) {
        this.cultureList = cultureList;
        return this;
    }

    public ArrayList<CultureEntry> getList() {
        return this.cultureList;
    }
}
