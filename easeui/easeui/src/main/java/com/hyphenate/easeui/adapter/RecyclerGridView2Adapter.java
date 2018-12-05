package com.hyphenate.easeui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.UserInfoBean;


import java.util.List;

public class RecyclerGridView2Adapter extends RecyclerView.Adapter<RecyclerGridView2Adapter.ViewHolder> {
    private Context mContext;
    private List<UserInfoBean> imgdata;
    private LayoutInflater inf;

    public  interface OnRecyclerViewItemListener {
        public void onItemClickListener(View view, int position);
        public void onItemCancelClickListener(View view, int position);

    }

    private RecyclerGridView2Adapter.OnRecyclerViewItemListener mOnRecyclerViewItemListener;

    public void setOnRecyclerViewItemListener(RecyclerGridView2Adapter.OnRecyclerViewItemListener listener) {
        mOnRecyclerViewItemListener = listener;
    }

    public RecyclerGridView2Adapter(Context mContext, List<UserInfoBean> imgdata) {
        this.mContext = mContext;
        this.imgdata = imgdata;
        inf = LayoutInflater.from(mContext);

    }

    @Override
    public RecyclerGridView2Adapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inf.inflate(R.layout.layout_commite_grid1, viewGroup, false);

        return new RecyclerGridView2Adapter.ViewHolder(v);
    }

    //将数据绑定到子View，会自动复用View
    @Override
    public void onBindViewHolder(RecyclerGridView2Adapter.ViewHolder viewHolder, int i) {
        if (viewHolder == null) {
            return;
        }
        if(i==imgdata.size()){
            viewHolder.imageView.setImageResource(R.drawable.icon_group_add);
            viewHolder.namess.setText("添加");
        }else{
            RequestOptions options = new RequestOptions();

            options.placeholder(R.drawable.ease_default_avatar);
            Glide.with(mContext).load(imgdata.get(i).getData().getHead_url()).apply(options).into(viewHolder.imageView);
            viewHolder.namess.setText(imgdata.get(i).getData().getNick_name());
        }
        itemOnClick( viewHolder.imageView,i);

    }

    //RecyclerView显示数据条数
    @Override
    public int getItemCount() {

            return imgdata.size()+1;

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView namess;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imagess);
            namess= (TextView) itemView.findViewById(R.id.namess);
        }
    }
    //单机事件
    private void itemOnClick(final ImageView imageView,final int position) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnRecyclerViewItemListener.onItemClickListener(imageView, position);
            }
        });

    }
    private void cancelClick(final ImageView icon_choose_close,final int position){
        icon_choose_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnRecyclerViewItemListener.onItemCancelClickListener(icon_choose_close, position);

            }
        });

    }
    //长按事件
    private void itemOnLongClick(final RecyclerView.ViewHolder holder) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getLayoutPosition();
                //返回true是为了防止触发onClick事件
                return true;
            }
        });
    }

}

