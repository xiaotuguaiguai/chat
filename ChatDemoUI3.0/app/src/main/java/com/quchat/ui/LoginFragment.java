package com.quchat.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseBaseFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.DemoApplication;
import com.quchat.DemoHelper;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.db.DemoDBManager;
import com.quchat.domain.AuthBean;
import com.quchat.domain.ContactListBean;
import com.quchat.domain.UserInfoBean;
import com.quchat.utils.JsonUitl;
import com.quchat.utils.MD5;
import com.quchat.utils.SPUtil;
import com.quchat.utils.TempCache;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginActivity";
    public static final int REQUEST_CODE_SETNICK = 1;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private boolean autoLogin = false;
    private Button login_btn;
    private View view;
    private ImageView wx_btn;
    private TextView forget_text,registe_text;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.em_activity_login,null);
        if (DemoHelper.getInstance().isLoggedIn()) {
            autoLogin = true;
            startActivity(new Intent(getActivity(), MainActivity.class));


        }else{
            forget_text= (TextView) view.findViewById(R.id.forget_text);
            registe_text= (TextView) view.findViewById(R.id.registe_text);
            wx_btn= (ImageView) view.findViewById(R.id.wx_btn);
            usernameEditText = (EditText) view.findViewById(R.id.username);
            passwordEditText = (EditText) view.findViewById(R.id.password);
            login_btn=view.findViewById(R.id.login_btn);

            registe_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    register();
                }
            });
            forget_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),"暂未开通",Toast.LENGTH_SHORT).show();

                }
            });
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    auth();
                }
            });
            wx_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),"暂未开通",Toast.LENGTH_SHORT).show();
                }
            });
            // if user changed, clear the password
            usernameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    passwordEditText.setText(null);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))) {
                        login();
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            });

            if (DemoHelper.getInstance().getCurrentUsernName() != null) {
                usernameEditText.setText(DemoHelper.getInstance().getCurrentUsernName());
            }

            TextView serviceCheckTV = (TextView) view.findViewById(R.id.txt_service_ckeck);
            serviceCheckTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }



        return view;
    }


    private ProgressDialog dialog;
    private String currentUsername,currentPassword;
    private void auth(){
        if (!EaseCommonUtils.isNetWorkConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        currentUsername = usernameEditText.getText().toString().trim();
        currentPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(getActivity(), R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(getActivity(), R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = ProgressDialog.show(getActivity(), "", "登录中");
        OkGo.<String>post(URL.AUTH).tag(this)
                .params("grant_type",Constant.grant_type)
                .params("client_id",Constant.client_id)
                .params("client_secret",Constant.client_secret)
                .params("scope",Constant.scope)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        AuthBean bean=   (AuthBean)JsonUitl.stringToObject(response.body().toString(),AuthBean.class);
                        if(null!=bean){
                            Constant.TOKEN=bean.getAccess_token();
                            SPUtil.put(getActivity(),Constant.TOKENKEY,  Constant.TOKEN);
                            EaseConstant.TOKEN=Constant.TOKEN;
                            Constant.USERNAME=currentUsername;
                            EaseConstant.USER_ID = Constant.USERNAME;
                            Log.e("ss",Constant.TOKEN);
                            Log.e("ss",SPUtil.get(getActivity(),Constant.TOKENKEY,"").toString());

                            login();
                            getInfo();
                            getFriendList();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    public void login() {

        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(currentUsername);

        final long start = System.currentTimeMillis();
        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        final  String hashed = MD5.MD5(currentPassword).toLowerCase();

        EMClient.getInstance().login(currentUsername, hashed, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");


                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().pushManager().updatePushNickname(
                        DemoApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                if (!getActivity().isFinishing() && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Constant.USERNAME=currentUsername;
                SPUtil.put(getActivity(),Constant.USERNAMEKEY,  currentUsername);

                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                Intent intent = new Intent(getActivity(),
                        MainActivity.class);
                startActivity(intent);

                getActivity().finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), getString(R.string.Login_failed) ,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void getFriendList(){
        OkGo.<String>get(URL.GETFRIENDLIST).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",Constant.USERNAME)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        ContactListBean userInfoBean=(ContactListBean) JsonUitl.stringToObject(response.body().toString(), ContactListBean.class);
                        if(userInfoBean.getCode()==200){
                            TempCache.setFriendList(userInfoBean.getData());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("sserror",response.body());
                    }
                });
    }
    private void getInfo(){
        OkGo.<String>get(URL.GETINFO).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("username",Constant.USERNAME)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        UserInfoBean userInfoBean=(UserInfoBean) JsonUitl.stringToObject(response.body().toString(), UserInfoBean.class);
                        if(userInfoBean.getCode()==200){
                            TempCache.setUserBean(userInfoBean);
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("sserror",response.body());
                    }
                });
    }



    public void register() {
        ((LoginActivity)getActivity()).setTabSelection(1);
//        startActivityForResult(new Intent(getActivity(), RegisterActivity.class), 0);
    }

    /**
     * SDK service check
     *
     * @param v
     */
    public void serviceCheck(View v) {
        startActivity(new Intent(getActivity(), ServiceCheckActivity.class));
    }



}
