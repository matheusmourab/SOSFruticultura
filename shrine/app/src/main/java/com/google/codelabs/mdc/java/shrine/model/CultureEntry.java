package com.google.codelabs.mdc.java.shrine.model;

import android.content.res.Resources;
import android.util.Log;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
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
import java.util.List;

@DatabaseTable(tableName="culture")
public class CultureEntry implements Serializable {
    private static final String TAG = CultureEntry.class.getSimpleName();

    @SerializedName("id")
    @DatabaseField(allowGeneratedIdInsert=true, generatedId=true)
    private int id;

    @SerializedName("name")
    @DatabaseField
    private String name;

    @SerializedName("image")
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ImageEntry image;

    public CultureEntry() {}

    public CultureEntry(String name, ImageEntry image) {
        this.name = name;
        this.image = image;
    }

    public static List<CultureEntry> getInitialCultureList(Resources resources) {
        InputStream inputStream = resources.openRawResource(R.raw.cultures);
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
        String jsonCulturesString = writer.toString();
        Gson gson = new Gson();
        Type cultureListType = new TypeToken<ArrayList<CultureEntry>>() {
        }.getType();
        return gson.fromJson(jsonCulturesString, cultureListType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ImageEntry getImage() {
        return this.image;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CultureEntry && ((CultureEntry) obj).getName().toLowerCase().equals(this.getName().toLowerCase());
    }
}