package com.example.findbackapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvNickname;
    private TextView tvUsername;
    private TextView tvEmail;
    private Button btnEditProfile;
    private Button btnLogout;

    private RecyclerView rvMyLost;
    private RecyclerView rvMyFound;

    private ItemAdapter lostAdapter;
    private ItemAdapter foundAdapter;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerViews();
        setupBottomNav();

        loadUserData();

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void initViews() {
        tvNickname = findViewById(R.id.tv_profile_nickname);
        tvUsername = findViewById(R.id.tv_profile_username);
        tvEmail = findViewById(R.id.tv_profile_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnLogout = findViewById(R.id.btn_logout);

        rvMyLost = findViewById(R.id.rv_my_lost_items);
        rvMyFound = findViewById(R.id.rv_my_found_items);
    }

    private void setupRecyclerViews() {
        rvMyLost.setLayoutManager(new LinearLayoutManager(this));
        rvMyFound.setLayoutManager(new LinearLayoutManager(this));

        lostAdapter = new ItemAdapter(this, null);
        foundAdapter = new ItemAdapter(this, null);

        rvMyLost.setAdapter(lostAdapter);
        rvMyFound.setAdapter(foundAdapter);

        ItemAdapter.OnItemClickListener listener = item -> showItemDetailDialog(item);
        lostAdapter.setOnItemClickListener(listener);
        foundAdapter.setOnItemClickListener(listener);
    }

    private void loadUserData() {
        long userId = sessionManager.getUserId();
        User user = dbHelper.getUserById(userId);

        if (user != null) {
            tvNickname.setText(user.getNickname());
            tvUsername.setText("帳號: " + user.getUsername());
            tvEmail.setText("Email: " + user.getEmail());
        } else {
            tvNickname.setText(sessionManager.getNickname());
            tvUsername.setText("帳號: " + sessionManager.getUsername());
            tvEmail.setText("Email: " + sessionManager.getEmail());
        }

        List<Item> myLostList = dbHelper.getItemsByUserAndType(userId, Item.TYPE_LOST);
        List<Item> myFoundList = dbHelper.getItemsByUserAndType(userId, Item.TYPE_FOUND);

        lostAdapter.updateData(myLostList);
        foundAdapter.updateData(myFoundList);
    }

    private void showEditProfileDialog() {
        long userId = sessionManager.getUserId();
        User user = dbHelper.getUserById(userId);
        if (user == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改個人資料");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText etNick = new EditText(this);
        etNick.setHint("暱稱");
        etNick.setText(user.getNickname());
        layout.addView(etNick);

        final EditText etMail = new EditText(this);
        etMail.setHint("電子郵件");
        etMail.setText(user.getEmail());
        layout.addView(etMail);

        builder.setView(layout);

        builder.setPositiveButton("儲存", (dialog, which) -> {
            String newNick = etNick.getText().toString().trim();
            String newMail = etMail.getText().toString().trim();

            if (TextUtils.isEmpty(newNick) || TextUtils.isEmpty(newMail)) {
                Toast.makeText(ProfileActivity.this, "暱稱與Email不得為空", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setNickname(newNick);
            user.setEmail(newMail);

            boolean success = dbHelper.updateUser(user);
            if (success) {
                sessionManager.updateUserInfo(newNick, newMail);
                Toast.makeText(ProfileActivity.this, "個人資料更新成功！", Toast.LENGTH_SHORT).show();
                loadUserData();
            } else {
                Toast.makeText(ProfileActivity.this, "更新失敗，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showItemDetailDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.getItemName());
        builder.setMessage("類型：" + (Item.TYPE_LOST.equalsIgnoreCase(item.getItemType()) ? "遺失物" : "拾獲物") + "\n"
                + "狀態：" + (Item.STATUS_RESOLVED.equalsIgnoreCase(item.getStatus()) ? "已找回" : "刊登中") + "\n"
                + "分類：" + item.getCategory() + "\n"
                + "顏色：" + item.getColor() + "\n"
                + "地點：" + item.getLocation() + "\n"
                + "日期：" + item.getItemDate() + "\n"
                + "聯絡方式：" + item.getContact() + "\n\n"
                + "特徵描述：\n" + item.getDescription());

        if (!Item.STATUS_RESOLVED.equalsIgnoreCase(item.getStatus())) {
            builder.setPositiveButton("標示為已找回", (dialog, which) -> {
                boolean success = dbHelper.updateItemStatus(item.getItemId(), Item.STATUS_RESOLVED);
                if (success) {
                    Toast.makeText(ProfileActivity.this, "已更新物品狀態為「已找回」", Toast.LENGTH_SHORT).show();
                    loadUserData();
                }
            });
        }

        builder.setNeutralButton("刪除此刊登", (dialog, which) -> {
            boolean success = dbHelper.deleteItem(item.getItemId());
            if (success) {
                Toast.makeText(ProfileActivity.this, "已刪除刊登紀錄", Toast.LENGTH_SHORT).show();
                loadUserData();
            }
        });

        builder.setNegativeButton("關閉", null);
        builder.show();
    }

    private void performLogout() {
        sessionManager.logoutUser();
        Toast.makeText(this, "已安全登出帳號", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void setupBottomNav() {
        findViewById(R.id.nav_btn_home).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_search).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_matching).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MatchingActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_claim).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ClaimActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.nav_btn_profile).setOnClickListener(v -> {});
    }
}
