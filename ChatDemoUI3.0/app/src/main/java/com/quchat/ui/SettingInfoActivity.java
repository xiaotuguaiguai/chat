package com.quchat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quchat.R;
import com.quchat.widget.CustomTextRow;
import com.hyphenate.chat.EMContactManager;

import java.util.ArrayList;
import java.util.List;

public class SettingInfoActivity extends BaseActivity implements View.OnClickListener {
    private ImageView img_back;
    private CustomTextRow note_layout,tuijian_layout,tousu_layout;
    private ImageView kaiguan;
    private String userName;
    private List< String > blackList = new ArrayList<>();
    private boolean addBlack;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        userName= getIntent().getStringExtra("userName");
        setContentView(R.layout.activity_setting_info);
        img_back=findViewById(R.id.img_back);
        note_layout=findViewById(R.id.note_layout);
        tuijian_layout=findViewById(R.id.tuijian_layout);
        tousu_layout=findViewById(R.id.tousu_layout);
        kaiguan=findViewById(R.id.kaiguan);
        img_back.setOnClickListener(this);
        note_layout.setOnClickListener(this);
        tuijian_layout.setOnClickListener(this);
        tousu_layout.setOnClickListener(this);
//        blackList = EMContactManager.getBlackListUsernames();
//        if(null!=blackList && blackList.size()!=0){
//            for(int i=0;i<blackList.size();i++){
//                if(userName.equals(blackList.get(i))){
//                    addBlack=true;
//                }
//            }
//        }
        if(addBlack){
            kaiguan.setImageResource(R.drawable.ease_open_icon);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.note_layout:

            case R.id.tuijian_layout:

            case R.id.tousu_layout:

            case R.id.kaiguan:
//                if(addBlack){
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
////                            EMContactManager.deleteUserFromBlackList(userName);//需异步处理
//
//                        }
//                    }).start();
//                    kaiguan.setImageResource(R.drawable.ease_close_icon);

//                }else{
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
////                            EMContactManager.getInstance().addUserToBlackList(userName,false);//需异步处理
//
//                        }
//                    }).start();
//                    kaiguan.setImageResource(R.drawable.ease_open_icon);

//                }
                break;

        }
    }
}
