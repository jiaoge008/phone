package com.example.xianjiao.phone;

/**
 * 服务器配置类
 * 用于配置服务器地址
 */
public class ServerConfig {
    // 服务器基础URL，请根据实际情况修改
    // 本地测试: "http://10.0.2.2:8080" (Android模拟器访问本机)
    // 局域网测试: "http://192.168.x.x:8080" (替换为实际IP)
    // 生产环境: "https://your-domain.com"
//    private static final String BASE_URL = "http://192.168.136.113:8080";
    private static final String BASE_URL = "https://notebook.baidudayu.com";
    
    /**
     * 获取服务器基础URL
     * @return 服务器地址
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }
    
    /**
     * 获取完整的API路径
     * @param apiPath API路径，如 "/api/appPhone/register"
     * @return 完整URL
     */
    public static String getApiUrl(String apiPath) {
        return BASE_URL + apiPath;
    }
}
