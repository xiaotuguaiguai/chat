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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMClientListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConferenceManager;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.adapter.EMAChatManager;
import com.hyphenate.chat.adapter.EMAConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.utils.EaseSharedUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.DemoHelper;
import com.quchat.HMSPushHelper;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.db.InviteMessgeDao;
import com.quchat.db.UserDao;
import com.quchat.domain.UserInfoBean;
import com.quchat.runtimepermissions.PermissionsManager;
import com.quchat.runtimepermissions.PermissionsResultAction;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.SPUtil;
import com.quchat.utils.TempCache;
import com.yzq.zxinglibrary.android.CaptureActivity;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity {

	protected static final String TAG = "MainActivity";
	// textview for unread message count
	private TextView unreadLabel,goneText;
	// textview for unread event message
	private TextView unreadAddressLable;

	private LinearLayout[] mTabs;
//	private ContactListFragment contactListFragment;
	private FindFragment findFragment;
	private ContactListFragment contactListFragment;

	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	// user logged into another device
	public boolean isConflict = false;
	// user account was removed
	private boolean isCurrentAccountRemoved = false;
	

	/**
	 * check if current user account was remove
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}


	private ImageView tab1_img,tab2_img,tab3_img,tab4_img,menu;
	private TextView tab1_text,tab2_text,tab3_text,tab4_text,fragent_title;
    private int REQUEST_CODE_SCAN = 111;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		    String packageName = getPackageName();
		    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
				try {
					//some device doesn't has activity to handle this intent
					//so add try catch
					Intent intent = new Intent();
					intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
					intent.setData(Uri.parse("package:" + packageName));
					startActivity(intent);
				} catch (Exception e) {
				}
			}
		}
		init();
		//make sure activity will not in background if user is logged into another device or removed
		if (getIntent() != null &&
				(getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) ||
						getIntent().getBooleanExtra(Constant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD, false) ||
						getIntent().getBooleanExtra(Constant.ACCOUNT_KICKED_BY_OTHER_DEVICE, false))) {
			DemoHelper.getInstance().logout(false,null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (getIntent() != null && getIntent().getBooleanExtra("isConflict", false)) {
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		setContentView(R.layout.em_activity_main);
		// runtime permission for android 6.0, just require all permissions here for simple
		requestPermissions();

		initView();

		showExceptionDialogFromIntent(getIntent());

		inviteMessgeDao = new InviteMessgeDao(this);
		UserDao userDao = new UserDao(this);
		conversationListFragment = new ConversationListFragment();
		contactListFragment= new ContactListFragment();
		findFragment = new FindFragment();
		MeFragment settingFragment = new MeFragment();
		fragments = new Fragment[] { conversationListFragment, contactListFragment,findFragment, settingFragment};

		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
				.add(R.id.fragment_container, contactListFragment).hide(contactListFragment)
				.add(R.id.fragment_container, findFragment).hide(findFragment).show(conversationListFragment)
				.commit();

		//register broadcast receiver to receive the change of group from DemoHelper
		registerBroadcastReceiver();


		EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
		EMClient.getInstance().addClientListener(clientListener);
		EMClient.getInstance().addMultiDeviceListener(new MyMultiDeviceListener());
		//debug purpose only
        registerInternalDebugReceiver();

		// 获取华为 HMS 推送 token
		HMSPushHelper.getInstance().getHMSToken(this);
	}

	EMClientListener clientListener = new EMClientListener() {
		@Override
		public void onMigrate2x(boolean success) {
//			Toast.makeText(MainActivity.this, "onUpgradeFrom 2.x to 3.x " + (success ? "success" : "fail"), Toast.LENGTH_LONG).show();
			if (success) {
				refreshUIWithMessage();
			}
		}
	};

	@TargetApi(23)
	private void requestPermissions() {
		PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
			@Override
			public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDenied(String permission) {
				//Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * init views
	 */
	private void initView() {
		fragent_title= (TextView) findViewById(R.id.fragent_title);
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		goneText= (TextView) findViewById(R.id.goneText);
		unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
		mTabs = new LinearLayout[4];
		mTabs[0] = (LinearLayout) findViewById(R.id.btn_conversation);
		mTabs[1] = (LinearLayout) findViewById(R.id.btn_address_list);
		mTabs[2] = (LinearLayout) findViewById(R.id.btn_find);
		mTabs[3] = (LinearLayout) findViewById(R.id.btn_setting);
		// select first tab
		mTabs[0].setSelected(true);
		tab1_img=findViewById(R.id.tab1_img);
		tab2_img=findViewById(R.id.tab2_img);
		tab3_img=findViewById(R.id.tab3_img);
		tab4_img=findViewById(R.id.tab4_img);
		tab1_text=findViewById(R.id.tab1_text);
		tab2_text=findViewById(R.id.tab2_text);
		tab3_text=findViewById(R.id.tab3_text);
		tab4_text=findViewById(R.id.tab4_text);
		menu = (ImageView) findViewById(R.id.menu);
		ImageView search = (ImageView) findViewById(R.id.search);
		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this,AddContactActivity.class));
			}
		});
		menu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(index==0){
					showPopwindow();
				}else if(index==1){
					startActivity(new Intent(MainActivity.this,AddContactActivity.class));
				}else if(index==2){
					showPopwindow();
				}else if(index==3){
					startActivity(new Intent(MainActivity.this,MyQrCodeActivity.class)
							.putExtra("nick_name",TempCache.UserBean.getData().getNick_name())
							.putExtra("headUrl",TempCache.UserBean.getData().getHead_url())
					);
				}

			}
		});
	}


	private void showPopwindow(){

		// 用于PopupWindow的View
		View contentView=LayoutInflater.from(this).inflate(R.layout.view_popupwindow, null, false);
		// 创建PopupWindow对象，其中：
		// 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
		// 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
		LinearLayout groupchat = contentView.findViewById(R.id.groupchat);
		LinearLayout addfriend = contentView.findViewById(R.id.addfriend);
		LinearLayout scan = contentView.findViewById(R.id.scan);
		LinearLayout help = contentView.findViewById(R.id.help);

		final PopupWindow window=new PopupWindow(contentView, 400, 500, true);
		// 设置PopupWindow的背景
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 设置PopupWindow是否能响应外部点击事件
		window.setOutsideTouchable(true);
		// 设置PopupWindow是否能响应点击事件
		window.setTouchable(true);
		// 显示PopupWindow，其中：
		// 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
		window.showAsDropDown(goneText, 0, 0);
		groupchat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				window.dismiss();
//				startActivity(new Intent(MainActivity.this, GroupsActivity.class));
			startActivityForResult(new Intent(MainActivity.this,GroupPickContactsActivity.class),300);
			}
		});
		addfriend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				window.dismiss();
				startActivity(new Intent(MainActivity.this,AddContactActivity.class));

			}
		});
		scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				window.dismiss();
				Intent intent2 = new Intent(MainActivity.this, CaptureActivity.class);
				startActivityForResult(intent2, REQUEST_CODE_SCAN);
			}
		});
		help.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(MainActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
				window.dismiss();
			}
		});



	}


	/**
	 * on tab clicked
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_conversation:
			index = 0;

			break;
		case R.id.btn_address_list:
			index = 1;
			break;
		case R.id.btn_find:
			index = 2;
			break;
		case R.id.btn_setting:
			index = 3;
			break;
	}
		setView();
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// set current tab selected
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	private void setView(){
		setTitleImage();
		if(index==0){
			tab1_img.setImageResource(R.drawable.em_conversation_selected);
			tab1_text.setTextColor(getResources().getColor(R.color.top_bar_normal_bg));
			tab2_img.setImageResource(R.drawable.em_contact_list_normal);
			tab2_text.setTextColor(getResources().getColor(R.color.gray_normal));
			tab3_img.setImageResource(R.drawable.em_find_normal);
			tab3_text.setTextColor(getResources().getColor(R.color.gray_normal));
			tab4_img.setImageResource(R.drawable.em_settings_normal);
			tab4_text.setTextColor(getResources().getColor(R.color.gray_normal));
		}else if(index==1){
			tab1_img.setImageResource(R.drawable.em_conversation_normal);
			tab1_text.setTextColor(getResources().getColor(R.color.gray_normal));
			tab2_img.setImageResource(R.drawable.em_contact_list_selected);
			tab2_text.setTextColor(getResources().getColor(R.color.top_bar_normal_bg));
			tab3_img.setImageResource(R.drawable.em_find_normal);
			tab3_text.setTextColor(getResources().getColor(R.color.gray_normal));
			tab4_img.setImageResource(R.drawable.em_settings_normal);
			tab4_text.setTextColor(getResources().getColor(R.color.gray_normal));
		}else if(index==2){
			tab1_img.setImageResource(R.drawable.em_conversation_normal);
			tab1_text.setTextColor(getResources().getColor(R.color.gray_normal));
			tab2_img.setImageResource(R.drawable.em_contact_list_normal);
			tab2_text.setTextColor(getResources().getColor(R.color.gray_normal));
			tab3_img.setImageResource(R.drawable.em_find_pressed);
			tab3_text.setTextColor(getResources().getColor(R.color.top_bar_normal_bg));
			tab4_img.setImageResource(R.drawable.em_settings_normal);
			tab4_text.setTextColor(getResources().getColor(R.color.gray_normal));
		}else if(index==3){
			tab1_img.setImageResource(R.drawable.em_conversation_normal);
			tab1_text.setTextColor(getResources().getColor(R.color.gray_normal));
			tab2_img.setImageResource(R.drawable.em_contact_list_normal);
			tab2_text.setTextColor(getResources().getColor(R.color.gray_normal));
			tab3_img.setImageResource(R.drawable.em_find_normal);
			tab3_text.setTextColor(getResources().getColor(R.color.gray_normal));
			tab4_img.setImageResource(R.drawable.em_settings_selected);
			tab4_text.setTextColor(getResources().getColor(R.color.top_bar_normal_bg));
		}

	}

	private void setTitleImage(){
		if(index==0) {
			fragent_title.setText("趣信");
			menu.setImageResource(R.drawable.em_add);
		}else if(index==1){
			fragent_title.setText("通讯录");
			menu.setImageResource(R.drawable.icon_green_add);
		}else if(index==2){
			fragent_title.setText("发现");
			menu.setImageResource(R.drawable.em_add);
		}else if(index==3){
			fragent_title.setText("我");
			menu.setImageResource(R.drawable.icon_qr_blue);
		}
	}

	EMMessageListener messageListener = new EMMessageListener() {
		
		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			// notify new message
			for (EMMessage message: messages) {
				DemoHelper.getInstance().getNotifier().vibrateAndPlayTone(message);
			}
			refreshUIWithMessage();
		}
		
		@Override
		public void onCmdMessageReceived(List<EMMessage> messages) {
			refreshUIWithMessage();
		}
		
		@Override
		public void onMessageRead(List<EMMessage> messages) {
		}
		
		@Override
		public void onMessageDelivered(List<EMMessage> message) {
		}

		@Override
		public void onMessageRecalled(List<EMMessage> messages) {
			refreshUIWithMessage();
		}

		@Override
		public void onMessageChanged(EMMessage message, Object change) {}
	};

	private void refreshUIWithMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				// refresh unread count
				updateUnreadLabel();
				if (currentTabIndex == 0) {
					// refresh conversation list
					if (conversationListFragment != null) {
						conversationListFragment.refresh();
					}
				}
			}
		});
	}

	@Override
	public void back(View view) {
		super.back(view);
	}
	
	private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
                updateUnreadAddressLable();
                if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                } else if (currentTabIndex == 1) {
//                    if(contactListFragment != null) {
//                        contactListFragment.refresh();
//                    }
                }
                String action = intent.getAction();
                if(action.equals(Constant.ACTION_GROUP_CHANAGED)){
                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
			}
	         };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }
	
	public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {}
        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
					if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
							username.equals(ChatActivity.activityInstance.toChatUsername)) {
					    String st10 = getResources().getString(R.string.have_you_removed);
					    Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
					    .show();
					    ChatActivity.activityInstance.finish();
					}
                }
            });
	        updateUnreadAddressLable();
        }
        @Override
        public void onContactInvited(String username, String reason) {}
        @Override
        public void onFriendRequestAccepted(String username) {
        	Log.e("ss","onFriendRequestAccepted="+username);
		}
        @Override
        public void onFriendRequestDeclined(String username) {}
	}

	public class MyMultiDeviceListener implements EMMultiDeviceListener {

		@Override
		public void onContactEvent(int event, String target, String ext) {

		}

		@Override
		public void onGroupEvent(int event, String target, final List<String> username) {
			switch (event) {
			case EMMultiDeviceListener.GROUP_LEAVE:
				ChatActivity.activityInstance.finish();
				break;
			default:
				break;
			}
		}
	}
	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();		
		
		if (exceptionBuilder != null) {
		    exceptionBuilder.create().dismiss();
		    exceptionBuilder = null;
		    isExceptionDialogShow = false;
		}
		unregisterBroadcastReceiver();

		try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }
		
	}

	/**
	 * update unread message count
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}
	public  void updateUnreadLabel2() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count-1));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}
	/**
	 * update the total unread count 
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();

				if (count > 0) {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}

	/**
	 * get unread event notification count, including application, accepted, etc
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
		return unreadAddressCountTotal;
	}

	/**
	 * get unread message count
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
//		return EMClient.getInstance().chatManager().getUnreadMessageCount();
		return unread();
	}


	private int  unread(){
		EMAChatManager emaObject  =(EMAChatManager)getSpecifiedFieldObject(EMClient.getInstance().chatManager(),
				"emaObject");
		List<EMAConversation> var1= emaObject.getConversations();
		int unread=0;

		Iterator var3 =var1.iterator();
		while (var3.hasNext()){
			EMAConversation  conversation=  (EMAConversation)var3.next();
			if(conversation._getType() != EMAConversation.EMAConversationType.CHATROOM &&
					EaseSharedUtils.isEnableMsgRing(this,  EMClient.getInstance().getCurrentUser(),conversation.conversationId())){
				unread += conversation.unreadMessagesCount();
			}
		}
		return unread;
	}
	public static Object getSpecifiedFieldObject(Object obj, String fieldName) {
		Class<?> clazz = obj.getClass();
		Object object = null;
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			object = field.get(obj);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;

	}

	private InviteMessgeDao inviteMessgeDao;

	@Override
	protected void onResume() {
		super.onResume();
		
		if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
		}

		// unregister this event listener when this activity enters the
		// background
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.pushActivity(this);

		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}

	@Override
	protected void onPause() {
		super.onPause();

		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		EMClient.getInstance().removeClientListener(clientListener);
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.popActivity(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private android.app.AlertDialog.Builder exceptionBuilder;
	private boolean isExceptionDialogShow =  false;
    private BroadcastReceiver internalDebugReceiver;
    private ConversationListFragment conversationListFragment;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;

    private int getExceptionMessageId(String exceptionType) {
         if(exceptionType.equals(Constant.ACCOUNT_CONFLICT)) {
             return R.string.connect_conflict;
         } else if (exceptionType.equals(Constant.ACCOUNT_REMOVED)) {
             return R.string.em_user_remove;
         } else if (exceptionType.equals(Constant.ACCOUNT_FORBIDDEN)) {
             return R.string.user_forbidden;
         }
         return R.string.Network_error;
    }
	/**
	 * show the dialog when user met some exception: such as login on another device, user removed or user forbidden
	 */
	private void showExceptionDialog(String exceptionType) {
	    isExceptionDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (exceptionBuilder == null)
				    exceptionBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				    exceptionBuilder.setTitle(st);
				    exceptionBuilder.setMessage(getExceptionMessageId(exceptionType));	
				    exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						exceptionBuilder = null;
						isExceptionDialogShow = false;
						finish();
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
				exceptionBuilder.setCancelable(false);
				exceptionBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}
		}
	}

	private void showExceptionDialogFromIntent(Intent intent) {
	    EMLog.e(TAG, "showExceptionDialogFromIntent");
	    if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)) {
            showExceptionDialog(Constant.ACCOUNT_CONFLICT);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false)) {
            showExceptionDialog(Constant.ACCOUNT_REMOVED);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_FORBIDDEN, false)) {
            showExceptionDialog(Constant.ACCOUNT_FORBIDDEN);
        } else if (intent.getBooleanExtra(Constant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD, false) ||
                intent.getBooleanExtra(Constant.ACCOUNT_KICKED_BY_OTHER_DEVICE, false)) {
            this.finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
	}
	private void init(){

		if(SPUtil.get(this,Constant.TOKENKEY,"")!=""){
			Constant.TOKEN=(String)SPUtil.get(this,Constant.TOKENKEY,"");
			Constant.USERNAME=(String)SPUtil.get(this,Constant.USERNAMEKEY,"");
			getInfo();
//			getFriendList();
			Log.e("ss","");
		}else{
			startActivity(new Intent(this,LoginActivity.class));
		}
	}

	private void getInfo(){
		OkGo.<String>get(URL.GETINFO).tag(this)
				.headers("Authorization", "Bearer " + Constant.TOKEN)
				.params("username",Constant.USERNAME)
				.execute(new StringCallback() {
					@Override
					public void onSuccess(Response<String> response) {
						Log.e("ss",response.body().toString());
						UserInfoBean	userInfoBean=(UserInfoBean) JsonUitl.stringToObject(response.body().toString(), UserInfoBean.class);
						if(userInfoBean.getCode()==200){
							TempCache.setUserBean(userInfoBean);
						}


					}

					@Override
					public void onError(Response<String> response) {
						super.onError(response);
//						Log.e("sserror",response.body());
					}
				});
	}



	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		showExceptionDialogFromIntent(intent);
	}
	
	/**
	 * debug purpose only, you can ignore this
	 */
	private void registerInternalDebugReceiver() {
	    internalDebugReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHelper.getInstance().logout(false,new EMCallBack() {
                    
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                finish();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        });
                    }
                    
                    @Override
                    public void onProgress(int progress, String status) {}
                    
                    @Override
                    public void onError(int code, String message) {}
                });
            }
        };
        IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
        registerReceiver(internalDebugReceiver, filter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		if( resultCode == RESULT_OK){
			if (requestCode == REQUEST_CODE_SCAN ) {
				if (data != null) {

					String content = data.getStringExtra("codedContent");

//					Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
					Log.e("ss",content);
//                result.setText("扫描结果为：" + content);
				}
			}else if(requestCode==300){
				String content = data.getStringExtra("groupId");
				Log.e("ss",content);
				startActivity(new Intent(MainActivity.this,ChatActivity.class)
						.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP)
						.putExtra("userId",content));
			}

		}
        // 扫描二维码/条码回传

    }
	@Override 
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
	}
}
