package com.quchat.domain;

public class RecordBean {
    private int code;
    private String msg;

    private RecordItemBean data;

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

    public RecordItemBean getData() {
        return data;
    }

    public void setData(RecordItemBean data) {
        this.data = data;
    }
}
