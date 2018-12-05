package com.quchat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.domain.UserInfoBean;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.TempCache;
import com.quchat.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;
import com.hyphenate.chat.EMClient;

public class PersonInfoActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout bg_layout,desc_layout;
    private ImageView img_back,more,image1,image2,image3,image4,image5,bg_image;
    private CircleImageView face;
    private TextView name_text,id_text,address_text,sign_text,source_text,msg_btn,video_btn;
    private  String username;
    private List<ImageView> imageViewList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_person_info);
        Intent intent = getIntent();
         username = intent.getStringExtra("username");
        initView();
        getInfo();
    }

    private void getCircle(){

    }

    private void getInfo(){
        final ProgressDialog dialog = ProgressDialog.show(this,"","加载中");
        OkGo.<String>get(URL.GETINFO).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",username)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        UserInfoBean   userInfoBean=(UserInfoBean) JsonUitl.stringToObject(response.body().toString(), UserInfoBean.class);
                        if(userInfoBean.getCode()==200){
                            name_text.setText(userInfoBean.getData().getNick_name());
                            id_text.setText("趣信号："+username);
                            if(TextUtils.isEmpty(userInfoBean.getData().getAddress_name())){
                                address_text.setText("");
                            }else
                              address_text.setText(userInfoBean.getData().getAddress_name());
                            if(TextUtils.isEmpty(userInfoBean.getData().getSignature())) {
                                sign_text.setText("");
                            }sign_text.setText(userInfoBean.getData().getSignature());

                            source_text.setText("");

                            if(!TextUtils.isEmpty(userInfoBean.getData().getHead_url())){
                                RequestOptions options = new RequestOptions();
                                options.placeholder(R.drawable.logo);
                                Glide.with(PersonInfoActivity.this).load(userInfoBean.getData().getHead_url()).apply(options).into(face);
                            }
                            if(TextUtils.isEmpty(userInfoBean.getData().getMoment_cover())){
                                RequestOptions options = new RequestOptions();
                                options.placeholder(R.drawable.bg_cover);
                                Glide.with(PersonInfoActivity.this).load(userInfoBean.getData().getMoment_cover()).apply(options).into(bg_image);

                            }
                        }
                        TempCache.setUserBean(userInfoBean);
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        dialog.dismiss();
                    }
                });
    }

    private void setView(){

    }

    private void initView(){
        bg_layout = findViewById(R.id.bg_layout);
        desc_layout = findViewById(R.id.desc_layout);
        img_back = findViewById(R.id.img_back);
        more = findViewById(R.id.more);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        face= findViewById(R.id.face);

        name_text = findViewById(R.id.name_text);
        id_text = findViewById(R.id.id_text);
        address_text = findViewById(R.id.address_text);
        sign_text = findViewById(R.id.sign_text);
        source_text = findViewById(R.id.source_text);

        msg_btn = findViewById(R.id.msg_btn);
        video_btn = findViewById(R.id.video_btn);
        bg_image = findViewById(R.id.bg_image);

        msg_btn.setOnClickListener(this);
        video_btn.setOnClickListener(this);
        img_back.setOnClickListener(this);
        more.setOnClickListener(this);
        desc_layout.setOnClickListener(this);

        imageViewList.add(image1);     imageViewList.add(image2);     imageViewList.add(image3);     imageViewList.add(image4);     imageViewList.add(image5);
    }
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", username)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);

        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.desc_layout:
                Toast.makeText(this,"暂未开通",Toast.LENGTH_SHORT).show();

                break;
            case R.id.more:
                startActivity(new Intent(PersonInfoActivity.this,SettingInfoActivity.class).putExtra("userName",username));
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.msg_btn:
                startActivity(new Intent(this,ChatActivity.class).putExtra("userId",username));
                break;
            case R.id.video_btn:
                startVideoCall();
                break;
        }
    }
}
