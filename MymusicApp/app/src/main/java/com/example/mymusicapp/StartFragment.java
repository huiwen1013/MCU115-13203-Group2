package com.example.mymusicapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class StartFragment extends Fragment {

    private WorkViewModel viewModel;
    private TextView tvStartStatus;
    private Button btnStart;
    private Button btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvStartStatus = view.findViewById(R.id.tvStartStatus);
        btnStart = view.findViewById(R.id.btnStart);
        btnCancel = view.findViewById(R.id.btnCancel);

        viewModel = new ViewModelProvider(requireActivity()).get(WorkViewModel.class);

        btnStart.setOnClickListener(v -> viewModel.startWork());
        btnCancel.setOnClickListener(v -> viewModel.cancelWork());

        viewModel.getWorkState().observe(getViewLifecycleOwner(), state -> {
            if (state != null) {
                tvStartStatus.setText(state.getDisplayText());

                // Disable start button while working or preparing to avoid duplicate tasks
                boolean isRunning = (state == WorkState.PREPARING || state == WorkState.WORKING);
                btnStart.setEnabled(!isRunning);
                btnStart.setAlpha(isRunning ? 0.5f : 1.0f);
            }
        });
    }
}
