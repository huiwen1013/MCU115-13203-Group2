package com.example.mythreadhandler;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CopyOnWriteArrayList;

public class TaskManager {

    public static final String STATUS_PREPARING = "準備中";
    public static final String STATUS_WORKING = "工作中";
    public static final String STATUS_FINISHED = "背景工作結束";
    public static final String STATUS_CANCELLED = "背景工作中斷";

    public interface TaskListener {
        void onTaskStateUpdated(int progress, String status);
    }

    private int currentProgress = 0;
    private String currentStatus = STATUS_PREPARING;
    private boolean isRunning = false;
    private volatile boolean isCancelled = false;

    private Thread backgroundThread;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final CopyOnWriteArrayList<TaskListener> listeners = new CopyOnWriteArrayList<>();

    public int getCurrentProgress() {
        return currentProgress;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void addListener(TaskListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            // Notify listener immediately with current state upon registration
            listener.onTaskStateUpdated(currentProgress, currentStatus);
        }
    }

    public void removeListener(TaskListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners() {
        for (TaskListener listener : listeners) {
            listener.onTaskStateUpdated(currentProgress, currentStatus);
        }
    }

    public synchronized void startTask() {
        // Prevent duplicate execution while work is already running
        if (isRunning) {
            return;
        }

        isRunning = true;
        isCancelled = false;
        currentProgress = 0;
        currentStatus = STATUS_PREPARING;
        notifyListeners();

        backgroundThread = new Thread(() -> {
            // Transition from preparing to working state
            mainHandler.post(() -> {
                if (!isCancelled) {
                    currentStatus = STATUS_WORKING;
                    notifyListeners();
                }
            });

            try {
                for (int i = 1; i <= 100; i++) {
                    if (isCancelled || Thread.currentThread().isInterrupted()) {
                        break;
                    }

                    // Simulate task step duration (~150ms per step, total ~15s)
                    Thread.sleep(150);

                    if (isCancelled || Thread.currentThread().isInterrupted()) {
                        break;
                    }

                    final int progress = i;
                    mainHandler.post(() -> {
                        if (!isCancelled && isRunning) {
                            currentProgress = progress;
                            currentStatus = STATUS_WORKING;
                            notifyListeners();
                        }
                    });
                }

                // Normal completion
                if (!isCancelled && !Thread.currentThread().isInterrupted()) {
                    mainHandler.post(() -> {
                        if (!isCancelled && isRunning) {
                            isRunning = false;
                            currentProgress = 100;
                            currentStatus = STATUS_FINISHED;
                            notifyListeners();
                        }
                    });
                }
            } catch (InterruptedException e) {
                // Thread interrupted during sleep; do not overwrite state to FINISHED
            }
        });

        backgroundThread.start();
    }

    public synchronized void cancelTask() {
        // If task was already completed normally, do not change status to CANCELLED
        if (STATUS_FINISHED.equals(currentStatus)) {
            return;
        }

        // If not running and already cancelled or preparing, ignore duplicate cancel
        if (!isRunning && STATUS_CANCELLED.equals(currentStatus)) {
            return;
        }

        isCancelled = true;
        isRunning = false;
        currentStatus = STATUS_CANCELLED;

        // Clear pending handler callbacks to prevent late progress updates
        mainHandler.removeCallbacksAndMessages(null);

        if (backgroundThread != null && backgroundThread.isAlive()) {
            backgroundThread.interrupt();
        }

        // Immediately update UI to CANCELLED while retaining the progress at moment of cancellation
        notifyListeners();
    }
}