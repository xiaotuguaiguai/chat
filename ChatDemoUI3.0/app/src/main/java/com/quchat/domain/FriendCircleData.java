package com.quchat.domain;

import java.util.List;

public class FriendCircleData {
    private int id;
    private String content;
    private List<String> pic;
    private String huanxin_id;
    private String user_name;
    private String head_url;
    private String nick_name;
    private int comment_num;
    private List<CircleCommonBean> comments;
    private String created_at;
    private String like_num;
    private String is_like;

    public String getIs_like() {
        return is_like;
    }

    public void setIs_like(String is_like) {
        this.is_like = is_like;
    }

    public String getLike_num() {
        return like_num;
    }

    public void setLike_num(String like_num) {
        this.like_num = like_num;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

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

    public List<String> getPic() {
        return pic;
    }

    public void setPic(List<String> pic) {
        this.pic = pic;
    }

    public String getHuanxin_id() {
        return huanxin_id;
    }

    public void setHuanxin_id(String huanxin_id) {
        this.huanxin_id = huanxin_id;
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

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public List<CircleCommonBean> getComments() {
        return comments;
    }

    public void setComments(List<CircleCommonBean> comments) {
        this.comments = comments;
    }


}
