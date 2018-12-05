package com.quchat.utils;

import com.quchat.domain.PersonBean;

import java.util.Comparator;

public class PinyinComparator implements Comparator<PersonBean> {

    public int compare(PersonBean o1, PersonBean o2) {
        //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
        if (o2.getFirstPinYin().equals("#")) {
            return -1;
        } else if (o1.getFirstPinYin().equals("#")) {
            return 1;
        } else {
            return o1.getFirstPinYin().compareTo(o2.getFirstPinYin());
        }
    }
}


