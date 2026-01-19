package com.example.xianjiao.phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
 * 注册Activity
 * 实现用户注册功能，与服务器通信
 */
public class RegisterActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private EditText etUsername;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private Button btnContacts;
    private Button btnMy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);

        // 初始化控件
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnContacts = findViewById(R.id.btn_contacts);
        btnMy = findViewById(R.id.btn_my);

        // 设置点击事件
        btnRegister.setOnClickListener(this);
        btnContacts.setOnClickListener(this);
        btnMy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                handleRegister();
                break;
            case R.id.btn_contacts:
                // 跳转到通讯录页面
                Intent contactsIntent = new Intent(this, PhoneCallDemo.class);
                startActivity(contactsIntent);
                finish();
                break;
            case R.id.btn_my:
                // 已经在"我的"页面，无需跳转
                break;
            default:
                break;
        }
    }

    /**
     * 处理注册逻辑
     */
    private void handleRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        
        // 验证输入
        if (username.isEmpty()) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return;
        }
        
        if (username.length() < 3) {
            Toast.makeText(this, "用户名至少需要3个字符", Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            Toast.makeText(this, "密码至少需要6位", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return;
        }
        
        // 禁用注册按钮，防止重复提交
        btnRegister.setEnabled(false);
        btnRegister.setText("注册中...");
        
        // 发送注册请求到服务器
        new RegisterTask().execute(username, password);
    }
    
    /**
     * 注册异步任务
     */
    private class RegisterTask extends AsyncTask<String, Void, HttpUtils.ApiResponse> {
        
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
                String response = HttpUtils.postRequest("/api/appPhone/register", jsonData.toString());
                
                // 解析响应
                return HttpUtils.parseResponse(response);
            } catch (Exception e) {
                Log.e(TAG, "注册请求失败: " + e.getMessage(), e);
                return new HttpUtils.ApiResponse(-1, "网络请求失败: " + e.getMessage(), null);
            }
        }
        
        @Override
        protected void onPostExecute(HttpUtils.ApiResponse response) {
            // 恢复按钮状态
            btnRegister.setEnabled(true);
            btnRegister.setText("注册");
            
            if (response == null) {
                Toast.makeText(RegisterActivity.this, "网络请求失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (response.isSuccess()) {
                // 注册成功
                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                
                // 返回登录界面，并传递用户名
                Intent resultIntent = new Intent();
                resultIntent.putExtra("username", etUsername.getText().toString().trim());
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                // 注册失败
                String errorMsg = response.message != null ? response.message : "注册失败";
                new AlertDialog.Builder(RegisterActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("注册失败")
                        .setMessage(errorMsg)
                        .setPositiveButton("确定", null)
                        .show();
            }
        }
    }
}
