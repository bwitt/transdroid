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
package org.transdroid.core.data.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import org.transdroid.core.data.entity.ServerSetting;

import java.util.List;

/**
 * Room DAO for server settings operations.
 */
@Dao
public interface ServerSettingDao {
    
    @Query("SELECT * FROM server_settings ORDER BY name ASC")
    LiveData<List<ServerSetting>> getAllServerSettings();
    
    @Query("SELECT * FROM server_settings WHERE id = :id")
    LiveData<ServerSetting> getServerSettingById(int id);
    
    @Query("SELECT * FROM server_settings WHERE lastUsed = 1 LIMIT 1")
    LiveData<ServerSetting> getLastUsedServer();
    
    @Query("SELECT * FROM server_settings WHERE type = :type ORDER BY name ASC")
    LiveData<List<ServerSetting>> getServerSettingsByType(String type);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertServerSetting(ServerSetting serverSetting);
    
    @Update
    void updateServerSetting(ServerSetting serverSetting);
    
    @Delete
    void deleteServerSetting(ServerSetting serverSetting);
    
    @Query("DELETE FROM server_settings WHERE id = :id")
    void deleteServerSettingById(int id);
    
    @Query("UPDATE server_settings SET lastUsed = 0")
    void clearLastUsedFlags();
    
    @Query("UPDATE server_settings SET lastUsed = 1 WHERE id = :id")
    void setLastUsedServer(int id);
    
    @Query("SELECT COUNT(*) FROM server_settings")
    int getServerCount();
} 