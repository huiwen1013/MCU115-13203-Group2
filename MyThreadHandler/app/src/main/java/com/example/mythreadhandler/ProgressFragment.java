package com.example.mythreadhandler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProgressFragment extends Fragment implements TaskManager.TaskListener {

    private ProgressBar progressBar;
    private TextView tvPercentage;
    private TextView tvStatus;

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        tvPercentage = view.findViewById(R.id.tv_percentage);
        tvStatus = view.findViewById(R.id.tv_status);

        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.getTaskManager().addListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            activity.getTaskManager().removeListener(this);
        }
        progressBar = null;
        tvPercentage = null;
        tvStatus = null;
    }

    @Override
    public void onTaskStateUpdated(int progress, String status) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setProgress(progress);
            }
            if (tvPercentage != null) {
                tvPercentage.setText(String.format(java.util.Locale.getDefault(), "%d%%", progress));
            }
            if (tvStatus != null) {
                tvStatus.setText(status);
            }
        });
    }
}