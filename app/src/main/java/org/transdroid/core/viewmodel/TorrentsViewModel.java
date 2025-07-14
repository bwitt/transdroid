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
package org.transdroid.core.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.transdroid.core.data.AppDatabase;
import org.transdroid.core.data.dao.ServerSettingDao;
import org.transdroid.core.data.entity.ServerSetting;
import org.transdroid.core.util.HttpClient;
import org.transdroid.daemon.Daemon;
import org.transdroid.daemon.IDaemonAdapter;
import org.transdroid.daemon.Torrent;
import org.transdroid.daemon.task.DaemonTaskResult;
import org.transdroid.daemon.task.RetrieveTask;
import org.transdroid.daemon.task.RetrieveTaskSuccessResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Modern ViewModel for torrent operations.
 * Demonstrates how to replace AndroidAnnotations patterns with modern Android Architecture Components.
 */
public class TorrentsViewModel extends AndroidViewModel {
    
    private final ServerSettingDao serverSettingDao;
    private final HttpClient httpClient;
    private final ExecutorService executorService;
    
    private final MutableLiveData<List<Torrent>> torrents = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<ServerSetting> currentServer = new MutableLiveData<>();
    
    public TorrentsViewModel(@NonNull Application application) {
        super(application);
        this.serverSettingDao = AppDatabase.getInstance(application).serverSettingDao();
        this.httpClient = new HttpClient();
        this.executorService = Executors.newCachedThreadPool();
    }
    
    /**
     * Load torrents from the current server
     */
    public void loadTorrents(ServerSetting serverSetting) {
        if (serverSetting == null) {
            errorMessage.setValue("No server configured");
            return;
        }
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        currentServer.setValue(serverSetting);
        
        executorService.execute(() -> {
            try {
                // Create daemon adapter
                IDaemonAdapter adapter = serverSetting.getServerAdapter(null, getApplication());
                
                // Execute retrieve task
                DaemonTaskResult result = new RetrieveTask(adapter).execute();
                
                if (result instanceof RetrieveTaskSuccessResult) {
                    RetrieveTaskSuccessResult successResult = (RetrieveTaskSuccessResult) result;
                    torrents.postValue(successResult.getTorrents());
                } else {
                    errorMessage.postValue("Failed to retrieve torrents: " + result.toString());
                }
            } catch (Exception e) {
                errorMessage.postValue("Error loading torrents: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }
    
    /**
     * Refresh torrents from the current server
     */
    public void refreshTorrents() {
        ServerSetting server = currentServer.getValue();
        if (server != null) {
            loadTorrents(server);
        }
    }
    
    /**
     * Get all server settings
     */
    public LiveData<List<ServerSetting>> getAllServerSettings() {
        return serverSettingDao.getAllServerSettings();
    }
    
    /**
     * Get the last used server
     */
    public LiveData<ServerSetting> getLastUsedServer() {
        return serverSettingDao.getLastUsedServer();
    }
    
    /**
     * Set a server as last used
     */
    public void setLastUsedServer(ServerSetting serverSetting) {
        executorService.execute(() -> {
            serverSettingDao.clearLastUsedFlags();
            serverSettingDao.setLastUsedServer(serverSetting.getId());
        });
    }
    
    /**
     * Get torrents LiveData
     */
    public LiveData<List<Torrent>> getTorrents() {
        return torrents;
    }
    
    /**
     * Get loading state LiveData
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    /**
     * Get error message LiveData
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Get current server LiveData
     */
    public LiveData<ServerSetting> getCurrentServer() {
        return currentServer;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
    
    /**
     * Factory for creating TorrentsViewModel instances
     */
    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {
        
        public Factory(@NonNull Application application) {
            super(application);
        }
        
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(TorrentsViewModel.class)) {
                return (T) new TorrentsViewModel(getApplication());
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
} 