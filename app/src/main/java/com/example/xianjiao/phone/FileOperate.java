package com.example.xianjiao.phone;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.io.FileWriter;

import android.util.Log;

public class FileOperate {

    public static HashMap<Integer,String> ReadTxtFile(String strFilePath)
    {
        String path = strFilePath;
        Integer count = 1000;
        String content = ""; //文件内容字符串
        HashMap<Integer,String> map = new HashMap<Integer,String>();
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (!file.exists())
        {
            try{
                file.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        content += line + "\n";
                        map.put(count, line);
                        count += 1;
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {

            }
            catch (IOException e)
            {

            }
        }
        return map;
        //return content;
    }
    public static void WriteFileCover(String strFilePath,String content)
    {

        //FileOutputStream fout =openFileOutput(strFilePath, Context.MODE_PRIVATE);
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件,false表示覆盖
            FileWriter writer = new FileWriter(strFilePath, false);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void WriteFileAppend(String strFilePath,String content)
    {

        //FileOutputStream fout =openFileOutput(strFilePath, Context.MODE_PRIVATE);
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件,false表示覆盖
            FileWriter writer = new FileWriter(strFilePath, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //将hashmap中的数据组合成字符串返回
    public static String composeMapStr(HashMap<Integer,String> map)
    {
        String content = "";
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext())
        {
            Object key = iter.next();
            Object val = map.get(key);
            content += (String)val + "\n";
        }
        return content;
    }
}
