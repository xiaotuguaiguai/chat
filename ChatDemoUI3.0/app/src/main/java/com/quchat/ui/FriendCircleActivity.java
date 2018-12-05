package com.quchat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.adapter.FriendCircleAdapter;
import com.quchat.domain.CircleCommonBean;
import com.quchat.domain.CodeAndMsg;
import com.quchat.domain.FriendCircleBean;
import com.quchat.domain.FriendCircleData;
import com.quchat.domain.UploadCoverBean;
import com.quchat.ui.BaseActivity;
import com.quchat.utils.ImageUtils;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.Permission;
import com.quchat.utils.StringUtils;
import com.quchat.utils.TempCache;
import com.quchat.widget.CircleImageView;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.iwf.photopicker.PhotoPicker;
import pub.devrel.easypermissions.EasyPermissions;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.PathUtil;

public class FriendCircleActivity extends AppCompatActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_CAMERA2 = 4;
    private File cameraFile;
    private RecyclerView recyclerView;
    private int pageNo=1;
//    private ImageView commite;
    private FriendCircleAdapter adapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private List<FriendCircleData> list = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog dialog;
    private boolean loading;
    private RelativeLayout send_layout;
    private Button sendBtn;
    private EditText send_content;
    private InputMethodManager imm;
    private int clickPosition;
    private FriendCircleData clickData;
    private boolean isSendItem;

    private int sendItemPosition;
    private CircleCommonBean itemData;

    private TextView bg_name;
    private CircleImageView bg_face;

    private ImageView big_pic,img_back,commite;
    private int from;
    private String fengmianAuth="eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImU3Yjk0ZmUzZmZjZmU4Mzk1ZGQyNTBmNmJhYTE0MTA3OTM3YzFjZmZkODAxYTMwZjUzZDZhZWE1MDQzMGUyYjdhZTg2MjVlMGQzZDBmZGJkIn0.eyJhdWQiOiIxIiwianRpIjoiZTdiOTRmZTNmZmNmZTgzOTVkZDI1MGY2YmFhMTQxMDc5MzdjMWNmZmQ4MDFhMzBmNTNkNmFlYTUwNDMwZTJiN2FlODYyNWUwZDNkMGZkYmQiLCJpYXQiOjE1NDE1MDQyNDgsIm5iZiI6MTU0MTUwNDI0OCwiZXhwIjoxNTczMDQwMjQ4LCJzdWIiOiIiLCJzY29wZXMiOltdfQ.QIKdkWhvNYAM3HRGVuSamjuvMrBwZ2XO9-60TQ9hqJf3jB9wc8stv8Q1pt8DDVi-TaJ552yM_9fXZFPfQSrgOtzEB288hGjTreFstcKCVdzBkoFUX-gWSbnLK8tJjOIhkbYJ6276OCAQ9oexERKi5mYVZcXL0mWM4rshpoCqhJMmMfpY_1_dk5PkZS1yYcAJwAJlOYK12f2OogcKhk73JIXPq_r6dQL0edSOb1_xRGmme6PAIupic5wj_jiE8rz26hXG6zWSgp-ovgvAE7N_1sZw18Vt0_uKHw6mrDZeR2sls2Z9HXrJDrsrhn-uGhoKy-67xIdoicC-qzMOXly4JyG_zT1eF-VQluqyDhh8XFviO57EZeYcaDwwvi7GxQnU0wmlijnYJG51hFU48eVP0iwTqVMraLvOMChys2tZlWxL7KlO251-m7-d-khWu3GsUkZEpGCDIwzJbB9kE0mamYriHs0UuD9FYVCF52WDY_CSMJxYUa2k_qQ1NOfkrsEeGw49Y_jTK5JL9DeXbnlTzIzetl92EBRrN5x_2N19jHDNDWaKNosTgtIhkSe0R9aQ8EiYtQLw3q8ybjMqqv1HKm_FkNzCcUDUp9xjB4a_i_rfBCvzVmbGgVtYvjPE4spbadzfOCzhwC4GlYjP6ub0dGZm1Nkaifl8vkESQlNTBKM";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        from=getIntent().getIntExtra("from",0);
        setContentView(R.layout.activity_friendcircle);
        commite=findViewById(R.id.commite);
        img_back=findViewById(R.id.img_back);
        recyclerView=findViewById(R.id.friendRecycle);
        send_layout=findViewById(R.id.send_layout);
        sendBtn=findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
        img_back.setOnClickListener(this);
        send_content=findViewById(R.id.send_content);
        mSwipeRefreshLayout=findViewById(R.id.swiperefreshlayout);
        commite.setOnClickListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hideLayout();
                pageNo=1;
                list.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                getList();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new FriendCircleAdapter(this,list);
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(adapter);
        View view = LayoutInflater.from(this).inflate(R.layout.friendcircle_head,null);
        bg_name=view.findViewById(R.id.bg_name);
        bg_face=view.findViewById(R.id.bg_face);
        big_pic=view.findViewById(R.id.big_pic);
        big_pic.setOnClickListener(this);

        if(null!=TempCache.UserBean.getData().getMoment_cover()){
            Glide.with(this).load(TempCache.UserBean.getData().getMoment_cover()).into(big_pic);
        }
        bg_name.setText(TempCache.UserBean.getData().getNick_name());
        Glide.with(this).load(TempCache.UserBean.getData().getHead_url()).into(bg_face);
        mHeaderAndFooterWrapper.addHeaderView(view);
        recyclerView.setAdapter(mHeaderAndFooterWrapper);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                hideLayout();
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount < (lastVisibleItem )) {
                    loading = true;
                    dialog = ProgressDialog.show(FriendCircleActivity.this,"","加载中");
                    pageNo++;
                    getList();
                }

            }
        });
        adapter.setInterface(new FriendCircleAdapter.CallBack() {
            @Override
            public void comment(int position,int mid,String toId) {
//                sendcomment(position,mid,toId);
            }

            @Override
            public void refresh() {
                mHeaderAndFooterWrapper.notifyDataSetChanged();
            }

            @Override
            public void showSendLayout(FriendCircleData data,int position) {
                if(TempCache.UserBean==null || TextUtils.isEmpty(Constant.USERNAME) || TextUtils.isEmpty(Constant.TOKEN)){
                    Toast.makeText(FriendCircleActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FriendCircleActivity.this,LoginActivity.class));
                    return;
                }
                isSendItem=false;
                clickData=data;
                clickPosition=position;
                showLayout();
            }

            @Override
            public void showSendLayoutByItem(int position1,int position2, CircleCommonBean data) {
                if(TempCache.UserBean==null || TextUtils.isEmpty(Constant.USERNAME) || TextUtils.isEmpty(Constant.TOKEN)){
                    Toast.makeText(FriendCircleActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FriendCircleActivity.this,LoginActivity.class));
                    return;
                }
                isSendItem=true;
                itemData=data;
                sendItemPosition=position1;
                showLayout();
//                sendcommentByItem();
            }
        });

    }

    private LinearLayout camera_layout,img_layout,text_layout;
    private void showDialog(){
      final  Dialog dialog = new Dialog(this, R.style.Dialog2);
        dialog.show();
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewDialog = inflater.inflate(R.layout.dialog_view, null);
        camera_layout=viewDialog.findViewById(R.id.camera_layout);
        img_layout=viewDialog.findViewById(R.id.img_layout);
        text_layout=viewDialog.findViewById(R.id.text_layout);

        camera_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localMediaList.clear();
                selectPicFromCamera(REQUEST_CODE_CAMERA2);
                dialog.dismiss();
            }
        });
        img_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localMediaList.clear();
                initImage();
                dialog.dismiss();
            }
        });
        text_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localMediaList.clear();
                startActivity(new Intent(FriendCircleActivity.this,CircleCommiteActivity.class)
                .putExtra("isText",true));
                dialog.dismiss();
            }
        });
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
        dialog.setContentView(viewDialog, layoutParams);
    }

    private ArrayList<String> localMediaList = new ArrayList<>();

    private void initImage(){
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setSelected(localMediaList)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        localMediaList.clear();
        pageNo=1;
        list.clear();
        adapter.setList(list);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
        getList();
    }

    private void hideLayout(){
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);

