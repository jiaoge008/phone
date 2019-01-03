package com.example.xianjiao.phone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.util.DisplayMetrics;

import com.example.phonecalldemo.R;

public class AddNewPhone extends Activity implements OnClickListener {

    private DisplayMetrics dm;
    private LinearLayout.LayoutParams imagebtn_params;
    private ImageButton addHeadImageButton;
    private Button addPersonButton;
    private EditText phoneNumEditText;

    private Bitmap backPicMap = null;

    private final static String projectName = "dadianhua";
    private final static String phoneCallProjectPath = Environment.getExternalStorageDirectory() + File.separator + projectName + File.separator;
    private final static String imagePath = phoneCallProjectPath + "images" + File.separator;
    private final static String imageFilePath = imagePath + "myPic.png";
    private final static String phoneBookFilePath = phoneCallProjectPath + "dianhuaben.txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.add_new_phone);


        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        imagebtn_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imagebtn_params.height = dm.heightPixels / 2;
        imagebtn_params.width = dm.widthPixels;

        addHeadImageButton = (ImageButton) findViewById(R.id.add_head_image);
        addHeadImageButton.setLayoutParams(imagebtn_params);
        addHeadImageButton.setOnClickListener(this);

        addPersonButton = (Button) findViewById(R.id.add_person);
        addPersonButton.setOnClickListener(this);
        phoneNumEditText = (EditText) findViewById(R.id.phone_num);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_head_image:
                ShowPickDialog();
                break;
            case R.id.add_person:
                if (backPicMap == null) {
                    Toast.makeText(AddNewPhone.this, "请先设置头像", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(AddNewPhone.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("提示")
                            .setMessage("请先设置头像")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("bitPhoto", backPicMap);
                //取得输入的电话号码串
                String inputStr = phoneNumEditText.getText().toString();
                if (inputStr.trim().length() == 0) {
                    Toast.makeText(AddNewPhone.this, "电话号码不能为空", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(AddNewPhone.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("提示")
                            .setMessage("请先输入电话号码")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                //检查电话号码是否已存在
                HashMap<Integer, String> map;
                map = FileOperate.ReadTxtFile(phoneBookFilePath);
                Iterator iter = map.keySet().iterator();
                while (iter.hasNext()) {
                    Object key = iter.next();
                    Object val = map.get(key);
                    Integer integerKey = (Integer) key;
                    String stringVal = (String) val;
                    if (stringVal.equals(inputStr)) {
                        Log.e("rawVal:", stringVal);
                        Log.e("rawInputVal:", inputStr);
                        new AlertDialog.Builder(AddNewPhone.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("提示")
                                .setMessage("电话号码" + inputStr + "已存在")
                                .setPositiveButton("确定", null)
                                .show();
                        return;
                    }
                }

                intent.putExtra("phoneNum", inputStr);
                setResult(1001, intent);
                finish();
                break;
            default:
                ;

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

//                        Intent intentNew = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //startActivityForResult(Intent.createChooser(intentNew, "Select image from your gallery"), 1);
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "image/*");
                        //intent.setType("image/*");
                        try {
                            startActivityForResult(intent, 1);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }

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
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                //cropImage(data.getData());
                startPhotoZoom(data.getData());
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/xiaoma.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            // 取得裁剪后的图片
            case 3:
                /**
                 * 非空判断大家一定要验证，如果不验证的话，
                 * 在剪裁之后如果发现不满意，要重新裁剪，丢弃
                 * 当前功能时，会报NullException，小马只
                 * 在这个地方加下，大家可以根据不同情况在合适的
                 * 地方做判断处理类似情况
                 *
                 */

                File smallTemp = new File("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
                if (smallTemp.exists()) {
                    System.out.println("samll haha : cunzai");
                } else {
                    System.out.println("small haha : bu cunzai");
                }
                Uri dataUri = Uri.fromFile(smallTemp);
                data.setDataAndType(dataUri, "image/*");
                System.out.println("dataUri path:" + dataUri.getPath());
                if (data != null) {
                    setPicToView(data);
                } else {
                    System.out.println("数据为空");
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
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
        //intent.putExtra("return-data", false);
//        File temp = new File("/storage/emulated/0/DCIM/Camera/IMG_20181224_123657.jpg");
//        if (temp.exists()) {
//            System.out.println("tem cunzai");
//        } else {
//            System.out.println("tem bu cunzai");
//        }
//        System.out.println("uri path:" + uri.getPath());
//        System.out.println("uri1111:" + uri.toString());
//        uri = Uri.fromFile(temp);
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);

        //uritempFile为Uri类变量，实例化uritempFile
        Uri uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            backPicMap = photo;
            //将Bitmap保存为png图片
			/*
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
			*/

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
            ImageButton addHeadImage = (ImageButton) findViewById(R.id.add_head_image);
            addHeadImage.setScaleType(ImageView.ScaleType.FIT_XY);
            addHeadImage.setLayoutParams(imagebtn_params);
            addHeadImage.setImageDrawable(drawable);
            //addHeadImage.setBackground(drawable);
        } else {
            //如果extras 为空的话，说明走的是外部存储
            Uri uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
            File tempFile = new File(uritempFile.getPath());

            try {

                FileInputStream inputStream = new FileInputStream(tempFile);
                Bitmap photo = BitmapFactory.decodeStream(inputStream);
                backPicMap = photo;
                Drawable drawable = new BitmapDrawable(photo);
                ImageButton addHeadImage = (ImageButton) findViewById(R.id.add_head_image);
                addHeadImage.setScaleType(ImageView.ScaleType.FIT_XY);
                addHeadImage.setLayoutParams(imagebtn_params);
                addHeadImage.setImageDrawable(drawable);
            } catch (Exception e) {
                System.out.println("FileOutputStream error:" + e);
            }
        }
    }

}
