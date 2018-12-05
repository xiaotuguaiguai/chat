package com.quchat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonObject;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.PathUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.Constant;
import com.quchat.utils.ImageUtils;
import com.quchat.utils.Permission;
import com.quchat.utils.StringUtils;
import com.quchat.URL;
import com.quchat.utils.TempCache;
import com.quchat.widget.CircleImageView;
import com.quchat.widget.XCRoundRectImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks {
    private RelativeLayout faceLayout,sexNameLayout,nickNameLayout,signNameLayout,addressNameLayout,numNameLayout;
    private static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_CAMERA = 2;
    private File cameraFile;
    private String headUrl="",nickname="",nicknameTemp,signTemp,address_nameTemp,sign,address_name,address_ids;
    private int sex,sexTemp;
    private CircleImageView face;
    private TextView nickName,sexText,numText,signText,addressText;
    private RequestOptions options;
    private Button save;
    private ImageView img_back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        nickName=findViewById(R.id.nickName);
        numText=findViewById(R.id.numText);
        save=findViewById(R.id.save);
        addressText=findViewById(R.id.addressText);

        signText=findViewById(R.id.signText);
        sexText=findViewById(R.id.sexText);
        img_back=findViewById(R.id.img_back);
        signNameLayout=findViewById(R.id.signNameLayout);
        addressNameLayout=findViewById(R.id.addressNameLayout);
        numNameLayout=findViewById(R.id.numNameLayout);

        face=findViewById(R.id.face);
        headUrl=TempCache.UserBean.getData().getHead_url();
        sex=TempCache.UserBean.getData().getSex();
        nickname=TempCache.UserBean.getData().getNick_name();
        sign=TempCache.UserBean.getData().getSignature();
        address_name=TempCache.UserBean.getData().getAddress_name();
        address_ids=TempCache.UserBean.getData().getAddress_ids();
        nicknameTemp=nickname;
        sexTemp=sex;
        signTemp=sign;
        address_nameTemp=address_name;
        faceLayout=findViewById(R.id.faceLayout);
        sexNameLayout=findViewById(R.id.sexNameLayout);
        nickNameLayout=findViewById(R.id.nickNameLayout);
        options = new RequestOptions();
        options.placeholder(R.drawable.logo);

        signNameLayout.setOnClickListener(this);
        addressNameLayout.setOnClickListener(this);
        numNameLayout.setOnClickListener(this);

        img_back.setOnClickListener(this);
        save.setOnClickListener(this);
        faceLayout.setOnClickListener(this);
        sexNameLayout.setOnClickListener(this);
        nickNameLayout.setOnClickListener(this);


        Glide.with(this).load(headUrl).apply(options).into(face);
        nickName.setText(nickname);
        numText.setText(Constant.USERNAME);
        if(sex==1){
            sexText.setText("男");
        }else{
            sexText.setText("女");
        }
        signText.setText(sign);
        addressText.setText(address_name);

    }
    private  ProgressDialog progressDialog;

    private void updateInfo(final String img){

        OkGo.<String>post(URL.UPDATEINFO).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",Constant.USERNAME)
                .params("nickname",nickname)
                .params("sex",sex)
                .params("address_ids",address_ids)
                .params("address_name",address_name)
                .params("signature",sign)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                     Log.e("ss",response.body().toString());
                     if(null!=progressDialog){
                         progressDialog.dismiss();
                     }

                        Intent intent = new Intent();
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("pic", img);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onError(Response<String> response) {
                       Toast.makeText(UserInfoActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                        super.onError(response);
                        if(null!=progressDialog){
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void uploadImg(File f){
        OkGo.<String>post(URL.UPLOAD_HEADER).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("user_name",Constant.USERNAME)
                .params("header_image",f)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        try {
                            JSONObject jsonObject= new JSONObject(response.body().toString());
                            if(jsonObject.optInt("code")==200){
                                JSONObject jsonObject2= jsonObject.optJSONObject("data");
                                String header_img=jsonObject2.optString("head_url");
                                if(sex!=sexTemp || !nickname.equals(nicknameTemp) || !signTemp.equals(sign) || !address_nameTemp.equals(address_name)){
                                    updateInfo(header_img);
                                }else {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra("nickname", nickname);
                                    intent.putExtra("pic", header_img);
                                    setResult(RESULT_OK, intent);

                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Log.e("ss",e.getMessage());
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("ss",response.body());
                    }
                });
    }
    private void compass(String path){

        Luban.with(this)
                .load(path)
                .ignoreBy(200)
                .setTargetDir(ImageUtils.ImagePath)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        uploadImg(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        uploadImg(cameraFile);
                    }
                }).launch();

    }

    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(this, "SD卡不存在，不能拍照", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if (EasyPermissions.hasPermissions(this, Permission.STORAGE)
                    &&EasyPermissions.hasPermissions(this, Permission.CAMERA)) {//检查是否获取该权限
                cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                        + System.currentTimeMillis() + ".jpg");
                //noinspection ResultOfMethodCallIgnored
                cameraFile.getParentFile().mkdirs();
                // 7.0+ 8.0+ 调用相机适配
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            FileProvider.getUriForFile(this,"com.quchat.fileProvider", cameraFile));
                }else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                }
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            } else {
                //第二个参数是被拒绝后再次申请该权限的解释
                //第三个参数是请求码
                //第四个参数是要申请的权限
                EasyPermissions.requestPermissions(this, "需要必要的权限", 0, Permission.STORAGE);
                EasyPermissions.requestPermissions(this, "需要必要的权限", 1, Permission.CAMERA);


            }
        }

    }

    protected void selectPicFromLocal() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if (EasyPermissions.hasPermissions(this, Permission.STORAGE)){
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");

                } else {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, REQUEST_CODE_LOCAL);
            }else{
                EasyPermissions.requestPermissions(this, "需要必要的权限", 0, Permission.STORAGE);

            }
        }

    }
    private String sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor =this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return "";
            }
            return picturePath;
