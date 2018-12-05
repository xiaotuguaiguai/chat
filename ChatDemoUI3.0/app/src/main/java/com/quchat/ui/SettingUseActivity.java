package com.quchat.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.quchat.R;
import com.quchat.widget.CustomTextRow;

public class SettingUseActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout layout_logout;
    private ImageView img_back;
    private CustomTextRow clear_layout,ziti_layout,psw_layout;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_setting_use);
        img_back=findViewById(R.id.img_back);
        clear_layout=findViewById(R.id.clear_layout);
        ziti_layout=findViewById(R.id.ziti_layout);

        clear_layout.setOnClickListener(this);
        ziti_layout.setOnClickListener(this);

        img_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.clear_layout:
            case R.id.ziti_layout:
                Toast.makeText(SettingUseActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }
}
