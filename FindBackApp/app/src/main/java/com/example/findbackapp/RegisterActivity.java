package com.example.findbackapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText etNickname;
    private EditText etEmail;
    private Button btnRegister;
    private Button btnBackLogin;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        etUsername = findViewById(R.id.et_reg_username);
        etPassword = findViewById(R.id.et_reg_password);
        etNickname = findViewById(R.id.et_reg_nickname);
        etEmail = findViewById(R.id.et_reg_email);
        btnRegister = findViewById(R.id.btn_reg_submit);
        btnBackLogin = findViewById(R.id.btn_reg_back_login);

        View root = findViewById(android.R.id.content);
        if (root != null) {
            root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        }

        btnRegister.setOnClickListener(v -> performRegistration());
        btnBackLogin.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("請輸入帳號");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("請輸入密碼");
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nickname)) {
            etNickname.setError("請輸入暱稱");
            etNickname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("請輸入電子郵件");
            etEmail.requestFocus();
            return;
        }

        if (dbHelper.checkUsernameExists(username)) {
            Toast.makeText(this, "該帳號已有人使用，請更換帳號名稱", Toast.LENGTH_LONG).show();
            return;
        }

        User newUser = new User(username, password, nickname, email);
        long userId = dbHelper.addUser(newUser);

        if (userId > 0) {
            sessionManager.createLoginSession(userId, username, nickname, email);
            Toast.makeText(this, "註冊成功！自動為您登入", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegisterActivity.class.cast(this), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {
            Toast.makeText(this, "註冊失敗，請稍後再試", Toast.LENGTH_SHORT).show();
        }
    }
}
