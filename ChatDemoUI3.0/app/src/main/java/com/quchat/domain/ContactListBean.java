package com.quchat.domain;

import java.util.List;

public class ContactListBean {

    private String msg;
    private int code;
    private List<MyData> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<MyData> getData() {
        return data;
    }

    public void setData(List<MyData> data) {
        this.data = data;
    }

    public class MyData{
        public String username;
        public String nick_name;
        public String head_url;
        public String huanxin_id;
        public int sex;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getHead_url() {
            return head_url;
        }

        public void setHead_url(String head_url) {
            this.head_url = head_url;
        }

        public String getHuanxin_id() {
            return huanxin_id;
        }

        public void setHuanxin_id(String huanxin_id) {
            this.huanxin_id = huanxin_id;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }
    }
}
