package com.example.mythreadhandler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPagerAdapter extends FragmentStateAdapter {

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ProgressFragment();
            case 2:
                return new ResultFragment();
            case 0:
            default:
                return new StartFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}