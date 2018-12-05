package com.quchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class CircleSettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout part,all,self;
    private ImageView part_img,all_img,self_img;
    private TextView save;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_circle_setting);
        part=findViewById(R.id.part);
        all=findViewById(R.id.all);
        self=findViewById(R.id.self);
        save=findViewById(R.id.save);

        all_img=findViewById(R.id.all_img);
        part_img=findViewById(R.id.part_img);
        self_img=findViewById(R.id.self_img);

        save.setOnClickListener(this);
        part.setOnClickListener(this);
        all.setOnClickListener(this);
        self.setOnClickListener(this);
    }
    private int allow_type;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.all:
                allow_type=1;
                all_img.setVisibility(View.VISIBLE);
                part_img.setVisibility(View.INVISIBLE);
                self_img.setVisibility(View.INVISIBLE);
                break;
            case R.id.self:
                allow_type=2;
                all_img.setVisibility(View.INVISIBLE);
                self_img.setVisibility(View.VISIBLE);
                part_img.setVisibility(View.INVISIBLE);
                break;
            case R.id.part:
                allow_type=3;
                all_img.setVisibility(View.INVISIBLE);
                self_img.setVisibility(View.INVISIBLE);
                part_img.setVisibility(View.VISIBLE);
                startActivityForResult(new Intent(this,GroupPickContactsActivity.class),100);
                break;
            case R.id.save:
                Intent i = new Intent();
                i.putExtra("allow_type",allow_type);
                if(null !=uses && uses.size()!=0){
                    i.putStringArrayListExtra("list",uses);
                }
                setResult(RESULT_OK,i);
                finish();
                break;
        }
    }
    private ArrayList<String> uses = new ArrayList<>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode==100){
                uses = data.getStringArrayListExtra("result");
            }
        }
    }
}
