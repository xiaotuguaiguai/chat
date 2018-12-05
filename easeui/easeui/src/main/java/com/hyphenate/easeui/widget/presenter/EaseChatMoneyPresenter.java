package com.hyphenate.easeui.widget.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.CodeAndMsg;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseRedPackageActivity;
import com.hyphenate.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easeui.utils.JsonUitl;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowImage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowMoney;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatMoneyPresenter extends EaseChatRowPresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowMoney(cxt, message, position, adapter);
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



        if(message.direct() == EMMessage.Direct.RECEIVE ){
            getMoney( message);
        }else{
            Toast.makeText(getChatRow().getContext(),"不能领取自己发的红包",Toast.LENGTH_SHORT).show();

        }
        Log.e("ss","onBubbleClick");

    }

    private void getMoney(final EMMessage message){
        Log.e("ss",message.getStringAttribute("msg_key",""));
        OkGo.<String>post("http://114.116.66.158:8081/api/red_packet/recevie").tag(this)
                .headers("Authorization", "Bearer " + EaseConstant.TOKEN)
                .params("username",EaseConstant.USER_ID)

                .params("msg_key",message.getStringAttribute("msg_key",message.getStringAttribute("msg_key","")))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        Log.e("ss",response.body().toString());
                        CodeAndMsg userInfoBean=(CodeAndMsg) JsonUitl.stringToObject(response.body().toString(), CodeAndMsg.class);
                        if(userInfoBean.getCode()==200){
                            Toast.makeText(getChatRow().getContext(),"红包已收取",Toast.LENGTH_SHORT).show();
                            message.setAttribute("isReceive",true);
                            EMClient.getInstance().chatManager().updateMessage(message);
//                            message.setAttribute("isReceive",true);
                            if(message.direct() == EMMessage.Direct.RECEIVE){

                                EMMessage message1 = EMMessage.createTxtSendMessage("红包已收取", message.getFrom());
                                message1.setAttribute(EaseConstant.IS_MONEY_TIP,true);
                                message1.setAttribute("updateMoneyState",message.getMsgId());
                                EMClient.getInstance().chatManager().sendMessage(message1);
                                getChatRow().updateView(message1);

                            }
                        }else{
                            Toast.makeText(getChatRow().getContext(),userInfoBean.getMsg(),Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
}
