package com.example.xianjiao.phone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phonecalldemo.R;

import org.json.JSONObject;

/**
 * 登录Activity
 * 实现用户登录功能，与服务器通信
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "login_prefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final int REQUEST_CODE_REGISTER = 1000;

    private EditText etAccount;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnContacts;
    private Button btnMy;

    // 记录当前登录用户名，供回调使用
    private String currentLoginAccount = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        // 初始化控件
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnContacts = findViewById(R.id.btn_contacts);
        btnMy = findViewById(R.id.btn_my);

        // 设置点击事件
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnContacts.setOnClickListener(this);
        btnMy.setOnClickListener(this);
        
        // 如果已登录，自动填充账号
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedUsername = prefs.getString(KEY_USERNAME, "");
        if (!savedUsername.isEmpty()) {
            etAccount.setText(savedUsername);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                handleLogin();
                break;
            case R.id.btn_register:
                // 跳转到注册页面
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivityForResult(registerIntent, REQUEST_CODE_REGISTER);
                break;
            case R.id.btn_contacts:
                // 跳转到通讯录页面
                Intent contactsIntent = new Intent(this, PhoneCallDemo.class);
                startActivity(contactsIntent);
                finish();
                break;
            case R.id.btn_my:
                // 如果已登录，跳转到详情页；否则当前即为登录页
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
                if (isLoggedIn) {
                    Intent detailIntent = new Intent(this, AccountDetailActivity.class);
                    startActivity(detailIntent);
                    finish();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * 处理登录逻辑
     */
    private void handleLogin() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString();
        
        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 记录本次登录用户名
        currentLoginAccount = account;

        // 禁用登录按钮，防止重复提交
        btnLogin.setEnabled(false);
        btnLogin.setText("登录中...");
        
        // 发送登录请求到服务器
        new LoginTask().execute(account, password);
    }
    
    /**
     * 登录异步任务
     */
    private class LoginTask extends AsyncTask<String, Void, HttpUtils.ApiResponse> {
        
        @Override
        protected HttpUtils.ApiResponse doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            
            try {
                // 构建JSON请求数据
                JSONObject jsonData = new JSONObject();
                jsonData.put("username", username);
                jsonData.put("password", password);
                
                // 发送POST请求
                String response = HttpUtils.postRequest("/api/appPhone/login", jsonData.toString());
                
                // 解析响应
                return HttpUtils.parseResponse(response);
            } catch (Exception e) {
                Log.e(TAG, "登录请求失败: " + e.getMessage(), e);
                return new HttpUtils.ApiResponse(-1, "网络请求失败: " + e.getMessage(), null);
            }
        }
        
        @Override
        protected void onPostExecute(HttpUtils.ApiResponse response) {
            // 恢复按钮状态
            btnLogin.setEnabled(true);
            btnLogin.setText("登录");
            
            if (response == null) {
                Toast.makeText(LoginActivity.this, "网络请求失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (response.isSuccess()) {
                // 登录成功，保存用户信息
                try {
                    if (response.data != null) {
                        // 如果data是JSONObject
                        if (response.data instanceof JSONObject) {
                            JSONObject data = (JSONObject) response.data;
                            String userId = data.optString("userId", "");
                            String username = data.optString("username", "");
                            String token = data.optString("token", "");
                            
                            // 保存到SharedPreferences
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(KEY_USER_ID, userId);
                            editor.putString(KEY_USERNAME, username);
                            editor.putString(KEY_TOKEN, token);
                            editor.putBoolean("is_logged_in", true);
                            editor.apply();
                            
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            // 跳转到详情页
                            Intent detailIntent = new Intent(LoginActivity.this, AccountDetailActivity.class);
                            detailIntent.putExtra("username", username);
                            startActivity(detailIntent);
                            finish();
                        } else {
                            Log.w(TAG, "响应data不是JSONObject类型");
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            Intent detailIntent = new Intent(LoginActivity.this, AccountDetailActivity.class);
                            detailIntent.putExtra("username", currentLoginAccount);
                            startActivity(detailIntent);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent detailIntent = new Intent(LoginActivity.this, AccountDetailActivity.class);
                        detailIntent.putExtra("username", currentLoginAccount);
                        startActivity(detailIntent);
                        finish();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "解析登录响应数据失败: " + e.getMessage(), e);
                    Toast.makeText(LoginActivity.this, "登录成功，但保存信息失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 登录失败
                String errorMsg = response.message != null ? response.message : "登录失败";
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            // 注册成功，自动填充用户名
            if (data != null) {
                String username = data.getStringExtra("username");
                if (username != null) {
                    etAccount.setText(username);
                    etPassword.requestFocus();
                    Toast.makeText(this, "注册成功，请输入密码登录", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
