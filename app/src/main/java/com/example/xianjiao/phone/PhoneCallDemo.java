package com.example.xianjiao.phone;

import java.io.File;
import android.util.Log;

import com.example.phonecalldemo.R;
import com.example.xianjiao.phone.FileOperate;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View.*;
import android.view.*;

public class PhoneCallDemo extends Activity implements OnClickListener,OnLongClickListener{

    private Button bt;
    private EditText et;
    private DisplayMetrics dm;
    private LinearLayout.LayoutParams imagebtn_params;
    private ImageButton jiangjiang;
    private ImageButton dujun;
    private ImageButton bingbing;
    private ImageButton dagu;

    private LinearLayout linerLayout;
    private ImageButton topAdd;
    private Integer deleteImageButtonId;
    private String deletePhoneNum;

    private HashMap<Integer,String> map;

    private int increaseNum = 1;

    private final static String projectName = "dadianhua";
    private final static String phoneCallProjectPath = Environment.getExternalStorageDirectory()+File.separator+projectName+File.separator;
    private final static String imagePath = phoneCallProjectPath+"images"+File.separator;
    private final static String imageFilePath = imagePath+"myPic.png";
    private final static String phoneBookFilePath = phoneCallProjectPath+"dianhuaben.txt";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.main1);


        //如果目录不存在则新建项目数据存储目录
        makeProjectPath();

        //读取文件
        map = FileOperate.ReadTxtFile(phoneBookFilePath);
        FileOperate.composeMapStr(map);

        linerLayout = (LinearLayout)findViewById(R.id.ll);


        ImageButton last = new ImageButton(this);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //取得资源
        bt = (Button)findViewById(R.id.bt1);
        et = (EditText)findViewById(R.id.et1);
        topAdd = (ImageButton)findViewById(R.id.top_add);
        bt.setVisibility(View.GONE);
        et.setVisibility(View.GONE);
        jiangjiang = (ImageButton)findViewById(R.id.jiangjiang);
        jiangjiang.setVisibility(View.GONE);
        dujun = (ImageButton)findViewById(R.id.dujun);
        dujun.setVisibility(View.GONE);
        bingbing = (ImageButton)findViewById(R.id.bingbing);
        bingbing.setVisibility(View.GONE);
        dagu = (ImageButton)findViewById(R.id.dagu);
        dagu.setVisibility(View.GONE);
        imagebtn_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imagebtn_params.height = dm.heightPixels/2;
        imagebtn_params.width = dm.widthPixels;
        dujun.setLayoutParams(imagebtn_params);
        bingbing.setLayoutParams(imagebtn_params);
        dagu.setLayoutParams(imagebtn_params);
        //通过遍历button动态创建ImageButton
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext())
        {
            Object key = iter.next();
            Object val = map.get(key);
            Integer integerKey = (Integer)key;
            if(integerKey > increaseNum)
            {
                increaseNum = integerKey;
            }
            ImageButton addImageButton = new ImageButton(this);
            addImageButton.setId(integerKey.intValue());
            addImageButton.setScaleType(ImageView.ScaleType.FIT_XY);
            addImageButton.setLayoutParams(imagebtn_params);
            addImageButton.setOnClickListener(this);
            //设置长按时间函数
            addImageButton.setOnLongClickListener(this);
            String imageFilePath = imagePath+(String)val+".png";
            Log.e("startActivity:",(String)val);
            File tmpImageFile = new File(imageFilePath);
            Bitmap photo;
            try{
                FileInputStream in = new FileInputStream(tmpImageFile);
                try{
                    photo = BitmapFactory.decodeStream(in);
                    Drawable drawable = new BitmapDrawable(photo);
                    addImageButton.setImageDrawable(drawable);
                    linerLayout.addView(addImageButton);
                    in.close();
                }catch (IOException e){

                }

            }catch (FileNotFoundException e){

            }

        }

        last.setId(1000);
        last.setImageDrawable(getResources().getDrawable(R.drawable.changjiang));
        last.setScaleType(ImageView.ScaleType.FIT_XY);
        last.setLayoutParams(imagebtn_params);
        last.setVisibility(View.GONE);
        last.setOnClickListener(this);
        //topAdd.setOnClickListener(this);
        linerLayout.addView(last);
        topAdd.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){

                Intent intent = new Intent();
                intent.setClass(PhoneCallDemo.this, AddNewPhone.class);
                //启动
                //startActivity(intent);
                startActivityForResult(intent,1000);
            }
        });
        dagu.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                String daguPhomeNum = "13280533817";
                Intent phoneIntent = new Intent("android.intent.action.CALL",
                        Uri.parse("tel:" + daguPhomeNum));
                //启动
                startActivity(phoneIntent);
            }
        });
        bingbing.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                String bingBingPhoneNum = "13854975246";
                Intent phoneIntent = new Intent("android.intent.action.CALL",
                        Uri.parse("tel:" + bingBingPhoneNum));
                //启动
                startActivity(phoneIntent);
            }
        });
        dujun.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                String dujunPhoneNum = "13505605306";
                Intent phoneIntent = new Intent("android.intent.action.CALL",
                        Uri.parse("tel:" + dujunPhoneNum));
                //启动
                startActivity(phoneIntent);
            }
        });
        jiangjiang.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                String jiangJiangPhoneNum = "18698868895";
                Intent phoneIntent = new Intent("android.intent.action.CALL",
                        Uri.parse("tel:" + jiangJiangPhoneNum));
                //启动
                startActivity(phoneIntent);
            }
        });
        //增加事件响应

        bt.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {

                //取得输入的电话号码串
                String inputStr = et.getText().toString();
                //如果输入不为空创建打电话的Intent
                if(inputStr.trim().length()!=0)
                {
                    Intent phoneIntent = new Intent("android.intent.action.CALL",
                            Uri.parse("tel:" + inputStr));
                    //启动
                    startActivity(phoneIntent);
                }
                //否则Toast提示一下
                else{
                    Toast.makeText(PhoneCallDemo.this, "不能输入为空", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    public boolean onLongClick(View v)
    {
        Integer imageButtoId = v.getId();
        deleteImageButtonId = imageButtoId;
        String phoneNum = map.get(imageButtoId);
        deletePhoneNum = phoneNum;
        new AlertDialog.Builder(this)
                .setTitle("编辑|删除")
                .setNegativeButton("编辑", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent intent = new Intent();

                        intent.putExtra("editPhoneNum",map.get(deleteImageButtonId));
                        intent.putExtra("editId", deleteImageButtonId);
                        intent.setClass(PhoneCallDemo.this,DeletePhone.class);

                        startActivityForResult(intent, 1001);

                    }
                })
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        ImageButton delImageButton = (ImageButton)findViewById((int)deleteImageButtonId);

                        map.remove(deleteImageButtonId);
                        String coverStr = FileOperate.composeMapStr(map);
                        FileOperate.WriteFileCover(phoneBookFilePath, coverStr);
                        String tmpImageFilePath = imagePath+deletePhoneNum+".png";
                        File tmpImageFile = new File(tmpImageFilePath);
                        tmpImageFile.delete();
                        linerLayout.removeView(delImageButton);
                    }
                }).show();
        return true;
    }
    public void onClick(View v){
        Integer imageButtoId = v.getId();
        String phoneNum = map.get(imageButtoId);
        Intent phoneIntent = new Intent("android.intent.action.CALL",
                Uri.parse("tel:" + phoneNum));
        startActivity(phoneIntent);

        switch(v.getId()){
	    	/*
	    			case 100:
	            	   String changjianPhoneNum = "18698868895";
		        		Intent phoneIntent = new Intent("android.intent.action.CALL",
		     			       Uri.parse("tel:" + changjianPhoneNum));
		        		//启动
		        		startActivity(phoneIntent);
	            	   break;
	            	   */
            case R.id.top_add:
                ShowPickDialog();
                break;
            default:;
        }

    }
    /**
     * 选择提示对话框
     */
    private void ShowPickDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置头像...")
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        /**
                         * 刚开始，我自己也不知道ACTION_PICK是干嘛的，后来直接看Intent源码，
                         * 可以发现里面很多东西，Intent是个很强大的东西，大家一定仔细阅读下
                         */
                        Intent intent = new Intent(Intent.ACTION_PICK, null);

                        /**
                         * 下面这句话，与其它方式写是一样的效果，如果：
                         * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                         * intent.setType(""image/*");设置数据类型
                         * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                         * 这个地方小马有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？
                         */
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "image/*");
                        startActivityForResult(intent, 1);

                    }
                })
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        /**
                         * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
                         * 文档，you_sdk_path/docs/guide/topics/media/camera.html
                         * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
                         * 官方文档太长了就不看了，其实是错的，这个地方小马也错了，必须改正
                         */
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        //下面这句指定调用相机拍照后的照片存储的路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                .fromFile(new File(Environment
                                        .getExternalStorageDirectory(),
                                        "xiaoma.jpg")));
                        startActivityForResult(intent, 2);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.e("num:","haaaaaaaaaaaaaaaaaaa");
        //添加联系人的操作返回处理
        if(requestCode == 1000 && resultCode == 1001)
        {
            String phoneNum = data.getStringExtra("phoneNum");
            //Log.e("num:",phoneNum);
            Bitmap photo = data.getParcelableExtra("bitPhoto");

            String imageFilePath = imagePath+phoneNum+".png";
            File tmpImageFile = new File(imageFilePath);
            try{
                FileOutputStream out = new FileOutputStream(tmpImageFile);
                photo.compress(Bitmap.CompressFormat.PNG, 60, out);
                try{
                    out.flush();
                    out.close();
                }catch (IOException e){

                }

            }catch (FileNotFoundException e){

            }
            Drawable drawable = new BitmapDrawable(photo);
            ImageButton addImageButton = new ImageButton(this);
            increaseNum += 1;
            map.put(increaseNum, phoneNum);
            addImageButton.setId(increaseNum);
            addImageButton.setScaleType(ImageView.ScaleType.FIT_XY);
            addImageButton.setLayoutParams(imagebtn_params);
            addImageButton.setImageDrawable(drawable);
            addImageButton.setOnClickListener(this);
            linerLayout.addView(addImageButton);
            FileOperate.WriteFileAppend(phoneBookFilePath,phoneNum+"\n");
        }
        //修改联系人的添加处理
        if(requestCode == 1001 && resultCode == 1001)
        {
            String phoneNum = data.getStringExtra("phoneNum");
            Bitmap photo = data.getParcelableExtra("bitPhoto");
            int editId = data.getIntExtra("editId", 0);
            String rawPhoneNum = data.getStringExtra("rawPhoneNum");

            boolean changedHeadImage = data.getBooleanExtra("changedHeadImage", false);
            boolean changedPhoneNum = data.getBooleanExtra("changedPhoneNum", false);
            if(changedHeadImage == true)
            {
                String imageFilePath = imagePath+phoneNum+".png";
                Log.e("phoneNum:",imageFilePath);
                File tmpImageFile = new File(imageFilePath);
                try{
                    FileOutputStream out = new FileOutputStream(tmpImageFile);
                    photo.compress(Bitmap.CompressFormat.PNG, 60, out);
                    try{
                        out.flush();
                        out.close();
                    }catch (IOException e){

                    }

                }catch (FileNotFoundException e){

                }
                Drawable drawable = new BitmapDrawable(photo);
                ImageButton editImageButton = (ImageButton)findViewById(editId);
                editImageButton.setImageDrawable(drawable);
            }
            if(changedPhoneNum == true)
            {
                map.put(editId,phoneNum);
                String coverStr = FileOperate.composeMapStr(map);
                FileOperate.WriteFileCover(phoneBookFilePath, coverStr);

                String imageFilePath = imagePath+phoneNum+".png";
                File tmpImageFile = new File(imageFilePath);
                try{
                    FileOutputStream out = new FileOutputStream(tmpImageFile);
                    photo.compress(Bitmap.CompressFormat.PNG, 60, out);
                    try{
                        out.flush();
                        out.close();
                    }catch (IOException e){

                    }

                }catch (FileNotFoundException e){

                }
                String deleteFilePath = imagePath+rawPhoneNum+".png";
                File deleteFile = new File(deleteFilePath);
                deleteFile.delete();
            }
        }
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
         * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
         * 制做的了...吼吼
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
    /**
     * 保存裁剪之后的图片数据
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            //将Bitmap保存为png图片
            File tmpImageFile = new File(imageFilePath);
            try{
                FileOutputStream out = new FileOutputStream(tmpImageFile);
                photo.compress(Bitmap.CompressFormat.PNG, 60, out);
                try{
                    out.flush();
                    out.close();
                }catch (IOException e){

                }

            }catch (FileNotFoundException e){

            }

            Drawable drawable = new BitmapDrawable(photo);

            /**
             * 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上
             * 传到服务器，QQ头像上传采用的方法跟这个类似
             */

				/*ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
				byte[] b = stream.toByteArray();
				// 将图片流以字符串形式存储下来

				tp = new String(Base64Coder.encodeLines(b));
				这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了，
				服务器处理的方法是服务器那边的事了，吼吼

				如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换
				为我们可以用的图片类型就OK啦...吼吼
				Bitmap dBitmap = BitmapFactory.decodeFile(tp);
				Drawable drawable = new BitmapDrawable(dBitmap);
				*/
            ImageButton dynamicButton = new ImageButton(this);
            dynamicButton.setScaleType(ImageView.ScaleType.FIT_XY);
            dynamicButton.setLayoutParams(imagebtn_params);
            dynamicButton.setBackground(drawable);
            linerLayout.addView(dynamicButton);
            //ib.setBackgroundDrawable(drawable);
            //iv.setBackgroundDrawable(drawable);
        }
    }
    private void makeProjectPath()
    {
        File dirFirstFile=new File(phoneCallProjectPath);//新建一级主目录
        if(!dirFirstFile.exists()){//判断文件夹目录是否存在
            dirFirstFile.mkdir();//如果不存在则创建
        }
        File dirSecondFile=new File(imagePath);//新建二级主目录
        if(!dirSecondFile.exists()){//判断文件夹目录是否存在
            dirSecondFile.mkdir();//如果不存在则创建
        }
    }
}
class StartClick implements OnClickListener{
    public void onClick(View v){
        switch(v.getId()){
            case 100:
                String changjianPhoneNum = "18698868895";
                Intent phoneIntent = new Intent("android.intent.action.CALL",
                        Uri.parse("tel:" + changjianPhoneNum));
                //启动
                //startActivity(phoneIntent);
                break;
            default:;

        }
    }
}