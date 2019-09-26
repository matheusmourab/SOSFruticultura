package com.google.codelabs.mdc.java.shrine.model;

import android.content.res.Resources;
import android.util.Log;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@DatabaseTable(tableName="product")
public class ProductEntry implements Serializable {
    private static final String TAG = ProductEntry.class.getSimpleName();

    @SerializedName("id")
    @DatabaseField(allowGeneratedIdInsert=true, generatedId=true)
    private int id;

    @SerializedName("title")
    @DatabaseField
    private String title;

    @SerializedName("images")
    @ForeignCollectionField
    private Collection<ImageEntry> images;

    @SerializedName("cientificName")
    @DatabaseField
    private String cientificName;

    @SerializedName("culture")
    @DatabaseField
    private String culture;

    @SerializedName("type")
    @DatabaseField
    private String type;

    @SerializedName("informations")
    @ForeignCollectionField
    private Collection<InfoEntry> informations;

    @SerializedName("isFavorite")
    @DatabaseField
    private boolean isFavorite;

    public ProductEntry() {}

    public ProductEntry(String title, ArrayList<ImageEntry> images, String cientificName, String type, ArrayList<InfoEntry> informations, String culture) {
        this.title = title;
        this.images = images;
        this.cientificName = cientificName;
        this.type = type;
        this.informations = informations;
        this.isFavorite = false;
        this.culture = culture;
    }

    public static List<ProductEntry> getInitialProductList(Resources resources) {
        InputStream inputStream = resources.openRawResource(R.raw.products);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int pointer;
            while ((pointer = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, pointer);
            }
        } catch (IOException exception) {
            Log.e(TAG, "Error writing/reading from the JSON file.", exception);
        } finally {
            try {
                inputStream.close();
            } catch (IOException exception) {
                Log.e(TAG, "Error closing the input stream.", exception);
            }
        }
        String jsonProductsString = writer.toString();
        Gson gson = new Gson();
        Type productListType = new TypeToken<ArrayList<ProductEntry>>() {
        }.getType();
        return gson.fromJson(jsonProductsString, productListType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<ImageEntry> getImages() {
        return new ArrayList<>(this.images);
    }

    public String getCientificName() {
        return cientificName;
    }

    public String getType() {
        return type;
    }

    public String getCulture() {
        return culture;
    }

    public ArrayList<InfoEntry> getInformations() {
        return new ArrayList<>(informations);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProductEntry && ((ProductEntry) obj).getTitle().toLowerCase().equals(this.getTitle().toLowerCase());
    }
}