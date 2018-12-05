package com.quchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.quchat.R;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPreview;

public class FriendGridViewAdapter extends RecyclerView.Adapter<FriendGridViewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> imgdata;
    private LayoutInflater inf;


    public FriendGridViewAdapter(Context mContext, ArrayList<String> imgdata) {
        this.mContext = mContext;
        if(imgdata==null){
            this.imgdata=new ArrayList<>();
        }else{
            this.imgdata = imgdata;
        }

        inf = LayoutInflater.from(mContext);

    }

    @Override
    public FriendGridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inf.inflate(R.layout.layout_commite_grid, viewGroup, false);

        return new FriendGridViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FriendGridViewAdapter.ViewHolder viewHolder, final int i) {
        if (viewHolder == null) {
            return;
        }
        Glide.with(mContext).load(imgdata.get(i)).into(viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBig(i);
            }
        });
    }
    private void showBig(int position){
        PhotoPreview.builder()
                .setPhotos(imgdata)
                .setCurrentItem(position)
                .setShowDeleteButton(false)
                .start((Activity) mContext);
    }
    //RecyclerView显示数据条数
    @Override
    public int getItemCount() {

        return imgdata.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
    //单机事件
    private void itemOnClick(final RecyclerView.ViewHolder holder) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition();
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

