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

import org.transdroid.core.data.entity.SearchHistory;

import java.util.List;

/**
 * Room DAO for search history operations.
 */
@Dao
public interface SearchHistoryDao {
    
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    LiveData<List<SearchHistory>> getAllSearchHistory();
    
    @Query("SELECT * FROM search_history WHERE site = :site ORDER BY timestamp DESC")
    LiveData<List<SearchHistory>> getSearchHistoryBySite(String site);
    
    @Query("SELECT * FROM search_history WHERE query LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    LiveData<List<SearchHistory>> searchHistoryByQuery(String query);
    
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<SearchHistory>> getRecentSearchHistory(int limit);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSearchHistory(SearchHistory searchHistory);
    
    @Update
    void updateSearchHistory(SearchHistory searchHistory);
    
    @Delete
    void deleteSearchHistory(SearchHistory searchHistory);
    
    @Query("DELETE FROM search_history")
    void clearAllSearchHistory();
    
    @Query("DELETE FROM search_history WHERE site = :site")
    void clearSearchHistoryBySite(String site);
    
    @Query("DELETE FROM search_history WHERE timestamp < :timestamp")
    void deleteOldSearchHistory(long timestamp);
} 