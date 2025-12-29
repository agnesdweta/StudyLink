package com.example.studylink.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private static Retrofit retrofit;

    // Untuk endpoint TANPA token (login, register, schedules, dll)
    public static ApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    // Untuk endpoint DENGAN token (assignment, course, dll)
    public static ApiService getServiceWithToken(String token) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain ->
                        chain.proceed(
                                chain.request().newBuilder()
                                        .addHeader("Authorization", "Bearer " + token)
                                        .build()
                        )
                )
                .build();

        Retrofit retrofitWithToken = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofitWithToken.create(ApiService.class);
    }
}
