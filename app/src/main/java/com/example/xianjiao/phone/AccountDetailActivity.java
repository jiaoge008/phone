package com.example.xianjiao.phone;

import android.app.Activity;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phonecalldemo.R;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 登录成功后的账号详情页
 * 上部显示账号，下方提供操作按钮
 */
public class AccountDetailActivity extends Activity implements View.OnClickListener {

    private static final String PREFS_NAME = "login_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "userId";

    // 本地通讯录文件和图片目录（与 PhoneCallDemo 保持一致）
    private static final String PROJECT_NAME = "dadianhua";
    private static final String PHONE_CALL_PROJECT_PATH =
            Environment.getExternalStorageDirectory() + File.separator + PROJECT_NAME + File.separator;
    private static final String IMAGE_PATH = PHONE_CALL_PROJECT_PATH + "images" + File.separator;
    private static final String PHONE_BOOK_FILE_PATH = PHONE_CALL_PROJECT_PATH + "dianhuaben.txt";

    private TextView tvUsername;
    private Button btnSync;
    private Button btnBackup;
    private Button btnLogout;
    private Button btnContacts;
    private Button btnMy;

    private static final int REQ_STORAGE = 2001;
    private static final int ACTION_NONE = 0;
    private static final int ACTION_SYNC = 1;
    private static final int ACTION_BACKUP = 2;
    private int pendingAction = ACTION_NONE;

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
                if (ensureStoragePermission(ACTION_SYNC)) {
                    new SyncContactsTask().execute();
                }
                break;
            case R.id.btn_backup_contacts:
                if (ensureStoragePermission(ACTION_BACKUP)) {
                    new BackupContactsTask().execute();
                }
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

