package com.example.findbackapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class LostItemActivity extends AppCompatActivity {

    private ImageView imgPhoto;
    private Button btnSelectPhoto;
    private EditText etName;
    private Spinner spinnerCategory;
    private Spinner spinnerColor;
    private EditText etDescription;
    private EditText etLocation;
    private EditText etDate;
    private EditText etContact;
    private Button btnSubmit;

    private Uri selectedImageUri;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}
                    imgPhoto.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_item);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupSpinners();
        setupDatePicker();

        btnSelectPhoto.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        btnSubmit.setOnClickListener(v -> submitLostItem());
    }

    private void initViews() {
        imgPhoto = findViewById(R.id.img_lost_photo);
        btnSelectPhoto = findViewById(R.id.btn_select_lost_photo);
        etName = findViewById(R.id.et_lost_name);
        spinnerCategory = findViewById(R.id.spinner_lost_category);
        spinnerColor = findViewById(R.id.spinner_lost_color);
        etDescription = findViewById(R.id.et_lost_description);
        etLocation = findViewById(R.id.et_lost_location);
        etDate = findViewById(R.id.et_lost_date);
        etContact = findViewById(R.id.et_lost_contact);
        btnSubmit = findViewById(R.id.btn_submit_lost);

        // Pre-fill contact with email if logged in
        if (sessionManager.isLoggedIn() && !sessionManager.getEmail().isEmpty()) {
            etContact.setText(sessionManager.getEmail());
        }
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

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
                String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                etDate.setText(dateStr);
            }, year, month, day);
            dialog.show();
        });
    }

    private void submitLostItem() {
        String name = etName.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String color = spinnerColor.getSelectedItem().toString();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("請輸入物品名稱");
            etName.requestFocus();
            return;
        }

        if (category.contains("請選擇")) {
            Toast.makeText(this, "請選擇物品分類", Toast.LENGTH_SHORT).show();
            return;
        }

        if (color.contains("請選擇")) {
            Toast.makeText(this, "請選擇物品顏色", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            etLocation.setError("請輸入遺失地點");
            etLocation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(date)) {
            etDate.setError("請選擇遺失日期");
            etDate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contact)) {
            etContact.setError("請輸入聯絡方式");
            etContact.requestFocus();
            return;
        }

        Item item = new Item();
        item.setUserId(sessionManager.getUserId());
        item.setItemType(Item.TYPE_LOST);
        item.setItemName(name);
        item.setCategory(category);
        item.setColor(color);
        item.setDescription(description);
        item.setLocation(location);
        item.setItemDate(date);
        item.setContact(contact);
        item.setImageUri(selectedImageUri != null ? selectedImageUri.toString() : "");
        item.setStatus(Item.STATUS_ACTIVE);

        long id = dbHelper.addItem(item);
        if (id > 0) {
            Toast.makeText(this, "遺失物刊登成功！系統正自動為您進行智慧媒合…", Toast.LENGTH_LONG).show();

            // Trigger background service
            try {
                Intent serviceIntent = new Intent(this, MatchingService.class);
                startService(serviceIntent);
            } catch (Exception ignored) {}

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            Toast.makeText(this, "刊登失敗，請稍後再試", Toast.LENGTH_SHORT).show();
        }
    }
}
