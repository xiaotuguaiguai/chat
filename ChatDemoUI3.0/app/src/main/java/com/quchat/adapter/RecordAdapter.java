package com.quchat.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.quchat.Constant;
import com.quchat.R;
import com.quchat.URL;
import com.quchat.domain.CircleCommonBean;
import com.quchat.domain.CodeAndMsg;
import com.quchat.domain.FriendCircleData;
import com.quchat.domain.RecordBean;
import com.quchat.domain.RecordItemBean;
import com.quchat.utils.JsonUitl;

import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<RecordItemBean.MyData> persons;
    private Context context;
    public RecordAdapter(Context context, List<RecordItemBean.MyData> persons) {
        this.context=context;
        this.persons = persons;

    }
    public void setList(List<RecordItemBean.MyData> persons){
        this.persons = persons;
    }
    public interface  CallBack{
        public void loadMore();
    }
    private CallBack callBack;
    public void setCallBack(CallBack callBack){
        this.callBack=  callBack;
    }
    @NonNull
    @Override
    public RecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_record_item, parent, false);
        RecordAdapter.ViewHolder viewHolder = new RecordAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.ViewHolder holder, final int position) {
        final RecordItemBean.MyData data = persons.get(position);

        if(data.getType()==1 ){
            holder.item_type.setText("发红包");
            holder.item_money.setText("-"+data.getMoney());
        }else if(data.getType()==2 ){
            holder.item_type.setText("收红包");
            holder.item_money.setText("+"+data.getMoney());
        }else if(data.getType()==3 ){
            holder.item_type.setText("充值");
            holder.item_money.setText("+"+data.getMoney());
        }else if(data.getType()==4 ){
            holder.item_type.setText("提现");
            holder.item_money.setText("-"+data.getMoney());
        }

        holder.item_time.setText(data.getTime());
//        if(position == getItemCount()-1){//已经到达列表的底部
//            callBack.loadMore();
//        }
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_type,item_time,item_money;
        ImageView item_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            item_type = itemView.findViewById(R.id.item_type);
            item_time= itemView.findViewById(R.id.item_time);
            item_money= itemView.findViewById(R.id.item_money);
            item_icon= itemView.findViewById(R.id.item_icon);

        }
    }
}
