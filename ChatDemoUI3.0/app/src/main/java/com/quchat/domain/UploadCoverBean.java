package com.quchat.domain;

public class UploadCoverBean {
    private int code;
    private String msg;
    private String error;
    private MyData data;

    public MyData getData() {
        return data;
    }

    public void setData(MyData data) {
        this.data = data;
    }

    public class MyData{
        public String moment_cover;

        public String getMoment_cover() {
            return moment_cover;
        }

        public void setMoment_cover(String moment_cover) {
            this.moment_cover = moment_cover;
        }
    }
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

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
}
