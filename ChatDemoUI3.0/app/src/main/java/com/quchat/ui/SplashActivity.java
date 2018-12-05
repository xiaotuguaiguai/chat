package com.quchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.DemoHelper;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.conference.ConferenceActivity;
import com.hyphenate.util.EasyUtils;
import com.quchat.domain.ContactListBean;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.SPUtil;
import com.quchat.utils.TempCache;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {

	private static final int sleepTime = 2000;
	private ImageView wx_btn,phone_btn;
	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_choose_login);
		super.onCreate(arg0);

		DemoHelper.getInstance().initHandler(this.getMainLooper());

		RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		TextView wx_btn = (TextView) findViewById(R.id.wx_btn);
		TextView phone_btn = (TextView) findViewById(R.id.phone_btn);

		wx_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(SplashActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
			}
		});

		phone_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			startActivity(new Intent(SplashActivity.this,LoginActivity.class));
			}
		});
//		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
		init();
	}
	private void getFriendList(){
		OkGo.<String>get(URL.GETFRIENDLIST).tag(this)
				.headers("Authorization", "Bearer " + Constant.TOKEN)
				.params("username",Constant.USERNAME)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(Response<String> response) {
						Log.e("ss",response.body().toString());
						ContactListBean userInfoBean=(ContactListBean) JsonUitl.stringToObject(response.body().toString(), ContactListBean.class);
						if(userInfoBean.getCode()==200){
							TempCache.setFriendList(userInfoBean.getData());
							initHX();
						}
					}

					@Override
					public void onError(Response<String> response) {
						super.onError(response);
						Toast.makeText(SplashActivity.this,"网络请求失败",Toast.LENGTH_SHORT).show();
						initHX();
//						Log.e("sserror",response.body());
					}
				});
	}
	private void init(){

		if(SPUtil.get(this,Constant.TOKENKEY,"")!=""){
			Constant.TOKEN=(String)SPUtil.get(this,Constant.TOKENKEY,"");
			Constant.USERNAME=(String)SPUtil.get(this,Constant.USERNAMEKEY,"");
			EaseConstant.TOKEN=Constant.TOKEN;
			EaseConstant.USER_ID = Constant.USERNAME;
			getFriendList();

			Log.e("ss","");
		}else{
//			startActivity(new Intent(this,LoginActivity.class));
		}
	}
	private void initHX(){
		new Thread(new Runnable() {
			public void run() {
				if (DemoHelper.getInstance().isLoggedIn()) {
					// auto login mode, make sure all group and conversation is loaed before enter the main screen
					long start = System.currentTimeMillis();
					EMClient.getInstance().chatManager().loadAllConversations();
					EMClient.getInstance().groupManager().loadAllGroups();
					long costTime = System.currentTimeMillis() - start;
					//wait
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String topActivityName = EasyUtils.getTopActivityName(EMClient.getInstance().getContext());
					if (topActivityName != null && (topActivityName.equals(VideoCallActivity.class.getName()) || topActivityName.equals(VoiceCallActivity.class.getName()) || topActivityName.equals(ConferenceActivity.class.getName()))) {
						// nop
						// avoid main screen overlap Calling Activity
					} else {
						//enter main screen
						startActivity(new Intent(SplashActivity.this, MainActivity.class));
					}
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
//					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//					finish();
				}
			}
		}).start();
	}
	@Override
	protected void onStart() {
		super.onStart();


	}
	
	/**
	 * get sdk version
	 */
	private String getVersion() {
	    return EMClient.getInstance().VERSION;
	}
}
