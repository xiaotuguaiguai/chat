package com.hyphenate.easeui.model;

import java.io.Serializable;

public class UserInfoBean implements Serializable {

   public UserInfoBean(){
       data = new MyData();
    }
    private int code;
    private String msg;
    private MyData data;

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

    public MyData getData() {
        return data;
    }

    public void setData(MyData data) {
        this.data = data;
    }

    public class MyData implements Serializable{
        private String head_url;
        private String nick_name;
        private String huanxin_id;
        private int sex;
        private float balance;
        private String uid;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public float getBalance() {
            return balance;
        }

        public void setBalance(float balance) {
            this.balance = balance;
        }

        public String getHead_url() {
            return head_url;
        }

        public void setHead_url(String head_url) {
            this.head_url = head_url;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
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
