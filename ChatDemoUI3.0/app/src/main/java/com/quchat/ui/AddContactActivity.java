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
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.DemoHelper;

import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.domain.CodeAndMsg;
import com.quchat.domain.UserInfoBean;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.TempCache;

public class AddContactActivity extends com.quchat.ui.BaseActivity implements View.OnClickListener {
	private EditText editText;
	private RelativeLayout searchedUserLayout;
	private TextView nameText,myId;
	private Button searchBtn;
	private String toAddUsername;
	private ProgressDialog progressDialog;
	private ImageView myQr;
	private LinearLayout circle_layout,friend_layout,contact_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_add_contact);
		TextView mTextView = (TextView) findViewById(R.id.add_list_friends);
		
		editText = (EditText) findViewById(R.id.edit_note);
		myId= (TextView) findViewById(R.id.myId);
		myQr= (ImageView) findViewById(R.id.myQr);
		myId.setText("我的趣信号: "+Constant.USERNAME);

		String strAdd = getResources().getString(R.string.add_friend);
		mTextView.setText(strAdd);
		String strUserName = getResources().getString(R.string.user_name);
//		editText.setHint(strUserName);
		searchedUserLayout = (RelativeLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);

		myQr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(AddContactActivity.this,MyQrCodeActivity.class));
			}
		});
	}
	
	private void getFace(){
		OkGo.<String>get(URL.GETINFO).tag(this)
				.headers("Authorization", "Bearer " + Constant.TOKEN)
				.params("username",editText.getText().toString())
				.execute(new StringCallback() {
					@Override
					public void onSuccess(Response<String> response) {
						Log.e("ss",response.body().toString());
						UserInfoBean userInfoBean=(UserInfoBean) JsonUitl.stringToObject(response.body().toString(), UserInfoBean.class);
						if(userInfoBean.getCode()==200){
							TempCache.setUserBean(userInfoBean);
						}

					}

					@Override
					public void onError(Response<String> response) {
						super.onError(response);
						Toast.makeText(AddContactActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
					}
				});
	}
	/**
	 * search contact
	 * @param v
	 */
	public void searchContact(View v) {
		final String name = editText.getText().toString().toLowerCase();
		String saveText = searchBtn.getText().toString();
		
		if (getString(R.string.button_search).equals(saveText)) {
			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
				return;
			}
			getFace();
			// TODO you can search the user from your app server here.
			
			//show the userame and add button if user exist
			searchedUserLayout.setVisibility(View.VISIBLE);
			nameText.setText(toAddUsername);
			
		} 
	}	
	
	/**
	 *  add contact
	 * @param view
	 */
	public void addContact(View view){
		if(EMClient.getInstance().getCurrentUser().equals(nameText.getText().toString())){
			new EaseAlertDialog(this, R.string.not_add_myself).show();
			return;
		}
		
		if(DemoHelper.getInstance().getContactList().containsKey(nameText.getText().toString())){
		    //let the user know the contact already in your contact list
		    if(EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())){
		        new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
		        return;
		    }
			new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
			return;
		}
		
		progressDialog = new ProgressDialog(this);
		String stri = getResources().getString(R.string.Is_sending_a_request);
		progressDialog.setMessage(stri);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo use a hardcode reason here, you need let user to input if you like
					String s = getResources().getString(R.string.Add_a_friend);
					EMClient.getInstance().contactManager().addContact(toAddUsername, s);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(), s2 , Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}
	
	public void back(View v) {
		finish();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.circle_layout:
			case R.id.friend_layout:
			case R.id.contact_layout:
				Toast.makeText(AddContactActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
