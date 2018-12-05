package com.quchat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.quchat.ui.UserInfoActivity;
import com.quchat.domain.UserInfoBean;
import com.quchat.domain.UserInfoBean;
import com.quchat.Constant;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.TempCache;
import com.quchat.URL;
import com.quchat.widget.CircleImageView;
import com.quchat.widget.CustomRow;
import com.quchat.widget.XCRoundRectImageView;

public class MeFragment extends Fragment implements View.OnClickListener {


    private View view;
    private CircleImageView face;
    private CustomRow setting,qrcode,picture,wallet,about,save,shop;
    private TextView nickName,userId;
    private UserInfoBean userInfoBean;
    private RelativeLayout me_layout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_me, container, false);
        face=view.findViewById(R.id.face);
        nickName=view.findViewById(R.id.nickName);
        me_layout=view.findViewById(R.id.me_layout);
        setting=view.findViewById(R.id.setting);
        qrcode=view.findViewById(R.id.qrcode);
        wallet=view.findViewById(R.id.wallet);
        userId=view.findViewById(R.id.userId);
        shop=view.findViewById(R.id.shop);
        picture=view.findViewById(R.id.picture);
        about=view.findViewById(R.id.about);
        save=view.findViewById(R.id.save);
        face.setOnClickListener(this);
        shop.setOnClickListener(this);
        setting.setOnClickListener(this);
        wallet.setOnClickListener(this);
        qrcode.setOnClickListener(this);
        picture.setOnClickListener(this);
        about.setOnClickListener(this);
        save.setOnClickListener(this);
        me_layout.setOnClickListener(this);

        userId.setText("趣信号："+Constant.USERNAME);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getInfo();
    }

    private void getInfo(){
       final  ProgressDialog dialog = ProgressDialog.show(getActivity(),"","加载中");
        OkGo.<String>get(URL.GETINFO).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",Constant.USERNAME)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        userInfoBean=(UserInfoBean) JsonUitl.stringToObject(response.body().toString(), UserInfoBean.class);
                        if(userInfoBean.getCode()==200){
                            nickName.setText(userInfoBean.getData().getNick_name());
                            userId.setText("趣信号："+Constant.USERNAME);
                            RequestOptions     options = new RequestOptions();
                            options.placeholder(R.drawable.logo);
                            Glide.with(getActivity()).load(userInfoBean.getData().getHead_url()).apply(options).into(face);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode==100){
                String name = data.getStringExtra("nickname");
                String url = data.getStringExtra("pic");

                if(!TextUtils.isEmpty(url)){
                    Glide.with(this).load(url).into(face);
                }
                if(!TextUtils.isEmpty(name)){
                    nickName.setText(name);
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.qrcode:
                startActivity(new Intent(getActivity(),MyQrCodeActivity.class)
                        .putExtra("nick_name",userInfoBean.getData().getNick_name())
                        .putExtra("headUrl",userInfoBean.getData().getHead_url())
                );
                break;
            case R.id.setting:
                startActivity(new Intent(getActivity(),SettingDetailActivity.class));

                break;
            case R.id.me_layout:
                startActivityForResult(new Intent(getActivity(), UserInfoActivity.class)
                        .putExtra("headUrl",userInfoBean.getData().getHead_url())
                        .putExtra("sex",userInfoBean.getData().getSex())
                        .putExtra("nick_name",userInfoBean.getData().getNick_name()),100
                );

                break;
            case R.id.picture:
                startActivity(new Intent(getActivity(),FriendCircleActivity.class)
                        .putExtra("from",1)
                );
                break;
            case R.id.wallet:
                startActivity(new Intent(getActivity(),MyWalletActivity.class)

                );
                break;
            case R.id.about:
                Toast.makeText(getActivity(),"隐私暂未开通",Toast.LENGTH_SHORT).show();
                break;
            case R.id.save:
                Toast.makeText(getActivity(),"收藏暂未开通",Toast.LENGTH_SHORT).show();
                break;
            case R.id.shop:
                Toast.makeText(getActivity(),"店铺暂未开通",Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
