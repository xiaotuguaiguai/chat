package com.quchat.wxapi;








import com.hyphenate.easeui.EaseConstant;
import com.quchat.R;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EaseConstant.WX_API.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		EaseConstant.WX_API.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Intent intent = new Intent();
		Log.e("ss","resp.type="+resp.getType());
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			intent.putExtra("payStatus","0");
		}else{
			intent.putExtra("payStatus","1");
		}
		intent.setAction("auth");
		WXPayEntryActivity.this.sendBroadcast(intent);
		finish();
	}
}