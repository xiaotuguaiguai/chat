package com.quchat.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.WriterException;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.utils.TempCache;
import com.quchat.widget.CircleImageView;
import com.quchat.widget.XCRoundRectImageView;
import com.yzq.zxinglibrary.encode.CodeCreator;

public class MyQrCodeActivity extends BaseActivity {
    private ImageView qrcode,img_back;
    private CircleImageView face;
    private TextView name,userId;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        String nametext=TempCache.UserBean.getData().getNick_name();
        String faceurl=TempCache.UserBean.getData().getHead_url();
        setContentView(R.layout.activity_myqrcode);
        qrcode=findViewById(R.id.qrcode);
        face=findViewById(R.id.face);
        userId=findViewById(R.id.userId);
        name=findViewById(R.id.name);
        name.setText(nametext);
        userId.setText("趣信号: "+Constant.USERNAME);
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.logo);
        Glide.with(this).load(faceurl).apply(options).into(face);
        img_back=findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        code();

    }

    private void code(){
        String contentEtString =Constant.USERNAME;

        if (TextUtils.isEmpty(contentEtString)) {
            Toast.makeText(this, "contentEtString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = null;
        try {
            /*
             * contentEtString：字符串内容
             * w：图片的宽
             * h：图片的高
             * logo：不需要logo的话直接传null
             * */

//            Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            bitmap = CodeCreator.createQRCode(contentEtString, 400, 400,null);
            qrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
