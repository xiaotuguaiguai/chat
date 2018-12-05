package com.quchat.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.quchat.R;
import com.quchat.widget.CustomTextRow;

public class SettingAccountActivity extends BaseActivity implements View.OnClickListener {
    private ImageView img_back;
    private CustomTextRow phone_layout,quxin_layout,psw_layout;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_setting_account);
        img_back=findViewById(R.id.img_back);
        phone_layout=findViewById(R.id.phone_layout);
        quxin_layout=findViewById(R.id.quxin_layout);
        psw_layout=findViewById(R.id.psw_layout);

        phone_layout.setOnClickListener(this);
        quxin_layout.setOnClickListener(this);
        psw_layout.setOnClickListener(this);

        img_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.phone_layout:
            case R.id.quxin_layout:
                Toast.makeText(SettingAccountActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
                break;
            case R.id.psw_layout:
                startActivity(new Intent(SettingAccountActivity.this,ChangePswActivity.class));
                    break;
            case R.id.img_back:
                finish();
                break;
        }
    }
}
