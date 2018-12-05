package com.quchat.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.EaseConstant;

import com.hyphenate.easeui.domain.CodeAndMsg;
import com.hyphenate.easeui.utils.DecimalDigitsInputFilter;
import com.hyphenate.easeui.utils.JsonUitl;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.domain.UserInfoBean;
import com.quchat.utils.TempCache;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class MyWithDrawActivity extends BaseActivity {
    private ReceiveBroadCast receiveBroadCast;
    private TextView recharge,canCash,cashAll;
    private EditText money;
    private ImageView choose_wx_type;
    private boolean isChoose;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_my_withdraw);
        recharge=findViewById(R.id.recharge);
        money=findViewById(R.id.money);
        canCash=findViewById(R.id.canCash);
        cashAll=findViewById(R.id.cashAll);
        choose_wx_type=findViewById(R.id.choose_wx_type);
        ImageView img_back=findViewById(com.hyphenate.easeui.R.id.img_back);
        choose_wx_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChoose = !isChoose;
                if(isChoose){
                    choose_wx_type.setImageResource(R.drawable.icon_wx_choose);
                }else{
                    choose_wx_type.setImageResource(R.drawable.icon_wx_unchoose);
                }
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        money.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        cashAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null!=userInfoBean && null!=userInfoBean.getData()){
                    money.setText(userInfoBean.getData().getBalance()+"");
                }
            }
        });
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(money.getText().toString())){
                        withdraw();
                }
            }
        });
        getAccountInfo();
    }
    private void rechard() {
        if (EaseConstant.WX_API == null) {
            EaseConstant.WX_API = WXAPIFactory.createWXAPI(this, EaseConstant.WX_APPID, false);
            EaseConstant.WX_API.registerApp(EaseConstant.WX_APPID);
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        EaseConstant.WX_API.sendReq(req);
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("auth");
        registerReceiver(receiveBroadCast, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiveBroadCast);
    }


    class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getAccessToken(intent.getStringExtra("code"));

        }
    }
    UserInfoBean userInfoBean;
    private void getAccountInfo(){
        final ProgressDialog dialog = ProgressDialog.show(this,"","加载中");
        OkGo.<String>get(URL.GETINFO).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",Constant.USERNAME)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dialog.dismiss();
                        Log.e("ss",response.body().toString());
                        userInfoBean=(UserInfoBean) com.quchat.utils.JsonUitl.stringToObject(response.body().toString(), UserInfoBean.class);
                        if(userInfoBean.getCode()==200){
                            canCash.setText("可提现余额"+userInfoBean.getData().getBalance()+"");
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        dialog.dismiss();
                        Log.e("sserror",response.body());
                    }
                });
    }

    private void getAccessToken(String code){
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
         + EaseConstant.WX_APPID
            + "&secret="
            + EaseConstant.WX_APPSecret
            + "&code="
            + code
            + "&grant_type=authorization_code";

        OkGo.<String>get(url).tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String access_token = jsonObject.getString("access_token");
                            String openid = jsonObject.getString("openid");
                            bindwx(openid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private void bindwx(String openid){

        OkGo.<String>post("http://114.116.66.158:8081/api/bind_wechat").tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",EaseConstant.USER_ID)
                .params("open_id",openid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        CodeAndMsg codeAndMsg = (CodeAndMsg) JsonUitl.stringToObject(response.body(),CodeAndMsg.class);
                        if(codeAndMsg.getCode()==200){

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }


    private void withdraw(){
        OkGo.<String>post("http://114.116.66.158:8081/api/withdraw").tag(this)
                .headers("Authorization", "Bearer " + EaseConstant.TOKEN)
                .params("user_name",EaseConstant.USER_ID)
                .params("money",money.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body());
                        CodeAndMsg codeAndMsg = (CodeAndMsg) JsonUitl.stringToObject(response.body(),CodeAndMsg.class);
                        if(codeAndMsg.getCode()==200){
                            Toast.makeText(MyWithDrawActivity.this,"提现成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(MyWithDrawActivity.this,codeAndMsg.getMsg(),Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
}
