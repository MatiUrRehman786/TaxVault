package com.sar.taxvault.retrofit;

import com.androidnetworking.interceptors.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Controller {

    public static final String Base_Url = "https://buzzwaretech.com";
    public static Retrofit retrofit = null;
    public static ApiClient getApi() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder mOkHttpClient = new OkHttpClient.Builder();
        mOkHttpClient.connectTimeout(50, TimeUnit.SECONDS).writeTimeout(1, TimeUnit.MINUTES).readTimeout(50, TimeUnit.SECONDS);
        mOkHttpClient.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        Retrofit.Builder mBuilder = new Retrofit.Builder().baseUrl(Base_Url).addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create());
        Retrofit mRetrofit = mBuilder.client(mOkHttpClient.addInterceptor(httpLoggingInterceptor).build()).build();
        ApiClient mClient = mRetrofit.create(ApiClient.class);
        return mClient;
    }

}
