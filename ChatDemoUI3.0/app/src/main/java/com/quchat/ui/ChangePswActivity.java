package com.quchat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.EaseConstant;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.DemoHelper;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.domain.AuthBean;
import com.quchat.domain.CodeAndMsg;
import com.quchat.domain.UserInfoBean;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.SPUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

public class ChangePswActivity extends BaseActivity implements View.OnClickListener {
    private ImageView img_back;
    private TextView save;
    private EditText oldPsw,newPsw;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_changepsw);
        img_back=findViewById(R.id.img_back);
        save=findViewById(R.id.save);
        oldPsw=findViewById(R.id.oldPsw);
        newPsw=findViewById(R.id.newPsw);

        save.setOnClickListener(this);
    }

    private void changePsw(){
        if(TextUtils.isEmpty(oldPsw.getText().toString())||TextUtils.isEmpty(newPsw.getText().toString()) ) {
            Toast.makeText(ChangePswActivity.this, "请正确输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
          final  ProgressDialog progressDialog = ProgressDialog.show(this,"","修改中");
            OkGo.<String>post(URL.CHANGEPSW).tag(this)
                    .headers("Authorization", "Bearer " + Constant.TOKEN)
                    .params("username",Constant.USERNAME)
                    .params("user_password",oldPsw.getText().toString())
                    .params("new_user_password",newPsw.getText().toString())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("ss",response.body());
                            progressDialog.dismiss();
                            CodeAndMsg userInfoBean=(CodeAndMsg) JsonUitl.stringToObject(response.body().toString(), CodeAndMsg.class);
                            if(userInfoBean.getCode()==200){

                                Toast.makeText(ChangePswActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                logout();
                            }else{
                                Toast.makeText(ChangePswActivity.this,"修改失败",Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onError(Response<String> response) {

                            super.onError(response);
                            progressDialog.dismiss();
                        }
                    });

    }
    void logout() {
//        final ProgressDialog pd = new ProgressDialog(this);
//        String st = getResources().getString(R.string.Are_logged_out);
//        pd.setMessage(st);
//        pd.setCanceledOnTouchOutside(false);
//        pd.show();
        DemoHelper.getInstance().logout(true,new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
//                        pd.dismiss();
                        Constant.TOKEN=null;
                        // show login screen
                        startActivity(new Intent(ChangePswActivity.this, LoginActivity.class));

                        finish();

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
//                        pd.dismiss();
                        Toast.makeText(ChangePswActivity.this, "环信退出失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
               finish();
                break;
            case R.id.save:
                Toast.makeText(this,"暂未开通",Toast.LENGTH_SHORT).show();
//                changePsw();
                break;

        }
    }
}
