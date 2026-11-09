package com.example.mymusicapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ProgressFragment extends Fragment {

    private WorkViewModel viewModel;
    private ProgressBar circularProgressBar;
    private TextView tvPercentage;
    private TextView tvProgressStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        tvPercentage = view.findViewById(R.id.tvPercentage);
        tvProgressStatus = view.findViewById(R.id.tvProgressStatus);

        viewModel = new ViewModelProvider(requireActivity()).get(WorkViewModel.class);

        viewModel.getWorkState().observe(getViewLifecycleOwner(), state -> {
            if (state != null) {
                switch (state) {
                    case IDLE:
                        circularProgressBar.setProgress(0);
                        tvPercentage.setText("0%");
                        tvProgressStatus.setText("Idle");
                        break;
                    case PREPARING:
                        circularProgressBar.setProgress(0);
                        tvPercentage.setText("Preparing...");
                        tvProgressStatus.setText("Preparing...");
                        break;
                    case WORKING:
                        tvProgressStatus.setText("Working...");
                        break;
                    case COMPLETED:
                        circularProgressBar.setProgress(100);
                        tvPercentage.setText("100%");
                        tvProgressStatus.setText("100%");
                        break;
                    case CANCELED:
                        tvPercentage.setText("Canceled");
                        tvProgressStatus.setText("Canceled");
                        break;
                }
            }
        });

        viewModel.getProgress().observe(getViewLifecycleOwner(), progress -> {
            WorkState currentState = viewModel.getWorkState().getValue();
            if (currentState == WorkState.WORKING && progress != null) {
                circularProgressBar.setProgress(progress);
                tvPercentage.setText(progress + "%");
            }
        });
    }
}
