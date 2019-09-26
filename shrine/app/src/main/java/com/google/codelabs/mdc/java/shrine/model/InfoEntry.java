package com.google.codelabs.mdc.java.shrine.model;

import android.content.res.Resources;
import android.util.Log;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
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

@DatabaseTable(tableName="info")
public class InfoEntry implements Serializable {
    private static final String TAG = InfoEntry.class.getSimpleName();

    @SerializedName("id")
    @DatabaseField(allowGeneratedIdInsert=true, generatedId=true)
    private int id;

    @SerializedName("title")
    @DatabaseField
    private String title;

    @SerializedName("imageName")
    @DatabaseField
    private String imageName;

    @SerializedName("imageDescription")
    @DatabaseField
    private String imageDescription;

    @SerializedName("description")
    @DatabaseField
    private String description;

    @SerializedName("culture")
    @DatabaseField
    private String culture;

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(foreign = true)
    private ProductEntry product;

    public InfoEntry() {}

    public InfoEntry(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public static ArrayList<InfoEntry> getInitialGeneralInfoList(Resources resources) {
        InputStream inputStream = resources.openRawResource(R.raw.infolist);
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
        String jsonInfoListString = writer.toString();
        Gson gson = new Gson();
        Type infoListType = new TypeToken<ArrayList<InfoEntry>>() {}.getType();
        return gson.fromJson(jsonInfoListString, infoListType);
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductEntry getProduct() {
        return product;
    }

    public void setProduct(ProductEntry product) {
        this.product = product;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }

    @Override
    public String toString() {
        return this.title;
    }
}