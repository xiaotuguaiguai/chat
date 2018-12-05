package com.quchat.domain;

import java.util.List;

public class RecordItemBean {
    private List<MyData> list;
    private int count;
    private int total_page;
    private int cur_page;

    public List<MyData> getList() {
        return list;
    }

    public void setList(List<MyData> list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public int getCur_page() {
        return cur_page;
    }

    public void setCur_page(int cur_page) {
        this.cur_page = cur_page;
    }

    public  class MyData{
      private int id;
      private int type;
      private float before_money;
      private float after_money;
      private float money;
      private int rid;
      private String time;

      public int getId() {
          return id;
      }

      public void setId(int id) {
          this.id = id;
      }

      public int getType() {
          return type;
      }

      public void setType(int type) {
          this.type = type;
      }

      public float getBefore_money() {
          return before_money;
      }

      public void setBefore_money(float before_money) {
          this.before_money = before_money;
      }

      public float getAfter_money() {
          return after_money;
      }

      public void setAfter_money(float after_money) {
          this.after_money = after_money;
      }

      public float getMoney() {
          return money;
      }

      public void setMoney(float money) {
          this.money = money;
      }

      public int getRid() {
          return rid;
      }

      public void setRid(int rid) {
          this.rid = rid;
      }

      public String getTime() {
          return time;
      }

      public void setTime(String time) {
          this.time = time;
      }
  }
}
