package com.quchat.domain;

import java.util.List;

public class FriendCircleBean {
    private int code;
    private String msg;
    private List<FriendCircleData> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<FriendCircleData> getData() {
        return data;
    }

    public void setData(List<FriendCircleData> data) {
        this.data = data;
    }
}
