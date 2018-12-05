package com.hyphenate.easeui.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.CodeAndMsg;
import com.hyphenate.easeui.utils.DecimalDigitsInputFilter;
import com.hyphenate.easeui.utils.JsonUitl;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class MyRechargeActivity extends EaseBaseActivity {
    private ReceiveBroadCast receiveBroadCast;
    private  ProgressDialog progressDialog;
    private ImageView img_back,choose_wx_type;
    private TextView next;
    private String incomMoney;
    private boolean isChoose;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        incomMoney=getIntent().getStringExtra("money");
        setContentView(R.layout.activity_my_recharge);
        img_back=findViewById(R.id.img_back);
        choose_wx_type=findViewById(R.id.choose_wx_type);
        next=findViewById(R.id.next);
        choose_wx_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChoose){
                    isChoose=false;
                    choose_wx_type.setImageResource(R.drawable.icon_wx_unchoose);
                }else{
                    isChoose=true;
                    choose_wx_type.setImageResource(R.drawable.icon_wx_choose);
                }
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isChoose){
                    Toast.makeText(MyRechargeActivity.this,"请选择支付方式",Toast.LENGTH_SHORT).show();

                    return;
                }
                if(TextUtils.isEmpty(incomMoney)){
                    Toast.makeText(MyRechargeActivity.this,"请输入充值金额",Toast.LENGTH_SHORT).show();

                    return;
                }
                rechard();

            }
        });
    }

    private void rechard() {
         progressDialog = ProgressDialog.show(MyRechargeActivity.this,"","支付中");
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
        dismissDialog();
        unregisterReceiver(receiveBroadCast);
    }

    private void dismissDialog(){
        if(null!=progressDialog){
            progressDialog.dismiss();
        }
    }

    class ReceiveBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String code=intent.getStringExtra("code");
            Log.e("ss","onReceive code="+code);
            if(TextUtils.isEmpty(code)){// 支付返回
                if(intent.getStringExtra("payStatus").equals("0")){
                    dismissDialog();

                    Toast.makeText(MyRechargeActivity.this,"支付成功",Toast.LENGTH_SHORT).show();

                    finish();
                }else{
                    dismissDialog();
                    Toast.makeText(MyRechargeActivity.this,"支付失败",Toast.LENGTH_SHORT).show();
                }
                Log.e("ss","");
                createOrder();
            }else
                getAccessToken(code);

        }
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
                        Log.e("ss","getAccessTokenonException1="+response.body());
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String access_token = jsonObject.getString("access_token");
                            String openid = jsonObject.getString("openid");

                            bindwx(openid);
                        } catch (Exception e) {
                            Log.e("ss","getAccessTokenonException="+e.getMessage());

                            e.printStackTrace();
                            dismissDialog();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("ss","getAccessTokenonError="+response.body());
                        dismissDialog();
                    }
                });
    }

    private void bindwx(String openid){

        OkGo.<String>post("http://114.116.66.158:8081/api/bind_wechat").tag(this)
                .headers("Authorization", "Bearer " + EaseConstant.TOKEN)
                .params("username",EaseConstant.USER_ID)
                .params("open_id",openid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss","bindwx="+response.body());
                        CodeAndMsg codeAndMsg = (CodeAndMsg) JsonUitl.stringToObject(response.body(),CodeAndMsg.class);
//                        if(codeAndMsg.getCode()==200){
                            createOrder();
//                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        dismissDialog();
                        super.onError(response);
                        Log.e("ss","bindwxonError="+response.body());
                    }
                });
    }


    private void createOrder(){
        OkGo.<String>post("http://114.116.66.158:8081/api/off_order").tag(this)
                .headers("Authorization", "Bearer " + EaseConstant.TOKEN)
                .params("user_name",EaseConstant.USER_ID)
                .params("money",incomMoney)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body());
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if(jsonObject.optInt("code")==200){
                                JSONObject jsonObject2 = jsonObject.getJSONObject("data");
                                PayReq req = new PayReq();
                                //req.appId = "wxf8b4f85f3a794e77";  // ≤‚ ‘”√appId
                                req.appId			= jsonObject2.getString("appid");
                                req.partnerId		= jsonObject2.getString("partnerid");
                                req.prepayId		= jsonObject2.getString("prepayid");
                                req.nonceStr		= jsonObject2.getString("noncestr");
                                req.timeStamp		= jsonObject2.getInt("timestamp")+"";
                                req.packageValue	= jsonObject2.getString("package");
                                req.sign			= jsonObject2.getString("sign");
                                req.extData			= "app data"; // optional
                                EaseConstant.WX_API.sendReq(req);
                            }
                        } catch (JSONException e) {
                            dismissDialog();
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Response<String> response) {
                        dismissDialog();
                        super.onError(response);

                    }
                });
    }
}
