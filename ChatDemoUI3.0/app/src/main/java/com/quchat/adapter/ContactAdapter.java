package com.quchat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.quchat.R;
import com.quchat.domain.PersonBean;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<PersonBean> persons;
    private Context context;
    public ContactAdapter(Context context,List<PersonBean> persons) {
        this.context = context;
        this.persons = persons;

    }
    public interface OnItemClick{
        public void onItemClick(String uid);
    }
    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick=onItemClick;
    }
    private OnItemClick onItemClick;
    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_adapter, parent, false);
        ContactAdapter.ViewHolder viewHolder = new ContactAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
       final PersonBean cb = persons.get(position);

        int selection = cb.getFirstPinYin().charAt(0);
        // 通过首字母的assii值来判断是否显示字母
        int positionForSelection = getPositionForSelection(selection);
        if (position == positionForSelection) {// 相等说明需要显示字母
            holder.tv_lv_item_tag.setVisibility(View.VISIBLE);
            holder.tv_lv_item_tag.setText(cb.getFirstPinYin());
        } else {
            holder.tv_lv_item_tag.setVisibility(View.GONE);

        }

        holder.tv_lv_item_name.setText(cb.getName());
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.logo);
        Glide.with(context).load(cb.getImgUrl()).apply(options).into(holder.iv_lv_item_head);
//        holder.tv_lv_item_tag.setText(cb.getFirstPinYin());
//        holder.iv_lv_item_head.setImageResource(R.drawable.logo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onItemClick(cb.getUserId());
            }
        });
    }
    public int getPositionForSelection(int selection) {
        for (int i = 0; i < persons.size(); i++) {
            String Fpinyin = persons.get(i).getFirstPinYin();
            char first = Fpinyin.toUpperCase().charAt(0);
            if (first == selection) {
                return i;
            }
        }
        return -1;

    }
    @Override
    public int getItemCount() {
        return persons.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_lv_item_name,tv_lv_item_tag;
        ImageView iv_lv_item_head;


        public ViewHolder(View itemView) {
            super(itemView);
            tv_lv_item_name = itemView.findViewById(R.id.tv_lv_item_name);
            tv_lv_item_tag = itemView.findViewById(R.id.tv_lv_item_tag);

            iv_lv_item_head = itemView.findViewById(R.id.iv_lv_item_head);

        }
    }
}
