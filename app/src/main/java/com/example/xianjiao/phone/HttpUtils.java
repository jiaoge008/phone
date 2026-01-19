package com.example.xianjiao.phone;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HTTP请求工具类
 * 用于与服务器进行通信
 */
public class HttpUtils {
    
    private static final String TAG = "HttpUtils";
    
    /**
     * 发送POST请求
     * @param apiPath API路径，如 "/api/appPhone/register"
     * @param jsonData JSON数据字符串
     * @return 响应结果
     */
    public static String postRequest(String apiPath, String jsonData) {
        HttpURLConnection connection = null;
        try {
            String fullUrl = ServerConfig.getApiUrl(apiPath);
            Log.d(TAG, "请求URL: " + fullUrl);
            Log.d(TAG, "请求数据: " + jsonData);
            URL url = new URL(fullUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(10000); // 10秒连接超时
            connection.setReadTimeout(10000); // 10秒读取超时
            
            // 发送数据
            if (jsonData != null && !jsonData.isEmpty()) {
                OutputStream os = connection.getOutputStream();
                os.write(jsonData.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
            }
            
            // 获取响应码
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "响应码: " + responseCode);
            
            // 读取响应
            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(
                        connection.getErrorStream(), StandardCharsets.UTF_8));
            }
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            String result = response.toString();
            Log.d(TAG, "响应内容: " + result);
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "请求失败: " + e.getMessage(), e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * 解析服务器响应
     * @param response 响应字符串
     * @return ApiResponse对象
     */
    public static ApiResponse parseResponse(String response) {
        if (response == null || response.isEmpty()) {
            return new ApiResponse(-1, "网络请求失败", null);
        }
        
        try {
            JSONObject json = new JSONObject(response);
            int code = json.optInt("code", -1);
            String message = json.optString("message", "");
            Object data = json.opt("data");
            
            return new ApiResponse(code, message, data);
        } catch (Exception e) {
            Log.e(TAG, "解析响应失败: " + e.getMessage(), e);
            return new ApiResponse(-1, "解析响应失败: " + e.getMessage(), null);
        }
    }
    
    /**
     * API响应封装类
     */
    public static class ApiResponse {
        public int code;
        public String message;
        public Object data;
        
        public ApiResponse(int code, String message, Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
        
        public boolean isSuccess() {
            return code == 0;
        }
    }
}
