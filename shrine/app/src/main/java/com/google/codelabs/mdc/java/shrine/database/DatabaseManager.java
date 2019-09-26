package com.google.codelabs.mdc.java.shrine.database;

import android.content.Context;

import com.google.codelabs.mdc.java.shrine.model.CultureEntry;
import com.google.codelabs.mdc.java.shrine.model.ImageEntry;
import com.google.codelabs.mdc.java.shrine.model.InfoEntry;
import com.google.codelabs.mdc.java.shrine.model.ProductEntry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static DatabaseManager instance;
    private DatabaseHelper helper;

    public static void init(Context ctx) {
        if (null == instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

    public void close() {
        getHelper().close();
    }

    public ArrayList<CultureEntry> findAllCultures() {
        List<CultureEntry> list = null;
        try {
            list = getHelper().getCultureDao().queryBuilder().orderBy("id", true).selectRaw().query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (list != null) {
            return new ArrayList<>(list);
        }
        return null;
    }

    public ArrayList<ProductEntry> findAllProducts() {
        List<ProductEntry> list = null;
        try {
            list = getHelper().getProductDao().queryBuilder().orderBy("id", true).selectRaw().query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (list != null) {
            return new ArrayList<>(list);
        }
        return null;
    }

    public ArrayList<ProductEntry> findProducts(String culture) {
        List<ProductEntry> list = null;
        if (culture == null) {
            return new ArrayList<>();
        }
        try {
            list = getHelper().getProductDao().queryBuilder().orderBy("id", true).selectRaw().where().eq("culture", culture).query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (list != null) {
            ArrayList<ProductEntry> productList = new ArrayList<>(list);
            return productList;
        }
        return null;
    }

    public ProductEntry findProductByName(String productName) {
        List<ProductEntry> list = null;
        try {
            list = getHelper().getProductDao().queryBuilder().orderBy("id", true).selectRaw().where().eq("title", productName).query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public ArrayList<ProductEntry> findAllFavoriteProducts() {
        List<ProductEntry> list = null;
        try {
            list = getHelper().getProductDao().queryBuilder().orderBy("id", true).selectRaw().where().eq("isFavorite", true).query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (list != null) {
            return new ArrayList<>(list);
        }
        return null;
    }

    public ArrayList<ProductEntry> findFavoriteProducts(String culture) {
        List<ProductEntry> list = null;
        if (culture == null) {
            return this.findAllFavoriteProducts();
        }
        try {
            list = getHelper().getProductDao().queryBuilder().orderBy("id", true).selectRaw().where().eq("isFavorite", true).and().eq("culture", culture).query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (list != null) {
            return new ArrayList<>(list);
        }
        return null;
    }

    public ArrayList<InfoEntry> findInfos() {
        List<InfoEntry> list = null;
        try {
            list = getHelper().getInfoDao().queryBuilder().orderBy("id", true).query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (list != null) {
            return new ArrayList<>(list);
        }
        return null;
    }

    public ArrayList<InfoEntry> findGeneralInfos(String culture) {
        List<InfoEntry> list = null;
        try {
            System.out.println(getHelper().getInfoDao().queryBuilder().orderBy("id", true).selectRaw().query());
            if (culture == null) {
                list = getHelper().getInfoDao().queryBuilder().orderBy("id", true).selectRaw().where().isNull("product_id").and().isNull("culture").query();
            } else {
                list = getHelper().getInfoDao().queryBuilder().orderBy("id", true).selectRaw().where().isNull("product_id").and().eq("culture", culture).query();
                System.out.println(list);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (list != null) {
            return new ArrayList<>(list);
        }
        return null;
    }

    public ArrayList<ImageEntry> findImages() {
        List<ImageEntry> list = null;
        try {
            list = getHelper().getImageDao().queryBuilder().orderBy("id", false).query();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        if (list != null) {
            return new ArrayList<>(list);
        }
        return null;
    }

    public void addProduct(ProductEntry product) {
        try {
            getHelper().getProductDao().create(product);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCulture(CultureEntry culture) {
        try {
            getHelper().getCultureDao().create(culture);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addInfo(InfoEntry info) {
        try {
            getHelper().getInfoDao().create(info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addImage(ImageEntry image) {
        try {
            getHelper().getImageDao().create(image);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProducts(List<ProductEntry> products) {
        try {
            getHelper().getProductDao().delete(products);
            for (ProductEntry product : products) {
                this.deleteImages(product.getImages());
                this.deleteInfos(product.getInformations());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCultures(List<CultureEntry> cultures) {
        try {
            getHelper().getCultureDao().delete(cultures);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteInfos(List<InfoEntry> infos) {
        try {
            getHelper().getInfoDao().delete(infos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGeneralInfos() {
        try {
            List<InfoEntry> allGeneralInfos = getHelper().getInfoDao().queryBuilder().selectRaw().where().isNull("product_id").query();
            getHelper().getInfoDao().delete(allGeneralInfos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteImages(List<ImageEntry> image) {
        try {
            getHelper().getImageDao().delete(image);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProduct(ProductEntry product) {
        try {
            getHelper().getProductDao().update(product);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCulture(CultureEntry culture) {
        try {
            getHelper().getCultureDao().update(culture);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateInfo(InfoEntry info) {
        try {
            getHelper().getInfoDao().update(info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateImage(ImageEntry image) {
        try {
            getHelper().getImageDao().update(image);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
