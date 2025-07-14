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
package org.transdroid.core.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Room entity for search history.
 * Replaces the ORMLite SearchHistory entity.
 */
@Entity(tableName = "search_history")
public class SearchHistory {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String query;
    
    @NonNull
    private String site;
    
    @NonNull
    private Date timestamp;
    
    public SearchHistory(@NonNull String query, @NonNull String site) {
        this.query = query;
        this.site = site;
        this.timestamp = new Date();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @NonNull
    public String getQuery() {
        return query;
    }
    
    public void setQuery(@NonNull String query) {
        this.query = query;
    }
    
    @NonNull
    public String getSite() {
        return site;
    }
    
    public void setSite(@NonNull String site) {
        this.site = site;
    }
    
    @NonNull
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(@NonNull Date timestamp) {
        this.timestamp = timestamp;
    }
} 