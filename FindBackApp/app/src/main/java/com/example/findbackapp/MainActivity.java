package com.example.findbackapp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView tvWelcome;
    private EditText etSearch;
    private ImageButton btnSearch;

    private RecyclerView rvLatestLost;
    private RecyclerView rvLatestFound;

    private ItemAdapter lostAdapter;
    private ItemAdapter foundAdapter;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private NetworkReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            sessionManager = new SessionManager(this);
            if (!sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            setContentView(R.layout.activity_main);

            dbHelper = new DatabaseHelper(this);

            initViews();
            setupBottomNav();
            setupActionCards();
            setupRecyclerViews();

            // Start background service & workmanager safely
            startMatchingServiceAndWorker();

            // Register Network Receiver safely
            registerNetworkReceiver();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (sessionManager != null && sessionManager.isLoggedIn()) {
                if (tvWelcome != null) {
                    tvWelcome.setText(getString(R.string.welcome_user, sessionManager.getNickname()));
                }
                loadLatestData();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            try {
                unregisterReceiver(networkReceiver);
            } catch (Exception ignored) {}
        }
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        etSearch = findViewById(R.id.et_home_search);
        btnSearch = findViewById(R.id.btn_home_search);
        rvLatestLost = findViewById(R.id.rv_latest_lost);
        rvLatestFound = findViewById(R.id.rv_latest_found);

        tvWelcome.setText(getString(R.string.welcome_user, sessionManager.getNickname()));

        btnSearch.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String keyword = etSearch.getText().toString().trim();
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        if (!keyword.isEmpty()) {
            intent.putExtra("keyword", keyword);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setupActionCards() {
        View cardLost = findViewById(R.id.card_post_lost);
        if (cardLost != null) {
            cardLost.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, LostItemActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        View cardFound = findViewById(R.id.card_post_found);
        if (cardFound != null) {
            cardFound.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, FoundItemActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        View cardMatch = findViewById(R.id.card_smart_match);
        if (cardMatch != null) {
            cardMatch.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, MatchingActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    private void setupRecyclerViews() {
        if (rvLatestLost != null) {
            rvLatestLost.setLayoutManager(new LinearLayoutManager(this));
            lostAdapter = new ItemAdapter(this, null);
            rvLatestLost.setAdapter(lostAdapter);
            lostAdapter.setOnItemClickListener(this::showItemDetailDialog);
        }

        if (rvLatestFound != null) {
            rvLatestFound.setLayoutManager(new LinearLayoutManager(this));
            foundAdapter = new ItemAdapter(this, null);
            rvLatestFound.setAdapter(foundAdapter);
            foundAdapter.setOnItemClickListener(this::showItemDetailDialog);
        }
    }

    private void loadLatestData() {
        if (dbHelper == null) return;
        List<Item> lostList = dbHelper.getItemsByType(Item.TYPE_LOST);
        List<Item> foundList = dbHelper.getItemsByType(Item.TYPE_FOUND);

        if (lostAdapter != null) lostAdapter.updateData(lostList);
        if (foundAdapter != null) foundAdapter.updateData(foundList);
    }

    private void showItemDetailDialog(Item item) {
        if (item == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.getItemName());
        builder.setMessage("類型：" + (Item.TYPE_LOST.equalsIgnoreCase(item.getItemType()) ? "遺失物" : "拾獲物") + "\n"
                + "分類：" + item.getCategory() + "\n"
                + "顏色：" + item.getColor() + "\n"
                + "地點：" + item.getLocation() + "\n"
                + "日期：" + item.getItemDate() + "\n"
                + "聯絡方式：" + item.getContact() + "\n\n"
                + "特徵描述：\n" + item.getDescription());

        long currentUserId = sessionManager.getUserId();
        if (item.getUserId() != currentUserId) {
            builder.setPositiveButton("提出認領", (dialog, which) -> submitClaimRequest(item));
        }
        builder.setNegativeButton("關閉", null);
        builder.show();
    }

    private void submitClaimRequest(Item item) {
        long currentUserId = sessionManager.getUserId();
        if (dbHelper.hasClaimed(item.getItemId(), currentUserId)) {
            Toast.makeText(this, "您已針對此物品提出過認領申請，請勿重複建立！", Toast.LENGTH_LONG).show();
            return;
        }

        Claim claim = new Claim();
        claim.setItemId(item.getItemId());
        claim.setClaimantId(currentUserId);
        claim.setOwnerId(item.getUserId());
        claim.setMessage("你好，我想確認這項物品的詳細資訊並進行認領。");
        claim.setStatus(Claim.STATUS_PENDING);

        long res = dbHelper.addClaim(claim);
        if (res > 0) {
            Toast.makeText(this, "已成功提出認領申請！可在認領頁面查看進度", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "認領申請建立失敗，請稍後再試", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNav() {
        View btnHome = findViewById(R.id.nav_btn_home);
        if (btnHome != null) btnHome.setOnClickListener(v -> {});

        View btnSearch = findViewById(R.id.nav_btn_search);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }

        View btnMatch = findViewById(R.id.nav_btn_matching);
        if (btnMatch != null) {
            btnMatch.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, MatchingActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }

        View btnClaim = findViewById(R.id.nav_btn_claim);
        if (btnClaim != null) {
            btnClaim.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, ClaimActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }

        View btnProfile = findViewById(R.id.nav_btn_profile);
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    private void startMatchingServiceAndWorker() {
        try {
            Intent serviceIntent = new Intent(this, MatchingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting MatchingService", e);
        }

        try {
            PeriodicWorkRequest matchWorkRequest = new PeriodicWorkRequest.Builder(
                    MatchingWorker.class, 15, TimeUnit.MINUTES).build();
            WorkManager.getInstance(this).enqueue(matchWorkRequest);
        } catch (Exception e) {
            Log.e(TAG, "Error enqueueing MatchingWorker", e);
        }
    }

    private void registerNetworkReceiver() {
        try {
            networkReceiver = new NetworkReceiver();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.registerReceiver(this, networkReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(networkReceiver, filter);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error registering network receiver", e);
        }
    }
}
