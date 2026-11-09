package com.example.mymusicapp;

import android.app.Application;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WorkViewModel extends AndroidViewModel {

    private static final String TAG = "WorkViewModel";

    private final MutableLiveData<WorkState> workState = new MutableLiveData<>(WorkState.IDLE);
    private final MutableLiveData<Integer> progress = new MutableLiveData<>(0);

    private Thread backgroundThread;
    private volatile boolean isCanceled = false;

    private MediaPlayer mediaPlayer;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public WorkViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<WorkState> getWorkState() {
        return workState;
    }

    public LiveData<Integer> getProgress() {
        return progress;
    }

    public synchronized void startWork() {
        WorkState currentState = workState.getValue();
        if (currentState == WorkState.PREPARING || currentState == WorkState.WORKING) {
            // Prevent starting duplicate background tasks
            Log.d(TAG, "Work is already running or preparing.");
            return;
        }

        isCanceled = false;
        workState.setValue(WorkState.PREPARING);
        progress.setValue(0);

        if (backgroundThread != null && backgroundThread.isAlive()) {
            backgroundThread.interrupt();
        }

        backgroundThread = new Thread(() -> {
            try {
                // 1. Preparing phase (sleep 1.5s)
                Thread.sleep(1500);

                if (isCanceled || Thread.currentThread().isInterrupted()) {
                    return;
                }

                // 2. Start working phase
                workState.postValue(WorkState.WORKING);

                // Start playing background music on main UI thread
                mainHandler.post(this::startMusic);

                // 3. Progress loop from 0% to 100%
                for (int i = 1; i <= 100; i++) {
                    if (isCanceled || Thread.currentThread().isInterrupted()) {
                        return;
                    }

                    progress.postValue(i);

                    // 50ms per step = 5 seconds total duration
                    Thread.sleep(50);
                }

                if (isCanceled || Thread.currentThread().isInterrupted()) {
                    return;
                }

                // 4. Completed phase
                workState.postValue(WorkState.COMPLETED);
                progress.postValue(100);

                // Stop background music on main thread
                mainHandler.post(this::stopMusic);

            } catch (InterruptedException e) {
                if (isCanceled) {
                    workState.postValue(WorkState.CANCELED);
                    mainHandler.post(this::stopMusic);
                }
            }
        });

        backgroundThread.start();
    }

    public synchronized void cancelWork() {
        WorkState currentState = workState.getValue();
        if (currentState == WorkState.PREPARING || currentState == WorkState.WORKING) {
            isCanceled = true;
            if (backgroundThread != null) {
                backgroundThread.interrupt();
            }
            workState.setValue(WorkState.CANCELED);
            stopMusic();
        } else if (currentState != WorkState.CANCELED) {
            // Safely handle cancel when not actively working
            workState.setValue(WorkState.CANCELED);
            stopMusic();
        }
    }

    private synchronized void startMusic() {
        if (isCanceled || workState.getValue() != WorkState.WORKING) {
            return;
        }
        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer.create(getApplication(), R.raw.background_music);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                    Log.d(TAG, "Background music started.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error starting music", e);
            }
        }
    }

    private synchronized void stopMusic() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error stopping music", e);
            } finally {
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "Background music stopped and released.");
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        isCanceled = true;
        if (backgroundThread != null) {
            backgroundThread.interrupt();
        }
        stopMusic();
    }
}
