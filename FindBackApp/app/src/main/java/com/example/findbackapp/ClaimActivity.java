package com.example.findbackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClaimActivity extends AppCompatActivity {

    private Button btnTabMyClaims;
    private Button btnTabReceivedClaims;
    private TextView tvEmpty;
    private RecyclerView rvClaims;

    private ClaimAdapter adapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private boolean isReceivedClaimsTab = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        setupBottomNav();

        btnTabMyClaims.setOnClickListener(v -> switchTab(false));
        btnTabReceivedClaims.setOnClickListener(v -> switchTab(true));

        switchTab(false); // Default to my claims tab
    }

    private void initViews() {
        btnTabMyClaims = findViewById(R.id.btn_tab_my_claims);
        btnTabReceivedClaims = findViewById(R.id.btn_tab_received_claims);
        tvEmpty = findViewById(R.id.tv_claim_empty);
        rvClaims = findViewById(R.id.rv_claims);
    }

    private void setupRecyclerView() {
        rvClaims.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClaimAdapter(this, null, false);
        rvClaims.setAdapter(adapter);

        adapter.setOnClaimActionListener(new ClaimAdapter.OnClaimActionListener() {
            @Override
            public void onAccept(Claim claim) {
                boolean success = dbHelper.updateClaimStatus(claim.getClaimId(), Claim.STATUS_ACCEPTED);
                if (success) {
                    Toast.makeText(ClaimActivity.this, "已接受此認領申請！請儘快聯絡對方完成交接", Toast.LENGTH_LONG).show();
                    loadClaimsData();
                }
            }

            @Override
            public void onReject(Claim claim) {
                boolean success = dbHelper.updateClaimStatus(claim.getClaimId(), Claim.STATUS_REJECTED);
                if (success) {
                    Toast.makeText(ClaimActivity.this, "已拒絕此認領申請", Toast.LENGTH_SHORT).show();
                    loadClaimsData();
                }
            }

            @Override
            public void onComplete(Claim claim) {
                boolean cSuccess = dbHelper.updateClaimStatus(claim.getClaimId(), Claim.STATUS_COMPLETED);
                boolean iSuccess = dbHelper.updateItemStatus(claim.getItemId(), Item.STATUS_RESOLVED);
                if (cSuccess && iSuccess) {
                    Toast.makeText(ClaimActivity.this, "恭喜！物品已順利找回，狀態更新為「已找回」", Toast.LENGTH_LONG).show();
                    loadClaimsData();
                }
            }
        });
    }

    private void switchTab(boolean isReceivedTab) {
        this.isReceivedClaimsTab = isReceivedTab;

        if (isReceivedTab) {
            btnTabReceivedClaims.setTextColor(ContextCompat.getColor(this, R.color.primary));
            btnTabMyClaims.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            btnTabMyClaims.setTextColor(ContextCompat.getColor(this, R.color.primary));
            btnTabReceivedClaims.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }

        loadClaimsData();
    }

    private void loadClaimsData() {
        long currentUserId = sessionManager.getUserId();
        List<Claim> claims;

        if (isReceivedClaimsTab) {
            claims = dbHelper.getClaimsByOwner(currentUserId);
        } else {
            claims = dbHelper.getClaimsByClaimant(currentUserId);
        }

        adapter = new ClaimAdapter(this, claims, isReceivedClaimsTab);
        setupAdapterListener();
        rvClaims.setAdapter(adapter);

        if (claims.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvClaims.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvClaims.setVisibility(View.VISIBLE);
        }
    }

    private void setupAdapterListener() {
        adapter.setOnClaimActionListener(new ClaimAdapter.OnClaimActionListener() {
            @Override
            public void onAccept(Claim claim) {
                boolean success = dbHelper.updateClaimStatus(claim.getClaimId(), Claim.STATUS_ACCEPTED);
                if (success) {
                    Toast.makeText(ClaimActivity.this, "已接受此認領申請！請儘快聯絡對方完成交接", Toast.LENGTH_LONG).show();
                    loadClaimsData();
                }
            }

            @Override
            public void onReject(Claim claim) {
                boolean success = dbHelper.updateClaimStatus(claim.getClaimId(), Claim.STATUS_REJECTED);
                if (success) {
                    Toast.makeText(ClaimActivity.this, "已拒絕此認領申請", Toast.LENGTH_SHORT).show();
                    loadClaimsData();
                }
            }

            @Override
            public void onComplete(Claim claim) {
                boolean cSuccess = dbHelper.updateClaimStatus(claim.getClaimId(), Claim.STATUS_COMPLETED);
                boolean iSuccess = dbHelper.updateItemStatus(claim.getItemId(), Item.STATUS_RESOLVED);
                if (cSuccess || iSuccess) {
                    Toast.makeText(ClaimActivity.this, "恭喜！物品已順利找回，狀態更新為「已找回」", Toast.LENGTH_LONG).show();
                    loadClaimsData();
                }
            }
        });
    }

    private void setupBottomNav() {
        findViewById(R.id.nav_btn_home).setOnClickListener(v -> {
            startActivity(new Intent(ClaimActivity.this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_search).setOnClickListener(v -> {
            startActivity(new Intent(ClaimActivity.this, SearchActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_matching).setOnClickListener(v -> {
            startActivity(new Intent(ClaimActivity.this, MatchingActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_claim).setOnClickListener(v -> {});
        findViewById(R.id.nav_btn_profile).setOnClickListener(v -> {
            startActivity(new Intent(ClaimActivity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }
}
