package com.example.xianjiao.phone;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.util.Log;
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

public class DeletePhone extends Activity implements OnClickListener{

    private DisplayMetrics dm;
    private LinearLayout.LayoutParams imagebtn_params;
    private ImageButton deleteHeadImageButton;
    private EditText phoneNumEditText;
    private Button modifyButton;

    private Bitmap backPicMap=null;

    private String editPhoneNum = "";
    private int editId;
    private boolean changedHeadImage = false;
    private boolean changedPhoneNum = false;

    private final static String projectName = "dadianhua";
    private final static String phoneCallProjectPath = Environment.getExternalStorageDirectory()+File.separator+projectName+File.separator;
    private final static String imagePath = phoneCallProjectPath+"images"+File.separator;
    private final static String phoneBookFilePath = phoneCallProjectPath+"dianhuaben.txt";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.delete_phone);
        //获取PhoneCallDemo传递过来的参数
        Intent intent = getIntent();
        editPhoneNum = intent.getStringExtra("editPhoneNum");
        editId = intent.getIntExtra("editId", 0);

        deleteHeadImageButton = (ImageButton)findViewById(R.id.delete_head_image);
        phoneNumEditText = (EditText)findViewById(R.id.delete_phone_num);
        modifyButton = (Button)findViewById(R.id.modify_person);

        phoneNumEditText.setText(editPhoneNum);

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        imagebtn_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imagebtn_params.height = dm.heightPixels/2;
        imagebtn_params.width = dm.widthPixels;
        deleteHeadImageButton.setScaleType(ImageView.ScaleType.FIT_XY);
        deleteHeadImageButton.setLayoutParams(imagebtn_params);
        String imageFilePath = imagePath+editPhoneNum+".png";
        File tmpImageFile = new File(imageFilePath);
        try{
            FileInputStream in = new FileInputStream(tmpImageFile);
            try{
                backPicMap = BitmapFactory.decodeStream(in);
                Drawable drawable = new BitmapDrawable(backPicMap);
                deleteHeadImageButton.setImageDrawable(drawable);
                in.close();
            }catch (IOException e){

            }

        }catch (FileNotFoundException e){

        }
        deleteHeadImageButton.setOnClickListener(this);
        modifyButton.setOnClickListener(this);
        //deleteHeadImageButton.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        switch(v.getId()){

            case R.id.delete_head_image:
                ShowPickDialog();
                break;
            case R.id.modify_person:
                //获得修改后的电话号码
                String inputStr = phoneNumEditText.getText().toString();
                if(inputStr.trim().length()==0)
                {
                    new AlertDialog.Builder(DeletePhone.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("提示")
                            .setMessage("电话号码为空")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                Log.e("editNum:",editPhoneNum);
                Log.e("inputstr:",inputStr);
                if(!editPhoneNum.equals(inputStr))
                {
                    changedPhoneNum = true;
                }
                //如果电话号码和头像都没有改变的话不用向初始窗口回传参数
                if(changedPhoneNum==false && changedHeadImage==false)
                {
                    finish();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("bitPhoto", backPicMap);
                intent.putExtra("phoneNum", inputStr);
                intent.putExtra("editId", editId);
                intent.putExtra("rawPhoneNum", editPhoneNum);
                intent.putExtra("changedPhoneNum",changedPhoneNum);
                intent.putExtra("changedHeadImage", changedHeadImage);
                Log.d("deletephone:","jkll");
                setResult(1001, intent);
                break;
            default:;
        }
        finish();

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
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
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
                if(data != null){
                    setPicToView(data);
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * 裁剪图片方法实现
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

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
            backPicMap = photo;
            changedHeadImage = true;
            Drawable drawable = new BitmapDrawable(photo);

            deleteHeadImageButton = (ImageButton)findViewById(R.id.delete_head_image);
            deleteHeadImageButton.setScaleType(ImageView.ScaleType.FIT_XY);
            deleteHeadImageButton.setLayoutParams(imagebtn_params);
            deleteHeadImageButton.setImageDrawable(drawable);
            //addHeadImage.setBackground(drawable);
        }
    }
}
