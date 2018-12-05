package com.quchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.ui.MyRechargeActivity;
import com.quchat.R;
import com.quchat.ui.MyRecordActivity;
import com.quchat.ui.MyWalletActivity;
import com.quchat.ui.MyWithDrawActivity;
import com.quchat.ui.RechargeActivity;

import java.util.ArrayList;
import java.util.List;

public class MeWalletGridViewAdapter extends RecyclerView.Adapter<MeWalletGridViewAdapter.ViewHolder> {
    private Context mContext;
    private String[] textdata= new String[]{"充值","提现"};
    private int[] imgdata= new int[]{R.drawable.icon_in_money,R.drawable.icon_out_money};

    private LayoutInflater inf;

    public  interface OnRecyclerViewItemListener {
        public void onItemClickListener(View view, int position);

    }

    private MeWalletGridViewAdapter.OnRecyclerViewItemListener mOnRecyclerViewItemListener;

    public void setOnRecyclerViewItemListener(MeWalletGridViewAdapter.OnRecyclerViewItemListener listener) {
        mOnRecyclerViewItemListener = listener;
    }

    public MeWalletGridViewAdapter(Context mContext) {
        this.mContext = mContext;
        this.imgdata = imgdata;
        inf = LayoutInflater.from(mContext);

    }

    @Override
    public MeWalletGridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inf.inflate(R.layout.activity_wallet_item, viewGroup, false);

        return new MeWalletGridViewAdapter.ViewHolder(v);
    }

    //将数据绑定到子View，会自动复用View
    @Override
    public void onBindViewHolder(MeWalletGridViewAdapter.ViewHolder viewHolder, int i) {
        if (viewHolder == null) {
            return;
        }

        Glide.with(mContext).load(imgdata[i]).into(viewHolder.item_img);
        viewHolder.item_text.setText(textdata[i]);
//
//        if (mOnRecyclerViewItemListener != null) {
            itemOnClick(viewHolder);

//        }

    }

    //RecyclerView显示数据条数
    @Override
    public int getItemCount() {

        return imgdata.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView item_img;
        TextView item_text;
        public ViewHolder(View itemView) {
            super(itemView);
            item_img = itemView.findViewById(R.id.item_img);
            item_text =  itemView.findViewById(R.id.item_text);
        }
    }
    //单机事件
    private void itemOnClick(final RecyclerView.ViewHolder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition();
                if(position==0){
                    mContext.startActivity(new Intent(mContext,RechargeActivity.class));

                }else if(position==1){
                    mContext.startActivity(new Intent(mContext,MyWithDrawActivity.class));
                }
//                else if(position==2){
//                    mContext.startActivity(new Intent(mContext,MyRecordActivity.class));
//                }
//                mOnRecyclerViewItemListener.onItemClickListener(holder.itemView, position);
            }
        });
    }


}

