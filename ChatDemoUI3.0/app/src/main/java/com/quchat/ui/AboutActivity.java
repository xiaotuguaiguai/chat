package com.quchat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.quchat.R;
import com.quchat.widget.CustomTextRow;

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private CustomTextRow pingfen_layout,zhengce_layout,xieyi_layout;
    private ImageView img_back;
    private TextView version;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_about);
        pingfen_layout=findViewById(R.id.pingfen_layout);
        zhengce_layout=findViewById(R.id.zhengce_layout);
        xieyi_layout=findViewById(R.id.xieyi_layout);
        version=findViewById(R.id.version);
        img_back=findViewById(R.id.img_back);
        pingfen_layout.setOnClickListener(this);
        xieyi_layout.setOnClickListener(this);
        zhengce_layout.setOnClickListener(this);
        img_back.setOnClickListener(this);
        version.setText("趣信"+getVersionName()+"");
    }
    private String getVersionName()
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packInfo.versionName;
        return version;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.xieyi_layout:
            case R.id.zhengce_layout:
            case R.id.pingfen_layout:
                Toast.makeText(AboutActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }
}
