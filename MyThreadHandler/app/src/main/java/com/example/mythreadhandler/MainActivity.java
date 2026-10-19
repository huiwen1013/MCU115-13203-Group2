package com.example.mythreadhandler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private final TaskManager taskManager = new TaskManager();
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        MainPagerAdapter adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.tab_start);
                    break;
                case 1:
                    tab.setText(R.string.tab_progress);
                    break;
                case 2:
                    tab.setText(R.string.tab_result);
                    break;
            }
        }).attach();
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void switchToPage(int position) {
        if (viewPager != null && position >= 0 && position < 3) {
            viewPager.setCurrentItem(position, true);
        }
    }
}