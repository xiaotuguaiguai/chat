package com.quchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.adapter.ContactAdapter;

import com.quchat.domain.ContactListBean;
import com.quchat.domain.PersonBean;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.PinyinComparator;
import com.quchat.utils.PinyinUtils;
import com.quchat.widget.SideBar;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ContactFragment2 extends Fragment {
    private RecyclerView recycleView,headRecycleView ;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private ContactAdapter contactAdapter;
    private SideBar sidebar;
    private LinearLayoutManager linearLayoutManager;
    private  String[] data;
    private View headerView,footView;
    private TextView footText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        recycleView=view.findViewById(R.id.recycleView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recycleView.setLayoutManager(linearLayoutManager);
        recycleView.setOnScrollListener(new RecyclerViewListener() );

        data = new String[]{};
         headerView = inflater.inflate(R.layout.contact_header, null);
        footView= inflater.inflate(R.layout.fragment_contact_list_foot, null);
        footText=footView.findViewById(R.id.footText);
        sidebar=view.findViewById(R.id.sidebar);
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                // 该字母首次出现的位置
//                Toast.makeText(getActivity(),s+"",Toast.LENGTH_SHORT).show();

                int position = contactAdapter.getPositionForSelection(s.charAt(0));
//                Toast.makeText(getActivity(),position+"",Toast.LENGTH_SHORT).show();
                if (position != -1) {
                    moveToPosition(position);
                }

            }
        });
        getNickName();
        return view;
    }
    private void getNickName(){
        OkGo.<String>get(URL.GETFRIENDLIST). headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",Constant.USERNAME).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Log.e("ss",response.body().toString());
                ContactListBean bean = (ContactListBean) JsonUitl.stringToObject(response.body().toString(), ContactListBean.class);
                if(bean.getCode()==200){
                    List<ContactListBean.MyData> datas = bean.getData();
                    data= new String[datas.size()];
                    for (int i=0;i<datas.size();i++){

                        data[i]=datas.get(i).getNick_name();
                    }
                    List<PersonBean> list=  getData(datas);
                    Collections.sort(list,new PinyinComparator());
                    contactAdapter = new ContactAdapter(getActivity(),list);

                    contactAdapter.setOnItemClick(new ContactAdapter.OnItemClick() {
                        @Override
                        public void onItemClick(String uid) {
                            startActivity(new Intent(getActivity(),ChatActivity.class)
                            .putExtra("userId",uid)
                            );
                        }
                    });

                    mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(contactAdapter);

                    mHeaderAndFooterWrapper.addHeaderView(headerView);
                    mHeaderAndFooterWrapper.addFootView(footView);

                    recycleView.setAdapter(mHeaderAndFooterWrapper);
                    mHeaderAndFooterWrapper.notifyDataSetChanged();
                    footText.setText(datas.size()+"位好友");
                }


            }
        });
    }
    /**
     * 用户点击的分类在rv的位置
     */
    private int mIndex;
    /**
     * rv是否需要第二次滚动
     */
    private boolean move = false;
    private void moveToPosition(int index) {
        //获取当前recycleView屏幕可见的第一项和最后一项的Position
        int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = linearLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (index <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            recycleView.scrollToPosition(index);
        } else if (index <= lastItem) {
            //当要置顶的项已经在屏幕上显示时，计算它离屏幕原点的距离
            int top = recycleView.getChildAt(index - firstItem).getTop();
            recycleView.scrollBy(0, top);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            recycleView.scrollToPosition(index);
            //记录当前需要在RecyclerView滚动监听里面继续第二次滚动
            move = true;
        }
    }
    class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在这里进行第二次滚动（最后的距离）
            if (move) {
                move = false;
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                int n = mIndex - linearLayoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < recycleView.getChildCount()) {
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    int top = recycleView.getChildAt(n).getTop();
                    //最后的移动
                    recycleView.scrollBy(0, top);
                }
            }
        }
    }



    private List<PersonBean> getData(List<ContactListBean.MyData> data ) {
        List<PersonBean> listarray = new ArrayList<PersonBean>();
        for (int i = 0; i < data.size(); i++) {
            String pinyin = PinyinUtils.getPingYin(data.get(i).getNick_name());
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();

            PersonBean person = new PersonBean();
            person.setName(data.get(i).getNick_name());
            person.setPinYin(pinyin);
            person.setHuanxin_id(data.get(i).getHuanxin_id());
            person.setImgUrl(data.get(i).getHead_url());
            person.setUserId(data.get(i).getUsername());
            // 正则表达式，判断首字母是否是英文字母
            if (Fpinyin.matches("[A-Z]")) {
                person.setFirstPinYin(Fpinyin);
            } else {
                person.setFirstPinYin("#");
            }

            listarray.add(person);
        }
        return listarray;

    }
}

