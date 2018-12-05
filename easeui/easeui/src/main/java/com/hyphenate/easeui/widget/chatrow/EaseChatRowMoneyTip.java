package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;


public class EaseChatRowMoneyTip extends EaseChatRow {
    private TextView tv_state;
    public EaseChatRowMoneyTip(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_money_tip : R.layout.ease_row_send_money_tip, this);
    }

    @Override
    protected void onFindViewById() {
        tv_state = (TextView) findViewById(R.id.tv_state);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        adapter.notifyDataSetChanged();
    }



    @Override
    protected void onSetUpView() {
        String str = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_DESC,"");
        if(message.direct() == EMMessage.Direct.RECEIVE){
            tv_state.setText("红包已被领取");
        }else {
            tv_state.setText("您已领取红包");
        }
    }

}
