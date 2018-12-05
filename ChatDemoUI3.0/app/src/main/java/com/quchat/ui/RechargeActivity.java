package com.quchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.ui.MyRechargeActivity;
import com.hyphenate.easeui.utils.DecimalDigitsInputFilter;
import com.quchat.R;

public class RechargeActivity extends BaseActivity{
    private TextView cancel,next;
    private EditText money;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_recharge);
        cancel=findViewById(R.id.cancel);
        next=findViewById(R.id.next);
        money=findViewById(R.id.money);

        money.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(money.getText().toString())||money.getText().toString().equals("0.00")){
                    Toast.makeText(RechargeActivity.this,"请输入正确金额",Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(new Intent(RechargeActivity.this,MyRechargeActivity.class)
                        .putExtra("money",money.getText().toString())
                        ,100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==100){

//                setResult(RESULT_OK);
//                finish();
            }
        }
    }
}
