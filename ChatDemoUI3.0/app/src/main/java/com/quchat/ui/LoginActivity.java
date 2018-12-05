/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quchat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.DemoApplication;
import com.quchat.DemoHelper;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.db.DemoDBManager;
import com.quchat.domain.AuthBean;
import com.quchat.domain.ContactListBean;
import com.quchat.domain.UserInfoBean;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.MD5;
import com.quchat.utils.SPUtil;
import com.quchat.utils.TempCache;

/**
 * Login screen
 * 
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

	private  TextView loginText,regiteText;
	private Fragment loginFragment,registeFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_login_choose);
		loginText=findViewById(R.id.loginText);
		regiteText=findViewById(R.id.regiteText);
		loginText.setOnClickListener(this);
		regiteText.setOnClickListener(this);
//		loginFragment = new LoginFragment();
//		registeFragment = new RegisteFragment();
		setTabSelection(0);
	}

	public void setTabSelection(int index) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
//		hideFragment(transaction);
		switch (index) {
			case 0:
				if (loginText != null) {
					loginText.setEnabled(false);
					regiteText.setEnabled(true);
					regiteText.setTextColor(getResources().getColor(R.color._626262));

					loginText.setTextColor(getResources().getColor(R.color._2C2C2C));
					TextPaint paint = loginText.getPaint();
					paint.setFakeBoldText(true);
					TextPaint paint2 = regiteText.getPaint();
					paint2.setFakeBoldText(false);
				}
				if(null!=registeFragment){
					transaction.hide(registeFragment);
				}

				if (loginFragment == null) {
					loginFragment = new LoginFragment();
					transaction.add(R.id.fl_home_container, loginFragment, "login")
							.commitAllowingStateLoss();
				} else {
					transaction.show(loginFragment).commitAllowingStateLoss();
				}
				break;
			case 1:

				if (regiteText != null) {
					loginText.setEnabled(true);
					regiteText.setEnabled(false);
					loginText.setTextColor(getResources().getColor(R.color._626262));
					regiteText.setTextColor(getResources().getColor(R.color._2C2C2C));

					TextPaint paint = regiteText.getPaint();
					paint.setFakeBoldText(true);
					TextPaint paint2 = loginText.getPaint();
					paint2.setFakeBoldText(false);
				}
				transaction.hide(loginFragment);

				if (registeFragment == null) {
					registeFragment = new RegisteFragment();
					transaction.add(R.id.fl_home_container, registeFragment, "registe")
							.commitAllowingStateLoss();
				} else {
					transaction.show(registeFragment).commitAllowingStateLoss();
				}
				break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.loginText:
				setTabSelection(0);
				break;
			case R.id.regiteText:
				setTabSelection(1);
				break;
		}
	}
}
