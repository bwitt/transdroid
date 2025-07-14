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
package org.transdroid.core.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.transdroid.core.data.dao.SearchHistoryDao;
import org.transdroid.core.data.dao.ServerSettingDao;
import org.transdroid.core.data.entity.SearchHistory;
import org.transdroid.core.data.entity.ServerSetting;
import org.transdroid.core.data.converter.DateConverter;

/**
 * Modern Room database for Transdroid.
 * Replaces the deprecated ORMLite database.
 */
@Database(
    entities = {
        SearchHistory.class,
        ServerSetting.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "transdroid.db";
    private static volatile AppDatabase INSTANCE;
    
    // DAOs
    public abstract SearchHistoryDao searchHistoryDao();
    public abstract ServerSettingDao serverSettingDao();
    
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Initialize database with default data if needed
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
} 