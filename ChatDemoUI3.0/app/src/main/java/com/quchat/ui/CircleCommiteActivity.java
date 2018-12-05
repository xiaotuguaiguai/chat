package com.quchat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.hyphenate.easeui.ui.EaseBaiduMapActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.adapter.RecyclerGridViewAdapter;
import com.quchat.domain.CodeAndMsg;
import com.quchat.utils.ImageUtils;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.Permission;
import com.quchat.utils.StringUtils;
import com.quchat.utils.TempCache;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class CircleCommiteActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks ,View.OnClickListener {
    private RecyclerView recyclerView;
    private EditText content_eidt;
    private RecyclerGridViewAdapter adapter;
    private ArrayList<String> localMediaList = new ArrayList<>();
    private ArrayList<File> localMediaList2 = new ArrayList<>();
    private static final int REQUEST_CODE_CHOOSE = 23;
    private Button save;
    private TextView cancel,allow_type_text,address;
    private RelativeLayout choose_item_1,choose_item_2;
    private int allow_type=1;
    private String address_name,address_value;
    private boolean isText;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_commite);
        isText = getIntent().getBooleanExtra("isText",false);


        localMediaList= getIntent().getStringArrayListExtra("list");
        if(null==localMediaList){
            localMediaList = new ArrayList<>();
        }
        recyclerView=findViewById(R.id.recycleview);

        if(isText){
            recyclerView.setVisibility(View.GONE);
        }
        cancel=findViewById(R.id.cancel);
        choose_item_1=findViewById(R.id.choose_item_1);
        choose_item_2=findViewById(R.id.choose_item_2);
        allow_type_text=findViewById(R.id.allow_type_text);
        address=findViewById(R.id.address);

        if(!TextUtils.isEmpty(TempCache.UserBean.getData().getAddress_name())){
            address.setText(TempCache.UserBean.getData().getAddress_name());
        }


        choose_item_1.setOnClickListener(this);
        choose_item_2.setOnClickListener(this);

        cancel.setOnClickListener(this);
        save=findViewById(R.id.save);
        save.setOnClickListener(this);
        content_eidt=findViewById(R.id.content_eidt);
        GridLayoutManager mgr = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mgr);
//        localMediaList.add(null);
        adapter = new RecyclerGridViewAdapter(this,localMediaList);
        recyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemListener(new RecyclerGridViewAdapter.OnRecyclerViewItemListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                if(position==localMediaList.size()){
                    initImage();
                }else{
                    showBig(position);
                }

            }

            @Override
            public void onItemCancelClickListener(View view, int position) {
                localMediaList.remove(position);

                adapter.notifyDataSetChanged();
            }
        });

    }
    private  ProgressDialog dialog;
    private void commpass(){
        if(TextUtils.isEmpty(content_eidt.getText().toString()) && localMediaList.size()==0){
            Toast.makeText(this,"请输入内容",Toast.LENGTH_SHORT).show();
            return;
        }
        if(isText){
            if(TextUtils.isEmpty(content_eidt.getText().toString())){
                Toast.makeText(this,"请输入内容",Toast.LENGTH_SHORT).show();
                return;
            }
            initProgressDialog();
            send(localMediaList2);
        }else{
            if(null==localMediaList||localMediaList.size()==0){
                initProgressDialog();
                send(localMediaList2);
            }else {
                initProgressDialog();
                Luban.with(this)
                        .load(localMediaList)
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
                                // TODO 压缩开始前调用，可以在方法内启动 loading UI
                            }

                            @Override
                            public void onSuccess(File file) {
                                localMediaList2.add(file);
                                if(localMediaList2.size()==localMediaList.size()){
                                    send(localMediaList2);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                // TODO 当压缩过程出现问题时调用
                            }
                        }).launch();
            }
        }


    }
    private void initProgressDialog(){
        dialog= null;
        dialog = ProgressDialog.show(this,"","上传中");
        dialog.show();

    }
    private void dismissDialog(){
        if(null!=dialog){
            dialog.dismiss();
        }
    }
    private void showBig(int position){
        PhotoPreview.builder()
                .setPhotos(localMediaList)
                .setCurrentItem(position)
                .setShowDeleteButton(true)
                .start(this);
    }
    private void initImage(){
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setSelected(localMediaList)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    private List<String> friend_list = new ArrayList<>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                if(requestCode==101){

                    allow_type = data.getIntExtra("allow_type",1);

                    if(allow_type==1){
                        allow_type_text.setText("所有人可见");
                    }else if(allow_type==2){
                        allow_type_text.setText("仅自己可见");
                    }else if(allow_type==3){
                        allow_type_text.setText("部分人可见");
                    }
                    friend_list = data.getStringArrayListExtra("result");
                }else if(requestCode==102){
                    address_name= data.getStringExtra("address");
                    address_value= data.getDoubleExtra("latitude",0)+","+data.getDoubleExtra("longitude",0);
                    address.setText(address_name);
                }
               else if(requestCode == PhotoPicker.REQUEST_CODE ){
                    if (data != null) {
                        ArrayList<String> photos =
                                data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        localMediaList.clear();
                        localMediaList.addAll(photos);
                        if(localMediaList.size()<9){
//                            localMediaList.add("");
                        }
                        adapter.notifyDataSetChanged();
                    }
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }


    private void send(List<File> fileList){

// .params("friend_list[]",friend_list.toString())

        OkGo.<String>post(URL.CIRCLE_SNED).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("user_name",Constant.USERNAME)
                .params("content",content_eidt.getText().toString())
                .params("allow_type",allow_type_text+"")
                .params("address_name",address_name)
                .params("address_value",address_value)

                .addFileParams("images[]",fileList)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                       Log.e("ss",response.body());
                        CodeAndMsg codeAndMsg =(CodeAndMsg)JsonUitl.stringToObject(response.body(),CodeAndMsg.class);
                        if(codeAndMsg.getCode()==200){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CircleCommiteActivity.this,"上传成功",Toast.LENGTH_SHORT).show();

                                    finish();
                                }
                            });
                        }else{
                            Toast.makeText(CircleCommiteActivity.this,codeAndMsg.getMsg(),Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        dismissDialog();
                        super.onError(response);
//                        Log.e("ss",response.body());
                    }
                });
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save:
                commpass();
                break;
            case R.id.cancel:
                finish();
                break;
            case R.id.choose_item_1:
                startActivityForResult(new Intent(CircleCommiteActivity.this,EaseBaiduMapActivity.class),102);

                break;
            case R.id.choose_item_2:
                startActivityForResult(new Intent(CircleCommiteActivity.this,CircleSettingActivity.class),101);
            break;
        }
    }

}
