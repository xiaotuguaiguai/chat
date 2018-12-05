package com.quchat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.quchat.Constant;
import com.quchat.DemoHelper;
import com.quchat.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.quchat.widget.CustomTextRow;

public class SettingDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView img_back;
    private CustomTextRow about_layout,question_layout,help_layout,use_layout,notice_layout,account_layout,layout_logout;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_setting_detail);
        layout_logout=findViewById(R.id.layout_logout);

        about_layout=findViewById(R.id.about_layout);
        question_layout=findViewById(R.id.question_layout);
        help_layout=findViewById(R.id.help_layout);
        use_layout=findViewById(R.id.use_layout);
        notice_layout=findViewById(R.id.notice_layout);
        account_layout=findViewById(R.id.account_layout);

        about_layout.setOnClickListener(this);
        question_layout.setOnClickListener(this);
        help_layout.setOnClickListener(this);
        use_layout.setOnClickListener(this);
        notice_layout.setOnClickListener(this);
        account_layout.setOnClickListener(this);

        img_back=findViewById(R.id.img_back);
        layout_logout.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }
    void logout() {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DemoHelper.getInstance().logout(true,new EMCallBack() {

            @Override
            public void onSuccess() {
               runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Constant.TOKEN=null;
                        // show login screen
                        finish();
                        startActivity(new Intent(SettingDetailActivity.this, LoginActivity.class));

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        Toast.makeText(SettingDetailActivity.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.about_layout:
                startActivity(new Intent(SettingDetailActivity.this,AboutActivity.class));

                break;
            case R.id.question_layout:
                Toast.makeText(SettingDetailActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
                break;
            case R.id.help_layout:
                Toast.makeText(SettingDetailActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
                break;
            case R.id.use_layout:
                startActivity(new Intent(SettingDetailActivity.this,SettingUseActivity.class));
                break;
            case R.id.notice_layout:
                Toast.makeText(SettingDetailActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
                break;
            case R.id.account_layout:
                startActivity(new Intent(SettingDetailActivity.this,SettingAccountActivity.class));

                break;
            case R.id.layout_logout:
                logout();
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }
}
