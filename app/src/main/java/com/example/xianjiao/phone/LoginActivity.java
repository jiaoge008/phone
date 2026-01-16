package com.example.xianjiao.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phonecalldemo.R;

public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText etAccount;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnContacts;
    private Button btnMy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.login);

        // 初始化控件
        etAccount = (EditText) findViewById(R.id.et_account);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnContacts = (Button) findViewById(R.id.btn_contacts);
        btnMy = (Button) findViewById(R.id.btn_my);

        // 设置点击事件
        btnLogin.setOnClickListener(this);
        btnContacts.setOnClickListener(this);
        btnMy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                // 处理登录逻辑
                String account = etAccount.getText().toString();
                String password = etPassword.getText().toString();
                
                if (account.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
                } else {
                    // 这里可以添加实际的登录验证逻辑
                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                }
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
}