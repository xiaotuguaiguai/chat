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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.easeui.utils.EaseUserUtils;

import com.quchat.Constant;
import com.quchat.DemoHelper;
import com.quchat.R;
import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseSidebar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupPickContactsActivity extends BaseActivity {
	/** if this is a new group */
	protected boolean isCreatingNewGroup;
	private PickContactAdapter contactAdapter;
	/** members already in the group */
	private List<String> existMembers;
	private boolean isFromCicle,mingpian,changeAdmin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_group_pick_contacts);
		isFromCicle=getIntent().getBooleanExtra("isFromCicle",false);
		mingpian=getIntent().getBooleanExtra("mingpian",false);
		changeAdmin=getIntent().getBooleanExtra("changeAdmin",false);
		String groupId = getIntent().getStringExtra("groupId");
		if (groupId == null) {// create new group
			isCreatingNewGroup = true;
		} else {
			// get members of the group
			EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
			existMembers = group.getMembers();
			if(!changeAdmin){
				existMembers.add(group.getOwner());
			}
			existMembers.addAll(group.getAdminList());
		}
		if(existMembers == null)
			existMembers = new ArrayList<String>();


		// get contact list
		final List<EaseUser> alluserList = new ArrayList<EaseUser>();
        if(!changeAdmin){
            for (EaseUser user : DemoHelper.getInstance().getContactList().values()) {
                if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getUsername().equals(Constant.GROUP_USERNAME) & !user.getUsername().equals(Constant.CHAT_ROOM) & !user.getUsername().equals(Constant.CHAT_ROBOT))
                    alluserList.add(user);
            }
        }else {
            EaseUser user;
            for (int i=0;i<existMembers.size();i++){
                 user = EaseUserUtils.getUserInfo(existMembers.get(i));
                alluserList.add(user);
            }
//            alluserList.addAll(existMembers);
        }

		// sort the list
        Collections.sort(alluserList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNickname().compareTo(rhs.getNickname());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }
                
            }
        });

		ListView listView = (ListView) findViewById(R.id.list);
		contactAdapter = new PickContactAdapter(this, R.layout.em_row_contact_with_checkbox, alluserList);
		listView.setAdapter(contactAdapter);
		((EaseSidebar) findViewById(R.id.sidebar)).setListView(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
				checkBox.toggle();

			}
		});
	}

	/**
	 * save selected members
	 * 
	 * @param v
	 */
	public void save(View v) {
		List<String> var = getToBeAddMembers();
//		setResult(RESULT_OK, new Intent().putExtra("newmembers", var.toArray(new String[var.size()])));
//		finish();
		 if(changeAdmin||mingpian){
			if(var.size()>1 ){
				Toast.makeText(this,"选择人数不能大于1个",Toast.LENGTH_SHORT).show();
				return;
			}
			Intent i=new Intent().putStringArrayListExtra("result",(ArrayList)var);
			setResult(RESULT_OK,i);
			finish();
		}
		else if(isFromCicle){

			Intent i=new Intent().putStringArrayListExtra("result",(ArrayList)var);
			setResult(RESULT_OK,i);
			finish();
		}
		else if(isCreatingNewGroup)
			createGroup(var.toArray(new String[var.size()]));
		else{
			setResult(RESULT_OK, new Intent().putStringArrayListExtra("newmembers", (ArrayList)var));
			finish();
		}
	}
	private void createGroup(final String[] memberList){
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("创建中");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {

				;
				String desc = "";
				String[] members = memberList;
				try {

					List<EMGroup>	grouplist = EMClient.getInstance().groupManager().getAllGroups();
					int count =0;
					for(EMGroup group:grouplist){
						if(group.getGroupName().startsWith("群聊")){
							count++;
						}
					}
					final String groupName = "群聊"+"("+(count+1)+")";
					EMGroupOptions option = new EMGroupOptions();
					option.maxUsers = 200;
					option.inviteNeedConfirm = false;
					option.style=EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;

					String reason = GroupPickContactsActivity.this.getString(R.string.invite_join_group);
					reason  = EMClient.getInstance().getCurrentUser() + reason + groupName;


				final	EMGroup group=	EMClient.getInstance().groupManager().createGroup(groupName, "", members, "", option);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Intent i=new Intent().putExtra("groupId",group.getGroupId());
							setResult(RESULT_OK,i);
							finish();
						}
					});
				} catch (final HyphenateException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(GroupPickContactsActivity.this, "创建失败", Toast.LENGTH_LONG).show();
						}
					});
				}

			}
		}).start();
	}
	/**
	 * get selected members
	 * 
	 * @return
	 */
	private List<String> getToBeAddMembers() {
		List<String> members = new ArrayList<String>();
		int length = contactAdapter.isCheckedArray.length;
		for (int i = 0; i < length; i++) {
			String username = contactAdapter.getItem(i).getUsername();
			if(changeAdmin){
				if (contactAdapter.isCheckedArray[i] ) {
					members.add(username);
				}
			}else{
				if (contactAdapter.isCheckedArray[i] && !existMembers.contains(username)) {
					members.add(username);
				}
			}

		}

		return members;
	}

	/**
	 * adapter
	 */
	private class PickContactAdapter extends EaseContactAdapter {

		private boolean[] isCheckedArray;

		public PickContactAdapter(Context context, int resource, List<EaseUser> users) {
			super(context, resource, users);
			isCheckedArray = new boolean[users.size()];
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);

			final String username = getItem(position).getUsername();

			final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
			ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
			TextView nameView = (TextView) view.findViewById(R.id.name);
			
			if (checkBox != null) {
//			    if(existMembers != null && existMembers.contains(username)){
//                    checkBox.setButtonDrawable(R.drawable.em_checkbox_bg_gray_selector);
//                }else{
//                    checkBox.setButtonDrawable(R.drawable.em_checkbox_bg_selector);
//                }

				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// check the exist members
						if (existMembers.contains(username)) {
								isChecked = true;
								checkBox.setChecked(true);
						}
						isCheckedArray[position] = isChecked;

					}
				});
				// keep exist members checked
				if (existMembers.contains(username)&& !changeAdmin) {
						checkBox.setChecked(true);
						isCheckedArray[position] = true;
				} else {
					checkBox.setChecked(isCheckedArray[position]);
				}
			}

			return view;
		}
	}

	public void back(View view){
		finish();
	}
	
}
