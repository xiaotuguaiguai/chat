package com.quchat.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.easeui.EaseConstant;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ss","onCreate");
        EaseConstant.WX_API.handleIntent(getIntent(), this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("ss","onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        EaseConstant.WX_API.handleIntent(intent, this);
        finish();
    }


    @Override
    public void onReq(BaseReq baseReq) {
        Log.e("ss","onReq");

    }


    @Override
    public void onResp(BaseResp baseResp) {
        Log.e("ss","onResp");
        String result = "";
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) baseResp).code;
                Log.e("ss",code);
//                SharedPreferences WxSp = getApplicationContext().getSharedPreferences(PrefParams.spName, Context.MODE_PRIVATE);
//                SharedPreferences.Editor WxSpEditor = WxSp.edit();
//                WxSpEditor.putString(PrefParams.CODE, code);
//                WxSpEditor.apply();
                Intent intent = new Intent();
                intent.putExtra("code",code+"");
                intent.setAction("auth");
                WXEntryActivity.this.sendBroadcast(intent);
                result = "发送成功";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                result = "发送返回";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();


                finish();
                break;
        }
        Log.e("ss",result);
    }
}

