package com.google.codelabs.mdc.java.shrine.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName="image")
public class ImageEntry implements Serializable {
    private static final String TAG = ImageEntry.class.getSimpleName();

    @Expose(serialize = false, deserialize = false)
    @DatabaseField(foreign = true)
    private ProductEntry product;

    @SerializedName("id")
    @DatabaseField(allowGeneratedIdInsert=true, generatedId=true)
    private int id;

    @SerializedName("imageName")
    @DatabaseField
    private String imageName;

    @SerializedName("imageDescription")
    @DatabaseField
    private String imageDescription;

    public ImageEntry() {}

    public ImageEntry(String imageName, String imageDescription) {
        this.imageName = imageName;
        this.imageDescription = imageDescription;
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

    public ProductEntry getProduct() {
        return product;
    }

    public void setProduct(ProductEntry product) {
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return imageName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ImageEntry && ((ImageEntry) obj).getImageName().equals(this.getImageName());
    }
}
