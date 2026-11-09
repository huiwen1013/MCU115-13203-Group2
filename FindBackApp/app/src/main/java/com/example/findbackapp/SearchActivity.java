package com.example.findbackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etKeyword;
    private Spinner spinnerCategory;
    private Spinner spinnerColor;
    private EditText etLocation;
    private EditText etDate;
    private Button btnSearch;
    private Button btnClear;
    private TextView tvCount;
    private RecyclerView rvResults;

    private ItemAdapter adapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupSpinners();
        setupRecyclerView();
        setupBottomNav();

        // Check for incoming intent extra keyword
        String initialKeyword = getIntent().getStringExtra("keyword");
        if (initialKeyword != null && !initialKeyword.isEmpty()) {
            etKeyword.setText(initialKeyword);
            performSearch();
        } else {
            performSearch(); // Perform initial query showing all items
        }

        btnSearch.setOnClickListener(v -> performSearch());
        btnClear.setOnClickListener(v -> clearFilters());
    }

    private void initViews() {
        etKeyword = findViewById(R.id.et_search_keyword);
        spinnerCategory = findViewById(R.id.spinner_search_category);
        spinnerColor = findViewById(R.id.spinner_search_color);
        etLocation = findViewById(R.id.et_search_location);
        etDate = findViewById(R.id.et_search_date);
        btnSearch = findViewById(R.id.btn_search_submit);
        btnClear = findViewById(R.id.btn_clear_filters);
        tvCount = findViewById(R.id.tv_search_count);
        rvResults = findViewById(R.id.rv_search_results);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this,
                R.array.colors_array, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(colorAdapter);
    }

    private void setupRecyclerView() {
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(this, null);
        rvResults.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> showItemDetailDialog(item));
    }

    private void performSearch() {
        String keyword = etKeyword.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "";
        String color = spinnerColor.getSelectedItem() != null ? spinnerColor.getSelectedItem().toString() : "";
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        List<Item> results = dbHelper.searchItems(keyword, category, color, location, date);
        adapter.updateData(results);

        tvCount.setText(String.format(getString(R.string.search_results_count), results.size()));
    }

    private void clearFilters() {
        etKeyword.setText("");
        spinnerCategory.setSelection(0);
        spinnerColor.setSelection(0);
        etLocation.setText("");
        etDate.setText("");
        performSearch();
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
            builder.setPositiveButton("提出認領", (dialog, which) -> {
                submitClaimRequest(item);
            });
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
        findViewById(R.id.nav_btn_home).setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_search).setOnClickListener(v -> {});
        findViewById(R.id.nav_btn_matching).setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, MatchingActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_claim).setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, ClaimActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_profile).setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }
}
