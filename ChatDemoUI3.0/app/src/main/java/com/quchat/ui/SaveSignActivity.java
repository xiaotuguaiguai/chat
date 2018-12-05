package com.quchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.quchat.R;

public class SaveSignActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_sign);
       String s= getIntent().getStringExtra("sign");
       final  EditText name = findViewById(R.id.name);
        name.setText(s);
        ImageView img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("result", name.getText().toString().trim());
                //设置返回数据
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
