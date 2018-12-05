package com.quchat.domain;

public class CircleCommonBean {
    private int id;
    private String content;
    private String created_at;
    private String user_name;
    private String head_url;
    private int mid;
    private String nick_name;
    private String user_id;
    private String reply_to_user_id;
    private String reply_to_user_name;
    private String reply_user_nickname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getHead_url() {
        return head_url;
    }

    public void setHead_url(String head_url) {
        this.head_url = head_url;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReply_to_user_id() {
        return reply_to_user_id;
    }

    public void setReply_to_user_id(String reply_to_user_id) {
        this.reply_to_user_id = reply_to_user_id;
    }

    public String getReply_to_user_name() {
        return reply_to_user_name;
    }

    public void setReply_to_user_name(String reply_to_user_name) {
        this.reply_to_user_name = reply_to_user_name;
    }

    public String getReply_user_nickname() {
        return reply_user_nickname;
    }

    public void setReply_user_nickname(String reply_user_nickname) {
        this.reply_user_nickname = reply_user_nickname;
    }
}