//            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {

                return "";

            }
            return file.getAbsolutePath();
//            sendImageMessage(file.getAbsolutePath());
        }

    }

    private void save(){
        progressDialog = ProgressDialog.show(this,"","保存中");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setCancelable(true);
        if(cameraFile!=null){
            compass(cameraFile.getAbsolutePath());
        }else {

            updateInfo("");
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        save();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                save();
                break;
            case R.id.faceLayout:
                showListDialog();
            break;
            case R.id.nickNameLayout:
                startActivityForResult(new Intent(this,SaveNickNameActivity.class).putExtra("name",nickname),100);
                break;
            case R.id.sexNameLayout:
                showListDialog2();
                break;
            case R.id.save:
                progressDialog = ProgressDialog.show(this,"","保存中");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.setCancelable(true);
                if(cameraFile!=null){
                    compass(cameraFile.getAbsolutePath());

                }else {
                    updateInfo("");
                }
                break;
            case R.id.signNameLayout:
                startActivityForResult(new Intent(this,SaveSignActivity.class).putExtra("sign",signText.getText().toString()),110);

                break;
            case R.id.addressNameLayout:
                startActivityForResult(new Intent(this,EaseBaiduMapActivity.class),120);
                break;
            case R.id.numNameLayout:
//                Toast.makeText(this,"暂未开通",Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void showListDialog()
    {
        final String[]items = { "相机","图库"};
        AlertDialog.Builder
                listDialog =
                new AlertDialog.Builder(this);

        listDialog.setItems(items,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface
                                                dialog, int which)
                    {
                        if(which==0){
                            selectPicFromCamera();
                        }else{
                            selectPicFromLocal();
                        }

                    }
                });
        listDialog.show();
    }
    private void showListDialog2()
    {
        final String[]items = { "男","女"};
        AlertDialog.Builder
                listDialog =
                new AlertDialog.Builder(this);

        listDialog.setItems(items,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface
                                                dialog, int which)
                    {
                        if(which==0){
                            sex=1;
                            sexText.setText("男");
                        }else{
                            sex=2;
                            sexText.setText("女");
                        }

                    }
                });
        listDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image

                if (cameraFile != null && cameraFile.exists()){
//                    createTempImageFile();
//                    compass(cameraFile.getAbsolutePath());
                    Glide.with(this).load(cameraFile.getAbsolutePath()).into(face);
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        try {
                           String string= sendPicByUri(selectedImage);
                           if(!StringUtils.isEmpty(string)){
                               cameraFile = new File(string);
//                               compass(cameraFile.getAbsolutePath());
//                               createTempImageFile();
                               RequestOptions options = new RequestOptions();
                               options.placeholder(R.drawable.logo);
                                Glide.with(this).load(string).into(face);
                           }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else if(requestCode==100){
               String s=nickname= data.getExtras().getString("result");
               nickName.setText(s);
            }else if(requestCode==110){
                String s=sign= data.getExtras().getString("result");
                signText.setText(s);
            }else if(requestCode==120){
                String s=address_name=data.getExtras().getString("address");
                address_ids= data.getExtras().getDouble("latitude",0)+","+ data.getExtras().getDouble("longitude",0);
                addressText.setText(s);
            }


        }

    }

    private void createTempImageFile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap   bitmap2=null;
                try {
                    bitmap2=  Glide.with(UserInfoActivity.this).asBitmap().load(cameraFile).submit(100,100).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                File tempFile = null;
                if(null !=bitmap2){
                    tempFile=   ImageUtils.saveBitmapFile(bitmap2,System.currentTimeMillis()+"");
                }
                if(null !=tempFile){
                    cameraFile = tempFile;
                }else{
                    cameraFile = new File(cameraFile.getAbsolutePath());

                }
            }
        }).start();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

}
