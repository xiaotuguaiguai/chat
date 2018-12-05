package com.quchat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.DemoHelper;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.domain.AuthBean;
import com.quchat.domain.CodeAndMsg;
import com.quchat.utils.JsonUitl;

public class RegisteFragment extends Fragment {
   private View view;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText confirmPwdEditText;
    private Button registe;
    private ImageView wx_registe_btn;
    private TextView getCheckCode;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.em_activity_register,null);
        userNameEditText = (EditText) view.findViewById(R.id.username);
        passwordEditText = (EditText) view.findViewById(R.id.password);
        wx_registe_btn= (ImageView) view.findViewById(R.id.wx_registe_btn);
        confirmPwdEditText = (EditText) view.findViewById(R.id.confirm_password);
        getCheckCode= (TextView) view.findViewById(R.id.getCheckCode);
        registe=view.findViewById(R.id.registe);
        wx_registe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"暂未开通",Toast.LENGTH_SHORT).show();
            }
        });
        getCheckCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCheckCode();
            }
        });
        registe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth();
            }
        });
        return view;
    }

    private void getCheckCode(){
      final  String s = userNameEditText.getText().toString();
        if(TextUtils.isEmpty(s) || s.length()!=11){
            Toast.makeText(getActivity(),"请输入正确手机号",Toast.LENGTH_SHORT).show();
            return;
        }
        initTimer();
        getCheckCode.setEnabled(false);
        progressDialog = ProgressDialog.show(getActivity(),"","获取中");


        OkGo.<String>post(URL.AUTH).tag(this)
                .params("grant_type", Constant.grant_type)
                .params("client_id", Constant.client_id)
                .params("client_secret", Constant.client_secret)
                .params("scope", Constant.scope)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        AuthBean bean = (AuthBean) JsonUitl.stringToObject(response.body().toString(), AuthBean.class);
                        if (null != bean) {
                            Constant.TOKEN = bean.getAccess_token();
                            Log.e("ss", Constant.TOKEN);

                            getVerify(s);
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        progressDialog.dismiss();
                        Log.e("sserror", response.body());
                    }
                });


    }


    private void getVerify(String name){
        OkGo.<String>post(URL.REGIST_CHECKCODE).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("user_name", name)
                .params("code_type", "1")

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        progressDialog.dismiss();
                        CodeAndMsg bean = (CodeAndMsg) JsonUitl.stringToObject(response.body().toString(), CodeAndMsg.class);
                        if (null != bean && bean.getCode()==200) {
                            Toast.makeText(getActivity(),"验证短信已下发",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Toast.makeText(getActivity(),"验证短信下发失败，请重试",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }
    private  CountDownTimer timer;
    private void initTimer() {
        if(null==timer){
            timer = new CountDownTimer(60500,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    getCheckCode.setText((millisUntilFinished/1000)+"s");
                }

                @Override
                public void onFinish() {
                    getCheckCode.setEnabled(true);
                    getCheckCode.setText("获取验证码");
                }
            };
        }

        if(null !=timer){
            timer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null !=timer){
            timer.cancel();
        }
    }

    private  String username,pwd;
    private ProgressDialog progressDialog;
    private void auth() {
        username = userNameEditText.getText().toString().trim();
        pwd = passwordEditText.getText().toString().trim();
        String confirm_pwd = confirmPwdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            confirmPwdEditText.requestFocus();
            return;
        }
        progressDialog = ProgressDialog.show(getActivity(),"","注册中");



            OkGo.<String>post(URL.AUTH).tag(this)
                    .params("grant_type", Constant.grant_type)
                    .params("client_id", Constant.client_id)
                    .params("client_secret", Constant.client_secret)
                    .params("scope", Constant.scope)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            AuthBean bean = (AuthBean) JsonUitl.stringToObject(response.body().toString(), AuthBean.class);
                            if (null != bean) {
                                Constant.TOKEN = bean.getAccess_token();
                                Log.e("ss", Constant.TOKEN);

                                goRegiste();
                            }

                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            progressDialog.dismiss();
                            Log.e("sserror", response.body());
                        }
                    });


    }





    private void goRegiste() {


        OkGo.<String>post(URL.REGISTE).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("mobile_phone", userNameEditText.getText().toString())
                .params("user_password", confirmPwdEditText.getText().toString())
                .params("verify_code", passwordEditText.getText().toString())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss", response.body());
                        progressDialog.dismiss();
                        CodeAndMsg bean = (CodeAndMsg) JsonUitl.stringToObject(response.body().toString(), CodeAndMsg.class);
                        if (null != bean) {
                            if (bean.getCode() == 200) {
                                Toast.makeText(getActivity(), "注册成功,请登录", Toast.LENGTH_SHORT).show();
                                ((LoginActivity)getActivity()).setTabSelection(0);
//                                getActivity().finish();
//                               login();
                            }else{
                                Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();

                            }

                        }else{
                            Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        progressDialog.dismiss();
                        Log.e("sserror", response.body());
                    }
                });
    }



    public void register() {


        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setMessage(getResources().getString(R.string.Is_the_registered));
            pd.show();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        // call method in SDK
                        EMClient.getInstance().createAccount(username, pwd);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                if (!getActivity().isFinishing())
                                    pd.dismiss();
                                // save current user
                                DemoHelper.getInstance().setCurrentUserName(username);
                                Toast.makeText(getActivity(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        });
                    } catch (final HyphenateException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                if (!getActivity().isFinishing())
                                    pd.dismiss();
                                int errorCode=e.getErrorCode();
                                if(errorCode==EMError.NETWORK_ERROR){
                                    Toast.makeText(getActivity(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.USER_ALREADY_EXIST){
                                    Toast.makeText(getActivity(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
                                    Toast.makeText(getActivity(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
                                    Toast.makeText(getActivity(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
                                }else if(errorCode == EMError.EXCEED_SERVICE_LIMIT){
                                    Toast.makeText(getActivity(), getResources().getString(R.string.register_exceed_service_limit), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }).start();

        }
    }


}
