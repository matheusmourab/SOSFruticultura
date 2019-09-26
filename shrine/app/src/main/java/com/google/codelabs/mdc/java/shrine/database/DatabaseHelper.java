package com.google.codelabs.mdc.java.shrine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.codelabs.mdc.java.shrine.model.CultureEntry;
import com.google.codelabs.mdc.java.shrine.model.ImageEntry;
import com.google.codelabs.mdc.java.shrine.model.InfoEntry;
import com.google.codelabs.mdc.java.shrine.model.ProductEntry;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "manual_do_abacaxi.sqlite";

    private static final int DATABASE_VERSION = 11;

    private Dao<ProductEntry, Integer> productDao = null;
    private Dao<InfoEntry, Integer> infoDao = null;
    private Dao<ImageEntry, Integer> imageDao = null;
    private Dao<CultureEntry, Integer> cultureDao = null;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ProductEntry.class);
            TableUtils.createTable(connectionSource, InfoEntry.class);
            TableUtils.createTable(connectionSource, ImageEntry.class);
            TableUtils.createTable(connectionSource, CultureEntry.class);
        } catch (java.sql.SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ProductEntry.class, true);
            TableUtils.dropTable(connectionSource, InfoEntry.class, true);
            TableUtils.dropTable(connectionSource, ImageEntry.class, true);
            TableUtils.dropTable(connectionSource, CultureEntry.class, true);
            this.onCreate(database, connectionSource);
        } catch (java.sql.SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "exception during onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        super.close();
    }

    Dao<ProductEntry, Integer> getProductDao() {
        if (productDao == null) {
            try {
                productDao = getDao(ProductEntry.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return productDao;
    }

    Dao<InfoEntry, Integer> getInfoDao() {
        if (infoDao == null) {
            try {
                infoDao = getDao(InfoEntry.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return infoDao;
    }

    Dao<ImageEntry, Integer> getImageDao() {
        if (imageDao == null) {
            try {
                imageDao = getDao(ImageEntry.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return imageDao;
    }

    Dao<CultureEntry, Integer> getCultureDao() {
        if (cultureDao == null) {
            try {
                cultureDao = getDao(CultureEntry.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return cultureDao;
    }
}