package com.example.findbackapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

public class MatchingWorker extends Worker {

    private static final String TAG = "MatchingWorker";

    public MatchingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Executing periodic background matching check via WorkManager...");
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            MatchingManager matchingManager = new MatchingManager(dbHelper);

            List<MatchingResult> matches = matchingManager.findAllMatches();
            Log.d(TAG, "WorkManager matching completed. Matches count: " + matches.size());
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error running MatchingWorker", e);
            return Result.failure();
        }
    }
}
