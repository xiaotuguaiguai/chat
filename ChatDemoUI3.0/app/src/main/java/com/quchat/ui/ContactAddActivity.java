package com.quchat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.quchat.R;

public class ContactAddActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.contact_activity);
        ContactListFragment    chatFragment = new ContactListFragment();
//        ContactFragment2    chatFragment = new ContactFragment2();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatFragment).commit();
        ImageView img_back=findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
