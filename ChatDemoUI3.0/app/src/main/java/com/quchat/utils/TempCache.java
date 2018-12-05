package com.quchat.utils;


import com.hyphenate.easeui.EaseConstant;
import com.quchat.domain.ContactListBean;
import com.quchat.domain.UserInfoBean;

import java.util.List;

public class TempCache {
    public static UserInfoBean UserBean;
    public static List<ContactListBean.MyData> friendList;

    public static UserInfoBean getUserBean() {
        return UserBean;
    }

    public static void setUserBean(UserInfoBean userBean) {
        UserBean = userBean;
        if(null== EaseConstant.USERINFO){
            EaseConstant.USERINFO = new com.hyphenate.easeui.model.UserInfoBean();
        }
        com.hyphenate.easeui.model.UserInfoBean.MyData myData =  EaseConstant.USERINFO.getData();

        myData.setHead_url(userBean.getData().getHead_url());
        myData.setNick_name(userBean.getData().getNick_name());
        myData.setSex(userBean.getData().getSex());
        EaseConstant.USERINFO.setData(myData);
    }

    public static List<ContactListBean.MyData> getFriendList() {
        return friendList;
    }

    public static void setFriendList(List<ContactListBean.MyData> friendList) {
        TempCache.friendList = friendList;

//        EaseConstant.friendList;

    }
}