//        imm.hideSoftInputFromInputMethod(send_content.getWindowToken(),0);
        send_content.setText("");
        send_layout.setVisibility(View.GONE);

    }
    private void showLayout(){
        send_layout.setVisibility(View.VISIBLE);
        send_content.setFocusable(true);
        send_content.setFocusableInTouchMode(true);
        send_content.requestFocus();
        imm.showSoftInput(send_content,0);
    }
    private void getList(){
        String url;
        if(from==0){
            url= URL.CIRCLE_LIST;
        }else{
            url= URL.CIRCLE_LIST_MY;
        }
        OkGo.<String>get(url).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("user_name",Constant.USERNAME)
                .params("page",pageNo)
                .params("page_size",10)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        hideLayout();
                        FriendCircleBean bean = (FriendCircleBean)JsonUitl.stringToObject(response.body(),FriendCircleBean.class);
                        if(bean.getCode()==200){
                            List<FriendCircleData> list2=bean.getData();

                            list.addAll(list2);
                            adapter.setList(list2);
                            mHeaderAndFooterWrapper.notifyDataSetChanged();
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        if(null!=dialog){
                            dialog.dismiss();
                        }

                        loading=false;
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if(null!=dialog){
                            dialog.dismiss();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                        loading=false;
                    }
                });
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

    private void compass(String path){
        initProgressDialog();
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
                        uploadBigPic(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                       uploadBigPic(cameraFile);
                    }
                }).launch();

    }

    private void sendcomment0(){
        final ProgressDialog progressDialog = 	ProgressDialog.show(this, "", "提交中");
        OkGo.<String>post(URL.CIRCLE_SEND_COMMENT).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("user_name",Constant.USERNAME)
                .params("content",send_content.getText().toString())
                .params("mid",clickData.getId())
                .params("reply_to_user_name","")

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        CodeAndMsg bean = (CodeAndMsg)JsonUitl.stringToObject(response.body(),CodeAndMsg.class);

                        progressDialog.dismiss();
                        if(bean.getCode()==200){

                            CircleCommonBean circleCommonBean = new CircleCommonBean();
                            circleCommonBean.setContent(send_content.getText().toString());
                            circleCommonBean.setNick_name(clickData.getNick_name());
                            List<CircleCommonBean> ss=clickData.getComments();
                            ss.add(circleCommonBean);
                            list.get(clickPosition).setComments(ss);
                            list.get(clickPosition).setComment_num(list.get(clickPosition).getComment_num()+1);
                            adapter.setList(list);
                            mHeaderAndFooterWrapper.notifyDataSetChanged();
                        }
                        hideLayout();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        progressDialog.dismiss();
                    }
                });
    }
    private void sendcommentByItem(){
       final ProgressDialog progressDialog = 	ProgressDialog.show(this, "", "提交中");

        OkGo.<String>post(URL.CIRCLE_SEND_COMMENT).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("user_name",Constant.USERNAME)
                .params("content",send_content.getText().toString())
                .params("mid",list.get(sendItemPosition).getId())
                .params("reply_to_user_name",itemData.getUser_name())

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        CodeAndMsg bean = (CodeAndMsg)JsonUitl.stringToObject(response.body(),CodeAndMsg.class);
                        if(bean.getCode()==200){
                            List<CircleCommonBean> list2= list.get(sendItemPosition).getComments();
                            CircleCommonBean circleCommonBean = new CircleCommonBean();
                            circleCommonBean.setContent(send_content.getText().toString());
                            circleCommonBean.setNick_name(TempCache.UserBean.getData().getNick_name());
                            circleCommonBean.setReply_user_nickname(itemData.getNick_name());
                            list2.add(circleCommonBean);
                            list.get(sendItemPosition).setComments(list2);
                            list.get(sendItemPosition).setComment_num(list.get(sendItemPosition).getComment_num()+1);

                            adapter.setList(list);
                            mHeaderAndFooterWrapper.notifyDataSetChanged();
                        }
                        hideLayout();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        hideLayout();
                        progressDialog.dismiss();

                    }
                });
    }

    private void showListDialog()
    {
        final String[]items = { "相机","图库"};
        AlertDialog.Builder
                listDialog =
                new AlertDialog.Builder(this);
//        listDialog.setMessage("更换相册");
        listDialog.setCancelable(true);

        listDialog.setItems(items,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface
                                                dialog, int which)
                    {
                        if(which==0){
                            selectPicFromCamera(REQUEST_CODE_CAMERA);
                        }else{
                            selectPicFromLocal();
                        }

                    }
                });
        listDialog.show();
    }

    protected void selectPicFromCamera(int type) {
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
                startActivityForResult(intent, type);
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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.big_pic:
                showListDialog();
                break;
            case R.id.commite:
                showDialog();
//                startActivity(new Intent(FriendCircleActivity.this,CircleCommiteActivity.class));
                break;
            case R.id.sendBtn:
                if(!isSendItem){
                    sendcomment0();
                }else{
                    sendcommentByItem();
                }
                break;
                case R.id.img_back:
                    finish();
                    break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == REQUEST_CODE_CAMERA2){
                localMediaList.add(cameraFile.getAbsolutePath());
                startActivity(new Intent(FriendCircleActivity.this,CircleCommiteActivity.class)
                        .putStringArrayListExtra("list",localMediaList)
                );

            }
            if (requestCode == REQUEST_CODE_CAMERA ) { // capture new image

                if (cameraFile != null && cameraFile.exists()){
//                    createTempImageFile();
                    RequestOptions options = new RequestOptions();
                    options.placeholder(R.drawable.logo);
                    options.diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(this).load(cameraFile.getAbsolutePath()).apply(options).into(big_pic);
                    compass(cameraFile.getAbsolutePath());

                }
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        try {
                            String string= sendPicByUri(selectedImage);
                            if(!StringUtils.isEmpty(string)){
                                cameraFile = new File(string);
//                                createTempImageFile();
                                RequestOptions options = new RequestOptions();
                                options.placeholder(R.drawable.logo);
                                options.diskCacheStrategy(DiskCacheStrategy.ALL);
                                Glide.with(this).load(string).into(big_pic);
                                compass(cameraFile.getAbsolutePath());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if(requestCode == PhotoPicker.REQUEST_CODE ){
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                localMediaList.addAll(photos);
                startActivity(new Intent(FriendCircleActivity.this,CircleCommiteActivity.class)
                .putStringArrayListExtra("list",localMediaList)
                );

            }


        }

    }


    private void uploadBigPic(File f){
        OkGo.<String>post(URL.FENGMIAN).tag(this)
                .headers("Authorization", fengmianAuth)
                .params("user_name",Constant.USERNAME)
                .params("moment_cover",f)

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        dismissDialog();
                        UploadCoverBean bean = (UploadCoverBean)JsonUitl.stringToObject(response.body(),UploadCoverBean.class);
                        if(bean.getCode()==200){
                            Toast.makeText(FriendCircleActivity.this,"封面上传成功",Toast.LENGTH_SHORT).show();
                        }

                        TempCache.UserBean.getData().setMoment_cover(bean.getData().getMoment_cover());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        dismissDialog();
                    }
                });
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
