package com.quchat.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.ui.MyRechargeActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.adapter.MeWalletGridViewAdapter;
import com.quchat.domain.UserInfoBean;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.TempCache;


public class MyWalletActivity extends BaseActivity implements View.OnClickListener {
    private Button recharge,record,withdraw;
    private RecyclerView recycleView;
    private TextView money;
    private LinearLayout layout_yinhang,layout_record;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet);
        ImageView img_back=findViewById(com.hyphenate.easeui.R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        money = findViewById(R.id.money);
        layout_yinhang= findViewById(R.id.layout_yinhang);
        layout_record= findViewById(R.id.layout_record);
        layout_yinhang.setOnClickListener(this);
        layout_record.setOnClickListener(this);
        withdraw = findViewById(R.id.withdraw);
        withdraw.setOnClickListener(this);
        record = findViewById(R.id.record);
        record.setOnClickListener(this);

        recharge = findViewById(R.id.recharge);
        recharge.setOnClickListener(this);

        recycleView = findViewById(R.id.recycleView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        recycleView.setLayoutManager(gridLayoutManager);
        MeWalletGridViewAdapter adapter = new MeWalletGridViewAdapter(this);
        recycleView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfo();
    }

    private UserInfoBean userInfoBean;
    private void getInfo(){
        final ProgressDialog dialog = ProgressDialog.show(this,"","加载中");
        OkGo.<String>get(URL.GETINFO).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",Constant.USERNAME)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        userInfoBean=(UserInfoBean) JsonUitl.stringToObject(response.body().toString(), UserInfoBean.class);
                        if(userInfoBean.getCode()==200){
                            money.setText(userInfoBean.getData().getBalance()+"");
                        }
                        TempCache.setUserBean(userInfoBean);
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        dialog.dismiss();
                        Log.e("sserror",response.body());
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.recharge) {
            startActivity(new Intent(MyWalletActivity.this,RechargeActivity.class));

        }else if(i == R.id.record){
            startActivity(new Intent(MyWalletActivity.this,MyRecordActivity.class));

        }else if(i == R.id.withdraw){
            startActivity(new Intent(MyWalletActivity.this,MyWithDrawActivity.class));

        }else if(i==R.id.layout_record){
            startActivity(new Intent(MyWalletActivity.this,MyRecordActivity.class));
        }else if(i==R.id.layout_yinhang){
            Toast.makeText(MyWalletActivity.this,"暂未开通",Toast.LENGTH_SHORT).show();
        }
    }


}
