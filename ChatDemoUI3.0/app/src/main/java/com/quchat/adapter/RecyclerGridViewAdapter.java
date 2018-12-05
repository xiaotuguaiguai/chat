package com.quchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.quchat.R;

import java.util.List;

public class RecyclerGridViewAdapter extends RecyclerView.Adapter<RecyclerGridViewAdapter.ViewHolder> {
    private Context mContext;
    private List<String> imgdata;
    private LayoutInflater inf;

    public  interface OnRecyclerViewItemListener {
        public void onItemClickListener(View view, int position);
        public void onItemCancelClickListener(View view, int position);

    }

    private RecyclerGridViewAdapter.OnRecyclerViewItemListener mOnRecyclerViewItemListener;

    public void setOnRecyclerViewItemListener(RecyclerGridViewAdapter.OnRecyclerViewItemListener listener) {
        mOnRecyclerViewItemListener = listener;
    }

    public RecyclerGridViewAdapter(Context mContext,  List<String> imgdata) {
        this.mContext = mContext;
        this.imgdata = imgdata;
        inf = LayoutInflater.from(mContext);

    }

    @Override
    public RecyclerGridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inf.inflate(R.layout.layout_commite_grid, viewGroup, false);

        return new RecyclerGridViewAdapter.ViewHolder(v);
    }

    //将数据绑定到子View，会自动复用View
    @Override
    public void onBindViewHolder(RecyclerGridViewAdapter.ViewHolder viewHolder, int i) {
        if (viewHolder == null) {
            return;
        }
        if(i==imgdata.size()){
            viewHolder.icon_pic_choose.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.icon_choose_close.setVisibility(View.GONE);
            itemOnClick( viewHolder.icon_pic_choose,i);
        }else{
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.icon_pic_choose.setVisibility(View.GONE);
            viewHolder.icon_choose_close.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(imgdata.get(i)).into(viewHolder.imageView);
            itemOnClick( viewHolder.imageView,i);
        }
        if (mOnRecyclerViewItemListener != null) {

            itemOnLongClick(viewHolder);
            cancelClick( viewHolder.icon_choose_close,i);
        }

    }

    //RecyclerView显示数据条数
    @Override
    public int getItemCount() {
        if(imgdata.size()<9){
            return imgdata.size()+1;
        }
        return imgdata.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView,icon_pic_choose,icon_choose_close;
        RelativeLayout choose_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            choose_layout= (RelativeLayout) itemView.findViewById(R.id.choose_layout);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            icon_pic_choose= (ImageView) itemView.findViewById(R.id.icon_pic_choose);
            icon_choose_close= (ImageView) itemView.findViewById(R.id.icon_choose_close);
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