    private boolean ensureStoragePermission(int action) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            return true;
        }
        pendingAction = action;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQ_STORAGE);
        Toast.makeText(this, "请授予存储权限后再重试", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_STORAGE) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                pendingAction = ACTION_NONE;
                Toast.makeText(this, "未授予存储权限，同步/备份无法执行", Toast.LENGTH_LONG).show();
                return;
            }
            int action = pendingAction;
            pendingAction = ACTION_NONE;
            if (action == ACTION_SYNC) {
                new SyncContactsTask().execute();
            } else if (action == ACTION_BACKUP) {
                new BackupContactsTask().execute();
            }
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

    /**
     * 备份通讯录到服务器（使用新的备份接口）
     */
    private class BackupContactsTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "BackupContactsTask";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String userId = prefs.getString(KEY_USER_ID, "");
                if (userId == null || userId.isEmpty()) {
                    return "用户未登录或用户ID为空";
                }

                // 读取本地通讯录
                HashMap<Integer, String> map = FileOperate.ReadTxtFile(PHONE_BOOK_FILE_PATH);
                if (map == null || map.isEmpty()) {
                    return "本地通讯录为空，无需备份";
                }

                // 构建联系人列表
                JSONArray contactsArray = new JSONArray();
                Iterator<Integer> it = map.keySet().iterator();
                while (it.hasNext()) {
                    Integer key = it.next();
                    String phoneNum = map.get(key);
                    if (phoneNum == null) continue;
                    phoneNum = phoneNum.trim();
                    if (phoneNum.length() == 0 || phoneNum.length() != 11) continue;

                    // 构造本地图片路径
                    File imageFile = new File(IMAGE_PATH + phoneNum + ".png");
                    String avatarBase64 = "";
                    if (imageFile.exists()) {
                        try {
                            // 读取图片并转换为Base64
                            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            if (bitmap != null) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
                                byte[] imageBytes = baos.toByteArray();
                                avatarBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                                bitmap.recycle();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "读取图片失败: " + phoneNum, e);
                            // 图片读取失败，继续处理，avatarBase64为空
                        }
                    }

                    // 构建联系人项
                    JSONObject contactItem = new JSONObject();
                    contactItem.put("phoneNumber", phoneNum);
                    contactItem.put("avatarBase64", avatarBase64);
                    contactsArray.put(contactItem);
                }

                if (contactsArray.length() == 0) {
                    return "没有有效的联系人可备份";
                }

                // 构建备份请求
                JSONObject backupRequest = new JSONObject();
                backupRequest.put("userId", userId);
                backupRequest.put("contacts", contactsArray);

                // 调用备份接口
                String resp = HttpUtils.postRequest("/api/appPhone/backupContacts", backupRequest.toString());
                HttpUtils.ApiResponse result = HttpUtils.parseResponse(resp);
                if (result == null || !result.isSuccess()) {
                    return "备份失败: " + (result != null ? result.message : "网络请求失败");
                }

                // 解析响应
                if (result.data instanceof JSONObject) {
                    JSONObject data = (JSONObject) result.data;
                    int successCount = data.optInt("successCount", 0);
                    int failedCount = data.optInt("failedCount", 0);
                    String message = data.optString("message", "");
                    return String.format("备份完成：成功 %d 条，失败 %d 条%s", 
                            successCount, failedCount, message.isEmpty() ? "" : "\n" + message);
                }

                return result.message;
            } catch (Exception e) {
                Log.e(TAG, "备份失败", e);
                return "备份失败: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(AccountDetailActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 从服务器同步通讯录到本地（使用新的同步接口）
     */
    private class SyncContactsTask extends AsyncTask<Void, Void, String> {
        private static final String TAG = "SyncContactsTask";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String userId = prefs.getString(KEY_USER_ID, "");
                if (userId == null || userId.isEmpty()) {
                    return "用户未登录或用户ID为空";
                }

                // 调用同步接口
                JSONObject req = new JSONObject();
                req.put("userId", userId);

                String resp = HttpUtils.postRequest("/api/appPhone/syncContacts", req.toString());
                HttpUtils.ApiResponse result = HttpUtils.parseResponse(resp);
                if (result == null || !result.isSuccess()) {
                    return "同步失败: " + (result != null ? result.message : "网络请求失败");
                }

                // 解析响应
                JSONArray contactsArray = null;
                if (result.data instanceof JSONObject) {
                    JSONObject data = (JSONObject) result.data;
                    contactsArray = data.optJSONArray("contacts");
                }

                if (contactsArray == null || contactsArray.length() == 0) {
                    return "服务器上暂无通讯录数据";
                }

                // 确保本地目录存在
                File projectDir = new File(PHONE_CALL_PROJECT_PATH);
                if (!projectDir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    projectDir.mkdirs();
                }
                File imageDir = new File(IMAGE_PATH);
                if (!imageDir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    imageDir.mkdirs();
                }

                // 构建本地通讯录内容
                StringBuilder phoneBookBuilder = new StringBuilder();
                int successCount = 0;
                int failCount = 0;

                for (int i = 0; i < contactsArray.length(); i++) {
                    JSONObject item = contactsArray.getJSONObject(i);
                    String phoneNum = item.optString("phoneNumber", "").trim();
                    String avatar = item.optString("avatar", "");
                    if (phoneNum.isEmpty()) {
                        failCount++;
                        continue;
                    }

                    // 下载头像到本地
                    if (avatar != null && !avatar.isEmpty()) {
                        File destImage = new File(IMAGE_PATH + phoneNum + ".png");
                        if (HttpUtils.downloadFile(avatar, destImage)) {
                            successCount++;
                        } else {
                            failCount++;
                            Log.w(TAG, "下载头像失败: " + phoneNum);
                        }
                    } else {
                        successCount++;
                    }

                    // 添加到本地通讯录文本
                    phoneBookBuilder.append(phoneNum).append("\n");
                }

                // 覆盖写入本地电话本文件
                FileOperate.WriteFileCover(PHONE_BOOK_FILE_PATH, phoneBookBuilder.toString());

                return String.format("同步完成：共 %d 条，成功 %d 条，失败 %d 条\n请返回通讯录页面查看", 
                        contactsArray.length(), successCount, failCount);
            } catch (Exception e) {
                Log.e(TAG, "同步失败", e);
                return "同步失败: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(AccountDetailActivity.this, result, Toast.LENGTH_LONG).show();
            if (result != null && result.startsWith("同步完成")) {
                // 同步成功后回到通讯录页，触发重新加载本地文件和图片
                Intent intent = new Intent(AccountDetailActivity.this, PhoneCallDemo.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
