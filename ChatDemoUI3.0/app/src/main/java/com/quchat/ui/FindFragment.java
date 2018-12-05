package com.quchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.quchat.R;
import com.quchat.utils.TempCache;
import com.quchat.widget.CustomRow;
import com.yzq.zxinglibrary.android.CaptureActivity;


public class FindFragment extends Fragment implements View.OnClickListener {
    private View view;
    private CustomRow scan,friend,game,code;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        scan=view.findViewById(R.id.scan);
        friend=view.findViewById(R.id.friend);
        game=view.findViewById(R.id.game);
        code=view.findViewById(R.id.code);
        game.setOnClickListener(this);
        scan.setOnClickListener(this);
        friend.setOnClickListener(this);
        code.setOnClickListener(this);
        return view;

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scan:
                Intent intent2 = new Intent(getActivity(), CaptureActivity.class);
                getActivity().startActivityForResult(intent2, 111);
                break;
            case R.id.friend:
                startActivity(new Intent(getActivity(),FriendCircleActivity.class)
                        .putExtra("from",0)
                );
                break;
            case R.id.game:
                Toast.makeText(getActivity(),"暂未开通",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getActivity(),AddContactActivity.class));

                break;
            case R.id.code:
                startActivity(new Intent(getActivity(),MyQrCodeActivity.class)
                        .putExtra("nick_name",TempCache.UserBean.getData().getNick_name())
                        .putExtra("headUrl",TempCache.UserBean.getData().getHead_url())
                );
                break;
        }
    }



}
