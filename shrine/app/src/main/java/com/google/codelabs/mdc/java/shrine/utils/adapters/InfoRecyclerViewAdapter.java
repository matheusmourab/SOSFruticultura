package com.google.codelabs.mdc.java.shrine.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.codelabs.mdc.java.shrine.view.FullScreamImageViewActivity;
import com.google.codelabs.mdc.java.shrine.utils.ImageRequester;
import com.google.codelabs.mdc.java.shrine.utils.holders.InfoViewHolder;
import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.model.InfoEntry;

import java.util.ArrayList;
import java.util.List;

public class InfoRecyclerViewAdapter extends RecyclerView.Adapter<InfoViewHolder> {

    private final Context context;
    private List<InfoEntry> infoList;
    private List<InfoViewHolder> infoViewHolderList;

    public InfoRecyclerViewAdapter(Context context, List<InfoEntry> infoList) {
        this.context = context;
        this.infoList = infoList;
        this.infoViewHolderList = new ArrayList<>();
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shr_info, parent, false);
        return new InfoViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        if (infoList != null && position < infoList.size()) {
            final InfoEntry info = infoList.get(position);
            holder.titleText.setText(info.getTitle());
            holder.descriptionText.setText(info.getDescription());
            holder.title.setTag(R.string.itemDescriptionView, holder.description);
            holder.title.setTag(R.string.itemTitleDropDownIconView, holder.titleDropDownIcon);
            this.infoViewHolderList.add(position, holder);

            if (info.getImageName() != null) {
                ImageRequester.getInstance().setImageFromDrawable(holder.descriptionImage, info.getImageName());
                holder.descriptionImage.setVisibility(View.VISIBLE);
                //holder.descriptionImageTitle.setVisibility(View.VISIBLE);
                holder.descriptionImageTitle.setText(info.getImageDescription());
                holder.descriptionImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent fullScreamIntent = new Intent(context, FullScreamImageViewActivity.class);
                        fullScreamIntent.putExtra("image", info.getImageName());
                        context.startActivity(fullScreamIntent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (infoList == null)
            return 0;
        return infoList.size();
    }

    public InfoViewHolder getInfoViewHolder(int i) {
        return infoViewHolderList.get(i);
    }

    public RecyclerView.Adapter setList(ArrayList<InfoEntry> filteredList) {
        this.infoList = filteredList;
        return this;
    }

    public ArrayList<InfoEntry> getList() {
        return new ArrayList<>(infoList);
    }
}
