package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.CircleImageView;


public class EaseChatRowCard extends EaseChatRow {
    private TextView tv_id,tv_name;
    private CircleImageView img_face;
    private Context context;

    public EaseChatRowCard(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
        this.context=context;
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_card : R.layout.ease_row_sent_card, this);
    }

    @Override
    protected void onFindViewById() {
        img_face= (CircleImageView) findViewById(R.id.img_face);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_id = (TextView) findViewById(R.id.tv_id);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        adapter.notifyDataSetChanged();
    }



    @Override
    protected void onSetUpView() {
        String head = message.getStringAttribute(EaseConstant.cardOwnerHeadurl,"");
        String id = message.getStringAttribute(EaseConstant.cardOwnerId,"");
        String nick = message.getStringAttribute(EaseConstant.cardOwnerNickname,"");
        Glide.with(context).load(head).into(img_face);

        tv_name.setText(nick);
        tv_id.setText("趣信号:"+id);
    }

}
