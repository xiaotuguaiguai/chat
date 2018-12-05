package com.quchat.domain;

public class UserInfoBean {


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

    public class MyData{
        private String head_url;
        private String nick_name;
        private String huanxin_id;
        private int sex;
        private float balance;
        private int news_num;
        private String address_ids;
        private String moment_cover;
        private String address_name;
        private String signature;


        public int getNews_num() {
            return news_num;
        }

        public void setNews_num(int news_num) {
            this.news_num = news_num;
        }

        public String getAddress_ids() {
            return address_ids;
        }

        public void setAddress_ids(String address_ids) {
            this.address_ids = address_ids;
        }

        public String getMoment_cover() {
            return moment_cover;
        }

        public void setMoment_cover(String moment_cover) {
            this.moment_cover = moment_cover;
        }

        public String getAddress_name() {
            return address_name;
        }

        public void setAddress_name(String address_name) {
            this.address_name = address_name;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
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
