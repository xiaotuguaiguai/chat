package com.hyphenate.easeui.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.RecyclerGridView2Adapter;
import com.hyphenate.easeui.model.UserInfoBean;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSharedUtils;
import com.hyphenate.easeui.utils.EaseTopUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;
import com.superrtc.call.DataChannel;

import java.util.ArrayList;
import java.util.List;

public class ChatSettingDetailActivity extends EaseBaseActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private ImageView img_back,wuran_img,top_img;
    private RelativeLayout tousu_layout,clear_layout,file_layout,jinzhi_layout,wurao_layout,top_layout;
    private RecyclerGridView2Adapter adapter;
    private List<String> imgdata = new ArrayList<>();
    private int chatType;
    private String toChatUsername;
    private List<UserInfoBean> userInfos = new ArrayList<>();
    private  boolean isJinzhi,isTop;
    private EMGroup group;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_chat_detail);
        recyclerView =findViewById(R.id.recycleView);
        Bundle data = getIntent().getExtras();
        try {
            toChatUsername = data.getString("toChatUsername");
            chatType = data.getInt("chatType",-1);
            userInfos = (List<UserInfoBean>)data.getSerializable("infos");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(chatType==EaseConstant.CHATTYPE_GROUP){
            group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
        }
        img_back=findViewById(R.id.img_back);
        tousu_layout =findViewById(R.id.tousu_layout);
        clear_layout =findViewById(R.id.clear_layout);
        file_layout =findViewById(R.id.file_layout);
        jinzhi_layout =findViewById(R.id.jinzhi_layout);
        wurao_layout =findViewById(R.id.wurao_layout);
        top_layout =findViewById(R.id.top_layout);
        wuran_img=findViewById(R.id.wuran_img);
        top_img=findViewById(R.id.top_img);

        img_back.setOnClickListener(this);
        tousu_layout.setOnClickListener(this);
        clear_layout.setOnClickListener(this);
        file_layout.setOnClickListener(this);
        jinzhi_layout.setOnClickListener(this);
        wurao_layout.setOnClickListener(this);
        top_layout.setOnClickListener(this);

         isJinzhi =    EaseSharedUtils.isEnableMsgRing(this,EaseConstant.USER_ID,toChatUsername);
        if(!isJinzhi){
            wuran_img.setImageResource(R.drawable.ease_open_icon);
        }else{
            wuran_img.setImageResource(R.drawable.ease_close_icon);
        }

        isTop=EaseTopUtils.isEnableMsgTop(this,EaseConstant.USER_ID,toChatUsername);
        if(isTop){
            top_img.setImageResource(R.drawable.ease_open_icon);
        }else{
            top_img.setImageResource(R.drawable.ease_close_icon);
        }

        GridLayoutManager mgr = new GridLayoutManager(this, 6);
        recyclerView.setLayoutManager(mgr);
//        localMediaList.add(null);
        adapter = new RecyclerGridView2Adapter(this,userInfos);
        recyclerView.setAdapter(adapter);

        adapter.setOnRecyclerViewItemListener(new RecyclerGridView2Adapter.OnRecyclerViewItemListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                if(position==userInfos.size()){
                    Intent intent=new Intent("app.ui.GroupPickContactsActivity");
                    if(chatType==2){// 群聊
                        intent.putExtra("groupId",toChatUsername);
                        startActivityForResult(intent,100);
                    }else{
                        startActivityForResult(intent,0);
                    }
                }else{
                    Intent i = new Intent("app.ui.PersonInfoActivity");
                    i.putExtra("username",userInfos.get(position).getData().getUid());
                    startActivity(i);
                }



            }

            @Override
            public void onItemCancelClickListener(View view, int position) {

            }
        });
    }
    protected void emptyHistory() {
      final  EMConversation conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);

        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(this,null, msg, null,new EaseAlertDialog.AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if(confirmed){
                    if (conversation != null) {
                        conversation.clearAllMessages();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ChatSettingDetailActivity.this,"已清除",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
        }, true).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==100){// 添加好友
                Intent intent = new Intent("app.ui.ChatActivity");
                // it is group chat
                intent.putExtra("chatType", 2);
                intent.putExtra("userId", toChatUsername);
                Intent i = new Intent();
                i.putExtra("newmembers",data.getStringArrayListExtra("newmembers"));
                setResult(RESULT_OK,i);
//                startActivityForResult(intent, 10);
                finish();
            }else if(requestCode==200){// 更换管理员
                List<String> s= data.getStringArrayListExtra("result");
                if(null!=s && s.size()!=0){
                    changeAdmin(1,s.get(0));
                }

            }
                else{
                final  String ss = data.getStringExtra("groupId");
                Intent intent = new Intent("app.ui.ChatActivity");
                // it is group chat
                intent.putExtra("chatType", 2);
                intent.putExtra("userId", ss);
                startActivityForResult(intent, 10);
                finish();
            }

        }
    }

private void changeAdmin(final int type,final String userId){
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                if(type==1){
                    EMClient.getInstance().groupManager().changeOwner(toChatUsername, userId);//需异部处理
                    EMClient.getInstance().chatManager().deleteConversation(toChatUsername, true);


                }else{
                    EMClient.getInstance().groupManager().leaveGroup(toChatUsername);
                    EMClient.getInstance().chatManager().deleteConversation(toChatUsername, true);


                }
                setResult(111);
                finish();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }


        }
    }).start();
}



    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.img_back) {
            finish();

        }else if(i == R.id.tousu_layout){
            String msg = "确定退出群聊？";


            new EaseAlertDialog(this,null, msg, null,new EaseAlertDialog.AlertDialogUser() {

                @Override
                public void onResult(boolean confirmed, Bundle bundle) {
                    if(confirmed){
                        if(null!=group){
                            if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
                                Intent ii =new Intent("app.ui.GroupPickContactsActivity");
                                ii.putExtra("changeAdmin",true);
                                ii.putExtra("groupId",toChatUsername);

                                startActivityForResult(ii,200);
                            }
                        }else{
                            changeAdmin(0,"");
                        }
                }}
            }, true).show();

            Toast.makeText(this,"暂未开通",Toast.LENGTH_SHORT).show();
        }else if(i == R.id.clear_layout){
            emptyHistory();
        }else if(i == R.id.file_layout){
            Toast.makeText(this,"暂未开通",Toast.LENGTH_SHORT).show();
        }else if(i == R.id.jinzhi_layout){
            Toast.makeText(this,"暂未开通",Toast.LENGTH_SHORT).show();
        }else if(i == R.id.wurao_layout){
            isJinzhi=!isJinzhi;
            if(!isJinzhi){
                EaseSharedUtils.setEnableMsgRing(this,EaseConstant.USER_ID,toChatUsername,false);
                wuran_img.setImageResource(R.drawable.ease_open_icon);
            }else{
                EaseSharedUtils.setEnableMsgRing(this,EaseConstant.USER_ID,toChatUsername,true);
                wuran_img.setImageResource(R.drawable.ease_close_icon);
            }
        }else if(i == R.id.top_layout){
//            Toast.makeText(this,"暂未开通",Toast.LENGTH_SHORT).show();

            isTop=!isTop;
            if(isTop){
                top_img.setImageResource(R.drawable.ease_open_icon);
                EaseTopUtils.setAddNewTop(true);
            }else{
                top_img.setImageResource(R.drawable.ease_close_icon);
            }

            EaseTopUtils.setEnableMsgTop(this,EaseConstant.USER_ID,toChatUsername,isTop);

        }
    }
}
