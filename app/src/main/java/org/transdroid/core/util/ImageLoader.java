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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/**
 * Modern image loading utility using Glide.
 * Replaces the deprecated Universal Image Loader.
 */
public class ImageLoader {
    
    private static ImageLoader instance;
    private final Context context;
    
    private ImageLoader(Context context) {
        this.context = context.getApplicationContext();
    }
    
    public static ImageLoader getInstance(Context context) {
        if (instance == null) {
            instance = new ImageLoader(context);
        }
        return instance;
    }
    
    /**
     * Load image from URL into ImageView
     */
    public void loadImage(String url, ImageView imageView) {
        loadImage(url, imageView, null, null);
    }
    
    /**
     * Load image from URL into ImageView with placeholder and error drawable
     */
    public void loadImage(String url, ImageView imageView, 
                         @DrawableRes Integer placeholderRes, 
                         @DrawableRes Integer errorRes) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        
        if (placeholderRes != null) {
            requestBuilder.placeholder(placeholderRes);
        }
        
        if (errorRes != null) {
            requestBuilder.error(errorRes);
        }
        
        requestBuilder.into(imageView);
    }
    
    /**
     * Load image from URL into ImageView with callback
     */
    public void loadImage(String url, ImageView imageView, ImageLoadCallback callback) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, 
                                               Target<Drawable> target, boolean isFirstResource) {
                        if (callback != null) {
                            callback.onError(e);
                        }
                        return false;
                    }
                    
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, 
                                                 Target<Drawable> target, DataSource dataSource, 
                                                 boolean isFirstResource) {
                        if (callback != null) {
                            callback.onSuccess(resource);
                        }
                        return false;
                    }
                })
                .into(imageView);
    }
    
    /**
     * Load image as Bitmap
     */
    public void loadImageAsBitmap(String url, BitmapLoadCallback callback) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, 
                                               @Nullable Transition<? super Bitmap> transition) {
                        if (callback != null) {
                            callback.onSuccess(resource);
                        }
                    }
                    
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (callback != null) {
                            callback.onError(new Exception("Failed to load bitmap"));
                        }
                    }
                    
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Ignore
                    }
                });
    }
    
    /**
     * Load image from resource ID
     */
    public void loadImage(@DrawableRes int resourceId, ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .into(imageView);
    }
    
    /**
     * Load image from file path
     */
    public void loadImageFromFile(String filePath, ImageView imageView) {
        Glide.with(context)
                .load("file://" + filePath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
    
    /**
     * Clear memory cache
     */
    public void clearMemoryCache() {
        Glide.get(context).clearMemory();
    }
    
    /**
     * Clear disk cache
     */
    public void clearDiskCache() {
        Glide.get(context).clearDiskCache();
    }
    
    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        clearMemoryCache();
        clearDiskCache();
    }
    
    /**
     * Preload image
     */
    public void preloadImage(String url) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();
    }
    
    public interface ImageLoadCallback {
        void onSuccess(Drawable drawable);
        void onError(Exception e);
    }
    
    public interface BitmapLoadCallback {
        void onSuccess(Bitmap bitmap);
        void onError(Exception e);
    }
} 