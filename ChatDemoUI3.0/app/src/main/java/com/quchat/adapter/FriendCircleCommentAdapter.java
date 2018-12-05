package com.quchat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.quchat.R;
import com.quchat.domain.CircleCommonBean;
import com.quchat.domain.FriendCircleData;

import org.w3c.dom.Text;

import java.util.List;

public class FriendCircleCommentAdapter extends RecyclerView.Adapter<FriendCircleCommentAdapter.ViewHolder> {

    private List<CircleCommonBean> persons;
    private Context context;
    public FriendCircleCommentAdapter(Context context, List<CircleCommonBean> persons) {
        this.context=context;
        this.persons = persons;

    }
    public interface ItemClick{
        public void onItemClick(int position,CircleCommonBean data );
        public void onItemLongClick(int position );
    }
    private   ItemClick itemClick;
    public void  setOnItemClick(ItemClick itemClick){
        this.itemClick=itemClick;
    }
    @NonNull
    @Override
    public FriendCircleCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_comment, parent, false);
        FriendCircleCommentAdapter.ViewHolder viewHolder = new FriendCircleCommentAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull FriendCircleCommentAdapter.ViewHolder holder, final int position) {
        final CircleCommonBean data = persons.get(position);

        holder.reply1.setText(data.getNick_name());
        holder.replyContent.setText(":"+data.getContent());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onItemClick(position,data);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemClick.onItemLongClick(position);
                return false;
            }
        });
        if(!TextUtils.isEmpty(data.getReply_user_nickname()))
        {
            holder.replytip.setVisibility(View.VISIBLE);
            holder.reply2.setVisibility(View.VISIBLE);
            holder.reply2.setText(data.getReply_user_nickname());
        }else{
            holder.replytip.setVisibility(View.GONE);
            holder.reply2.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView reply1,reply2,replytip,replyContent;


        public ViewHolder(View itemView) {
            super(itemView);
            reply1 = itemView.findViewById(R.id.reply1);
            reply2= itemView.findViewById(R.id.reply2);
            replytip= itemView.findViewById(R.id.replytip);
            replyContent= itemView.findViewById(R.id.replyContent);


        }
    }
}
