package com.example.findbackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MatchingActivity extends AppCompatActivity {

    private Spinner spinnerSource;
    private TextView tvResultHeader;
    private TextView tvEmpty;
    private RecyclerView rvResults;

    private MatchingAdapter adapter;
    private DatabaseHelper dbHelper;
    private MatchingManager matchingManager;
    private SessionManager sessionManager;

    private List<Item> userItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        dbHelper = new DatabaseHelper(this);
        matchingManager = new MatchingManager(dbHelper);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        setupSourceSpinner();
        setupBottomNav();
    }

    private void initViews() {
        spinnerSource = findViewById(R.id.spinner_match_source);
        tvResultHeader = findViewById(R.id.tv_match_result_header);
        tvEmpty = findViewById(R.id.tv_match_empty);
        rvResults = findViewById(R.id.rv_matching_results);
    }

    private void setupSourceSpinner() {
        long currentUserId = sessionManager.getUserId();
        userItems = dbHelper.getItemsByUser(currentUserId);

        List<String> spinnerLabels = new ArrayList<>();
        spinnerLabels.add("全系統智慧交叉比對 (所有遺失物與拾獲物)");

        for (Item item : userItems) {
            String badge = Item.TYPE_LOST.equalsIgnoreCase(item.getItemType()) ? "[遺失]" : "[拾獲]";
            spinnerLabels.add(badge + " " + item.getItemName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerLabels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(spinnerAdapter);

        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    performAllMatching();
                } else {
                    Item selectedItem = userItems.get(position - 1);
                    performItemMatching(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MatchingAdapter(this, null);
        rvResults.setAdapter(adapter);

        adapter.setOnMatchActionListener(new MatchingAdapter.OnMatchActionListener() {
            @Override
            public void onDetailClick(MatchingResult result) {
                showItemDetailDialog(result.getMatchedItem());
            }

            @Override
            public void onClaimClick(MatchingResult result) {
                submitClaimRequest(result.getMatchedItem());
            }
        });
    }

    private void performAllMatching() {
        List<MatchingResult> matches = matchingManager.findAllMatches();
        updateUIWithResults(matches, "全系統智慧比對推薦結果");
    }

    private void performItemMatching(Item targetItem) {
        List<MatchingResult> matches = matchingManager.findMatches(targetItem);
        updateUIWithResults(matches, "針對「" + targetItem.getItemName() + "」比對推薦結果");
    }

    private void updateUIWithResults(List<MatchingResult> matches, String headerText) {
        tvResultHeader.setText(headerText);
        adapter.updateData(matches);

        if (matches.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvResults.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvResults.setVisibility(View.VISIBLE);
        }
    }

    private void showItemDetailDialog(Item item) {
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
        claim.setMessage("你好，經智慧媒合系統推薦，我想針對此物品提出認領申請。");
        claim.setStatus(Claim.STATUS_PENDING);

        long res = dbHelper.addClaim(claim);
        if (res > 0) {
            Toast.makeText(this, "已成功提出認領申請！可在認領頁面查看進度", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "認領申請建立失敗，請稍後再試", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNav() {
        findViewById(R.id.nav_btn_home).setOnClickListener(v -> {
            startActivity(new Intent(MatchingActivity.this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_search).setOnClickListener(v -> {
            startActivity(new Intent(MatchingActivity.this, SearchActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_matching).setOnClickListener(v -> {});
        findViewById(R.id.nav_btn_claim).setOnClickListener(v -> {
            startActivity(new Intent(MatchingActivity.this, ClaimActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_profile).setOnClickListener(v -> {
            startActivity(new Intent(MatchingActivity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }
}
