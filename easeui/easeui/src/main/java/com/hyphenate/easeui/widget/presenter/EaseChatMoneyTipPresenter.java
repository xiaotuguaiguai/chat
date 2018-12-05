package com.hyphenate.easeui.widget.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.CodeAndMsg;
import com.hyphenate.easeui.utils.JsonUitl;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowMoney;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowMoneyTip;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatMoneyTipPresenter extends EaseChatRowPresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowMoneyTip(cxt, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(final EMMessage message) {
        super.handleReceiveMessage(message);

        getChatRow().updateView(message);

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                getChatRow().updateView(message);
            }

            @Override
            public void onError(int code, String error) {
                getChatRow().updateView(message);
            }

            @Override
            public void onProgress(int progress, String status) {
                getChatRow().updateView(message);
            }
        });
    }

    @Override
    public void onBubbleClick(EMMessage message) {



    }

}
