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

/**
 * Room entity for server settings.
 * Replaces the ORMLite ServerSetting entity.
 */
@Entity(tableName = "server_settings")
public class ServerSetting {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    private String name;
    
    @NonNull
    private String type;
    
    @NonNull
    private String address;
    
    private int port;
    
    private String username;
    
    private String password;
    
    private String folder;
    
    private boolean useAuthentication;
    
    private boolean ssl;
    
    private String extraFolder;
    
    private String localNetwork;
    
    private boolean lastUsed;
    
    public ServerSetting(@NonNull String name, @NonNull String type, @NonNull String address, int port) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.port = port;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @NonNull
    public String getName() {
        return name;
    }
    
    public void setName(@NonNull String name) {
        this.name = name;
    }
    
    @NonNull
    public String getType() {
        return type;
    }
    
    public void setType(@NonNull String type) {
        this.type = type;
    }
    
    @NonNull
    public String getAddress() {
        return address;
    }
    
    public void setAddress(@NonNull String address) {
        this.address = address;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFolder() {
        return folder;
    }
    
    public void setFolder(String folder) {
        this.folder = folder;
    }
    
    public boolean isUseAuthentication() {
        return useAuthentication;
    }
    
    public void setUseAuthentication(boolean useAuthentication) {
        this.useAuthentication = useAuthentication;
    }
    
    public boolean isSsl() {
        return ssl;
    }
    
    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
    
    public String getExtraFolder() {
        return extraFolder;
    }
    
    public void setExtraFolder(String extraFolder) {
        this.extraFolder = extraFolder;
    }
    
    public String getLocalNetwork() {
        return localNetwork;
    }
    
    public void setLocalNetwork(String localNetwork) {
        this.localNetwork = localNetwork;
    }
    
    public boolean isLastUsed() {
        return lastUsed;
    }
    
    public void setLastUsed(boolean lastUsed) {
        this.lastUsed = lastUsed;
    }
} 