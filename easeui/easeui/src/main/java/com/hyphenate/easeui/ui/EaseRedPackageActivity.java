package com.hyphenate.easeui.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.CodeAndMsg;
import com.hyphenate.easeui.utils.DecimalDigitsInputFilter;
import com.hyphenate.easeui.utils.JsonUitl;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

public class EaseRedPackageActivity extends Activity {
    private TextView send_red,moneyNUm,back;
    private EditText desc,inputMoney;
    private ImageView question;
    private String msg_key,uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         uid = getIntent().getStringExtra("uid");
        setContentView(R.layout.activity_redpackage);
        send_red=findViewById(R.id.send_red);
        moneyNUm=findViewById(R.id.moneyNUm);
        back=findViewById(R.id.back);
        question=findViewById(R.id.question);
        inputMoney=findViewById(R.id.inputMoney);
        inputMoney.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EaseRedPackageActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        inputMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.equals("")){
                    moneyNUm.setText("0.00");
                }else
                 moneyNUm.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        desc=findViewById(R.id.desc);
        send_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                send();
            }
        });
    }

    private void send(){
        if(TextUtils.isEmpty(moneyNUm.getText().toString())){
            Toast.makeText(EaseRedPackageActivity.this,"请输入金额",Toast.LENGTH_SHORT).show();
            return;
        }
        msg_key=EaseConstant.USER_ID+"_"+System.currentTimeMillis();

      final  ProgressDialog progressDialog = ProgressDialog.show(this,"","发送中");
        OkGo.<String>post("http://114.116.66.158:8081/api/red_packet/send").tag(this)
                .headers("Authorization", "Bearer " + EaseConstant.TOKEN)
                .params("username",EaseConstant.USER_ID)
                .params("fusername",uid)
                .params("amount",moneyNUm.getText().toString())
                .params("msg_key",msg_key)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDialog.dismiss();
                        Log.e("ss",response.body().toString());
                        CodeAndMsg userInfoBean=(CodeAndMsg) JsonUitl.stringToObject(response.body().toString(), CodeAndMsg.class);
                        if(userInfoBean.getCode()==200){
                            Intent intent = new Intent();
                            intent.putExtra("price",moneyNUm.getText().toString());
                            intent.putExtra("msg_key",msg_key);
                            intent.putExtra("desc",TextUtils.isEmpty(desc.getText().toString())?"恭喜发财":desc.getText().toString());
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }else{
                            Toast.makeText(EaseRedPackageActivity.this,userInfoBean.getMsg(),Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
//                        Log.e("sserror",response.body());
                        progressDialog.dismiss();
                    }
                });
    }
}
