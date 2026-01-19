package com.example.xianjiao.phone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phonecalldemo.R;

/**
 * 登录成功后的账号详情页
 * 上部显示账号，下方提供操作按钮
 */
public class AccountDetailActivity extends Activity implements View.OnClickListener {

    private static final String PREFS_NAME = "login_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "userId";

    private TextView tvUsername;
    private Button btnSync;
    private Button btnBackup;
    private Button btnLogout;
    private Button btnContacts;
    private Button btnMy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.detail_account);

        tvUsername = findViewById(R.id.tv_username);
        btnSync = findViewById(R.id.btn_sync_contacts);
        btnBackup = findViewById(R.id.btn_backup_contacts);
        btnLogout = findViewById(R.id.btn_logout);
        btnContacts = findViewById(R.id.btn_contacts);
        btnMy = findViewById(R.id.btn_my);

        btnSync.setOnClickListener(this);
        btnBackup.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnContacts.setOnClickListener(this);
        btnMy.setOnClickListener(this);

        // 获取用户名：优先Intent，其次SharedPreferences
        String username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            username = prefs.getString(KEY_USERNAME, "");
        }
        tvUsername.setText(username == null ? "" : username);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sync_contacts:
                Toast.makeText(this, "同步通讯录（待实现）", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_backup_contacts:
                Toast.makeText(this, "备份通讯录（待实现）", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_logout:
                handleLogout();
                break;
            case R.id.btn_contacts:
                // 跳转到通讯录页面
                Intent contactsIntent = new Intent(this, PhoneCallDemo.class);
                startActivity(contactsIntent);
                finish();
                break;
            case R.id.btn_my:
                // 已经在详情页（我的），无需跳转
                break;
            default:
                break;
        }
    }

    /**
     * 退出登录：清除本地登录信息并返回登录页
     */
    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.putBoolean("is_logged_in", false);
        editor.apply();

        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
