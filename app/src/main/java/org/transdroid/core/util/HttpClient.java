/*
 * Copyright 2010-2024 Eric Kok et al.
 *
 * Transdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Transdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Transdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.transdroid.core.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.okhttp3.Call;
import com.squareup.okhttp3.Callback;
import com.squareup.okhttp3.Credentials;
import com.squareup.okhttp3.FormBody;
import com.squareup.okhttp3.HttpUrl;
import com.squareup.okhttp3.MediaType;
import com.squareup.okhttp3.OkHttpClient;
import com.squareup.okhttp3.Request;
import com.squareup.okhttp3.RequestBody;
import com.squareup.okhttp3.Response;
import com.squareup.okhttp3.ResponseBody;
import com.squareup.okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

/**
 * Modern HTTP client utility using OkHttp.
 * Replaces the deprecated Apache HTTP legacy library.
 */
public class HttpClient {
    
    private static final String TAG = "HttpClient";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final MediaType FORM = MediaType.get("application/x-www-form-urlencoded");
    
    private final OkHttpClient client;
    
    public HttpClient() {
        this(false);
    }
    
    public HttpClient(boolean enableLogging) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cookieJar(new com.squareup.okhttp3.CookieJar() {
                    private final CookieManager cookieManager = new CookieManager();
                    
                    {
                        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                    }
                    
                    @Override
                    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<com.squareup.okhttp3.Cookie> cookies) {
                        for (com.squareup.okhttp3.Cookie cookie : cookies) {
                            cookieManager.getCookieStore().add(url.uri(), cookie);
                        }
                    }
                    
                    @Override
                    public List<com.squareup.okhttp3.Cookie> loadForRequest(@NonNull HttpUrl url) {
                        List<com.squareup.okhttp3.Cookie> cookies = new ArrayList<>();
                        for (java.net.HttpCookie cookie : cookieManager.getCookieStore().get(url.uri())) {
                            cookies.add(com.squareup.okhttp3.Cookie.parse(url, cookie.toString()));
                        }
                        return cookies;
                    }
                });
        
        if (enableLogging) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
        
        this.client = builder.build();
    }
    
    /**
     * Perform a GET request
     */
    public void get(String url, @Nullable Map<String, String> headers, 
                   @Nullable String username, @Nullable String password,
                   @NonNull HttpCallback callback) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
        
        if (username != null && password != null) {
            requestBuilder.addHeader("Authorization", Credentials.basic(username, password));
        }
        
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }
            
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        callback.onSuccess(response.code(), body.string());
                    } else {
                        callback.onSuccess(response.code(), "");
                    }
                }
            }
        });
    }
    
    /**
     * Perform a POST request with form data
     */
    public void postForm(String url, @Nullable Map<String, String> headers,
                        @Nullable Map<String, String> formData,
                        @Nullable String username, @Nullable String password,
                        @NonNull HttpCallback callback) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (formData != null) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(formBuilder.build());
        
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
        
        if (username != null && password != null) {
            requestBuilder.addHeader("Authorization", Credentials.basic(username, password));
        }
        
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }
            
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        callback.onSuccess(response.code(), body.string());
                    } else {
                        callback.onSuccess(response.code(), "");
                    }
                }
            }
        });
    }
    
    /**
     * Perform a POST request with JSON data
     */
    public void postJson(String url, @Nullable Map<String, String> headers,
                        String jsonData,
                        @Nullable String username, @Nullable String password,
                        @NonNull HttpCallback callback) {
        RequestBody body = RequestBody.create(jsonData, JSON);
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);
        
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
        
        if (username != null && password != null) {
            requestBuilder.addHeader("Authorization", Credentials.basic(username, password));
        }
        
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }
            
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        callback.onSuccess(response.code(), body.string());
                    } else {
                        callback.onSuccess(response.code(), "");
                    }
                }
            }
        });
    }
    
    /**
     * Download a file
     */
    public void downloadFile(String url, @Nullable Map<String, String> headers,
                           @Nullable String username, @Nullable String password,
                           @NonNull DownloadCallback callback) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
        
        if (username != null && password != null) {
            requestBuilder.addHeader("Authorization", Credentials.basic(username, password));
        }
        
        Request request = requestBuilder.build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }
            
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        callback.onSuccess(response.code(), body.byteStream(), body.contentLength());
                    } else {
                        callback.onFailure(new IOException("Empty response body"));
                    }
                }
            }
        });
    }
    
    /**
     * Synchronous GET request (use with caution on main thread)
     */
    public String getSync(String url, @Nullable Map<String, String> headers,
                         @Nullable String username, @Nullable String password) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
        
        if (username != null && password != null) {
            requestBuilder.addHeader("Authorization", Credentials.basic(username, password));
        }
        
        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            try (ResponseBody body = response.body()) {
                return body != null ? body.string() : "";
            }
        }
    }
    
    public interface HttpCallback {
        void onSuccess(int statusCode, String response);
        void onFailure(IOException e);
    }
    
    public interface DownloadCallback {
        void onSuccess(int statusCode, InputStream inputStream, long contentLength);
        void onFailure(IOException e);
    }
} 