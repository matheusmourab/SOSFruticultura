package com.google.codelabs.mdc.java.shrine.utils;

import android.graphics.Bitmap;

import com.google.codelabs.mdc.java.shrine.model.CultureEntry;
import com.google.codelabs.mdc.java.shrine.model.InfoEntry;
import com.google.codelabs.mdc.java.shrine.model.ProductEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class ServerComunication {

    private static ServerComunication instance;
    public static final String BASE_URL = "http://manualdoabacaxi.us-east-2.elasticbeanstalk.com/";
    //public static final String BASE_URL = "http://192.168.0.102:8080/manualDoAbacaxiServer/";
    private final RequestService service;

    private ServerComunication() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.service = retrofit.create(RequestService.class);
    }

    public static ServerComunication getInstance() {
        if (instance == null) {
            instance = new ServerComunication();
        }
        return instance;
    }

    public ArrayList<ProductEntry> requestProductList(String cultureName) {
        Response<List<ProductEntry>> response;
        try {
            if (cultureName == null) {
                response = service.requestProductList().execute();
            } else {
                response = service.requestProductListByCulture(cultureName.toLowerCase()).execute();
            }
            if (response.isSuccessful() && response.body() != null) {
                return new ArrayList<>(response.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<CultureEntry> requestCultureList() {
        Response<List<CultureEntry>> response;
        try {
            response = service.requestCultureList().execute();
            if (response.isSuccessful() && response.body() != null) {
                return new ArrayList<>(response.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<InfoEntry> requestInfoList() {
        Response<List<InfoEntry>> response;
        try {
            response = service.requestInfoList().execute();
            if (response.isSuccessful() && response.body() != null) {
                return new ArrayList<>(response.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean sendNewSolicitation(String name, String email, String city, String description, Bitmap imageBitmap) {
        Response<String> response;
        try {
            response = service.requestHelp(
                    this.makePartFromString("name", name),
                    this.makePartFromString("email", email),
                    this.makePartFromString("city", city),
                    this.makePartFromString("description", description),
                    this.makePartFromBitmap("image", imageBitmap)).execute();
            if (response.isSuccessful() && response.body() != null) {
                return Boolean.valueOf(response.body());
            }
        } catch (IOException e) {
            System.out.println("Erro na conexão com o servidor.");
            e.printStackTrace();
        }
        return false;
    }

    private MultipartBody.Part makePartFromDouble(String fieldName, Double d) {
        if (d == null) {
            return null;
        }
        return makePartFromString(fieldName, Double.toString(d));
    }

    private MultipartBody.Part makePartFromBitmap(String fieldName, Bitmap imageBitmap) {
        if (imageBitmap == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return MultipartBody.Part.createFormData(fieldName, null, RequestBody.create(MediaType.parse("image/*"), imageBytes));
    }

    private MultipartBody.Part makePartFromString(String fieldName, String value) {
        if (value == null || fieldName == null) {
            return null;
        }
        return MultipartBody.Part.createFormData(fieldName, value);
    }
}

interface RequestService {
    @GET("rest/getdata/products")
    Call<List<ProductEntry>> requestProductList();

    @GET("rest/getdata/infos")
    Call<List<InfoEntry>> requestInfoList();

    @GET("rest/getdata/cultures")
    Call<List<CultureEntry>> requestCultureList();

    @GET("rest/getdata/products")
    Call<List<ProductEntry>> requestProductListByCulture(@Query("culture") String culture);

    @Multipart
    @POST("help")
    Call<String> requestHelp(@Part MultipartBody.Part name,
                             @Part MultipartBody.Part email,
                             @Part MultipartBody.Part city,
                             @Part MultipartBody.Part description,
                             @Part MultipartBody.Part image);
}