package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.exceptions.HyphenateException;


public class EaseChatRowMoney extends EaseChatRow {
    private TextView tv_money_state,tv_money_name;
    private LinearLayout bg_layout;
    public EaseChatRowMoney(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_groupmoney : R.layout.ease_row_sent_groupmoney, this);
    }

    @Override
    protected void onFindViewById() {
        tv_money_name = (TextView) findViewById(R.id.tv_money_name);
        tv_money_state = (TextView) findViewById(R.id.tv_money_state);
        bg_layout= (LinearLayout) findViewById(R.id.bg_layout);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        adapter.notifyDataSetChanged();
    }



    @Override
    protected void onSetUpView() {
        String str = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_DESC,"");
        if(message.direct() == EMMessage.Direct.RECEIVE){
            try {
                if(message.getBooleanAttribute("isReceive")){
                    bg_layout.setBackground(getResources().getDrawable(R.drawable.em_receive_group_money_off));
                }else{
                    bg_layout.setBackground(getResources().getDrawable(R.drawable.em_receive_group_money_on));
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            if(TextUtils.isEmpty(str)){
                tv_money_name.setText(getResources().getString(R.string.receive_money));
            }else {
                tv_money_name.setText(str);
            }
        }else {
            try {
                if(message.getBooleanAttribute("isReceive")){
                    bg_layout.setBackground(getResources().getDrawable(R.drawable.em_send_group_money_off));
                }else{
                    bg_layout.setBackground(getResources().getDrawable(R.drawable.em_send_group_money_on));
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            if(TextUtils.isEmpty(str)){
                tv_money_name.setText(getResources().getString(R.string.sent_money));
            }else {
                tv_money_name.setText(str);
            }
        }
        tv_money_state.setText("￥: "+message.getStringAttribute(EaseConstant.MESSAGE_ATTR_TRANSFER_NUM,"Null"));
    }

}
