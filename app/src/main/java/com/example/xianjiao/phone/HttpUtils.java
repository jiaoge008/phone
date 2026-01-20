package com.example.xianjiao.phone;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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
     * 上传图片文件到服务器
     * @param apiPath  API路径，例如 "/api/upload/image"
     * @param imageFile 要上传的图片文件
     * @return 服务器返回的完整响应字符串
     */
    public static String uploadImage(String apiPath, File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            Log.e(TAG, "上传图片失败：文件不存在");
            return null;
        }

        String boundary = "----AndroidFormBoundary" + System.currentTimeMillis();
        String LINE_FEED = "\r\n";
        HttpURLConnection connection = null;
        try {
            String fullUrl = ServerConfig.getApiUrl(apiPath);
            Log.d(TAG, "上传图片URL: " + fullUrl);
            URL url = new URL(fullUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            DataOutputStream requestStream =
                    new DataOutputStream(new BufferedOutputStream(connection.getOutputStream()));

            // 写入文件字段
            String fileName = imageFile.getName();
            requestStream.writeBytes("--" + boundary + LINE_FEED);
            requestStream.writeBytes(
                    "Content-Disposition: form-data; name=\"image\"; filename=\"" + fileName + "\"" + LINE_FEED);
            requestStream.writeBytes("Content-Type: image/png" + LINE_FEED);
            requestStream.writeBytes(LINE_FEED);

            FileInputStream fileInputStream = new FileInputStream(imageFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                requestStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();
            requestStream.writeBytes(LINE_FEED);

            // 结束boundary
            requestStream.writeBytes("--" + boundary + "--" + LINE_FEED);
            requestStream.flush();
            requestStream.close();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "上传图片响应码: " + responseCode);

            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String result = response.toString();
            Log.d(TAG, "上传图片响应内容: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "上传图片失败: " + e.getMessage(), e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 下载图片到本地文件
     * @param urlPath 服务器返回的图片路径或完整URL
     * @param destFile 目标文件
     * @return 是否下载成功
     */
    public static boolean downloadFile(String urlPath, File destFile) {
        HttpURLConnection connection = null;
        try {
            String fullUrl;
            if (urlPath.startsWith("http://") || urlPath.startsWith("https://")) {
                fullUrl = urlPath;
            } else {
                fullUrl = ServerConfig.getBaseUrl() + urlPath;
            }
            // 处理路径中的井号，避免被当作 URL fragment
            if (fullUrl.contains("#") && !fullUrl.contains("%23")) {
                fullUrl = fullUrl.replace("#", "%23");
            }
            Log.d(TAG, "下载文件URL: " + fullUrl);

            URL url = new URL(fullUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "下载文件失败，响应码: " + responseCode);
                return false;
            }

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            File parent = destFile.getParentFile();
            if (parent != null && !parent.exists()) {
                //noinspection ResultOfMethodCallIgnored
                parent.mkdirs();
            }
            FileOutputStream outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            Log.d(TAG, "下载文件成功: " + destFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "下载文件失败: " + e.getMessage(), e);
            return false;
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
