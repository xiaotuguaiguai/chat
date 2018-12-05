package com.quchat.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.easeui.ui.MyRechargeActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.adapter.RecordAdapter;
import com.quchat.domain.RecordBean;
import com.quchat.domain.RecordItemBean;
import com.quchat.utils.JsonUitl;

import java.util.ArrayList;
import java.util.List;


public class MyRecordActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private int pageNo=0;
    private RecordAdapter adapter;
    private List<RecordItemBean.MyData> myData = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ImageView img_back=findViewById(com.hyphenate.easeui.R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView=findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecordAdapter(this,myData);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastPosition = -1;
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    if(lastPosition == recyclerView.getLayoutManager().getItemCount()-1){
//                        Toast.makeText(MyRecordActivity.this, "滑动到底了", Toast.LENGTH_SHORT).show();
                        getData();
                    }

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        adapter.setCallBack(new RecordAdapter.CallBack() {
            @Override
            public void loadMore() {
                pageNo++;
                getData();
            }
        });
        recyclerView.setAdapter(adapter);
        getData();
    }

    private void getData(){
        pageNo++;
        final ProgressDialog dialog = ProgressDialog.show(this,"","加载中");
        OkGo.<String>post("http://114.116.66.158:8081/api/account/list").tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",Constant.USERNAME)
                .params("page",pageNo)
                .params("page_size",20)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        RecordBean bean = (RecordBean)JsonUitl.stringToObject(response.body(),RecordBean.class);
                        if(bean.getCode()==200){
                            RecordItemBean recordItemBean= bean.getData();
                            List<RecordItemBean.MyData> myData1= recordItemBean.getList();
                            if(myData1!=null){
                                myData.addAll(myData1);
                                adapter.setList(myData);
                                adapter.notifyDataSetChanged();
                            }

                        }
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

    }


}
