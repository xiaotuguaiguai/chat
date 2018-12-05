/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui;

import com.hyphenate.easeui.domain.ContactListBean;
import com.hyphenate.easeui.model.UserInfoBean;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.util.List;

public class EaseConstant {
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
    
    public static final String MESSAGE_TYPE_RECALL = "message_recall";
    
    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";
    
    public static final String MESSAGE_ATTR_AT_MSG = "em_at_list";
    public static final String MESSAGE_ATTR_VALUE_AT_MSG_ALL = "ALL";

    
    
	public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;
    
    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_USER_NICK = "userNickName";

    public static final String MESSAGE_ISMONEY = "em_money";//红包
    public static final String ATTR_GROUP_MONEY_ID = "group_money_id";//红包id
    public static final String MESSAGE_ATTR_DESC = "em_message_desc";//消息描述
    public static final String MESSAGE_ATTR_TRANSFER_NUM = "em_transfer_money_num";//消息描述
    public static final String MESSAGE_ISGROUPMONEY = "em_group_money";//群组红包
    public static final String ATTR_PHOTO = "photo";//自己头像
    public static final String ATTR_TOPHOTO = "tophoto";//对方头像
    public static final String ATTR_NICKNAME = "nickname";//自己姓名
    public static final String ATTR_TONICKNAME = "tonickname";//对方姓名



    public static final String myExtTYpe="myExtTYpe";
    public static final String IS_CARD_TYPE="CARD";
    public static final String cardOwnerId="cardOwnerId";
    public static final String cardOwnerNickname="cardOwnerNickname";
    public static final String cardOwnerHeadurl="cardOwnerHeadurl";


    public static final String IS_MONEY_TIP="MONEY_TIP";



    public static  String TOKEN = null;
    public static  String USER_ID = null;
    public static UserInfoBean USERINFO = null;
    public static List<ContactListBean.MyData> friendList=null;

    public static final String WX_APPID="wx8ae3acfeb8b43e94";
    public static final String WX_APPSecret="13d4823e1c6b05fba08a06e93a97a725";
    public static IWXAPI WX_API;
}
