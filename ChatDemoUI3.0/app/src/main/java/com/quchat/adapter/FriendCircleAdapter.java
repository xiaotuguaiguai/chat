package com.quchat.adapter;

import android.app.ProgressDialog;
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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.quchat.utils.DateTimerUtils;
import com.quchat.utils.JsonUitl;
import com.quchat.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class FriendCircleAdapter extends RecyclerView.Adapter<FriendCircleAdapter.ViewHolder> {

    private List<FriendCircleData> persons;
    private Context context;
    public FriendCircleAdapter(Context context,List<FriendCircleData> persons) {
        this.context=context;
        this.persons = persons;

    }
    public void setList(List<FriendCircleData> persons){
        this.persons = persons;
    }
    public   interface  CallBack{
        public void comment(int position,int mid,String toId);
        public void refresh();
        public void showSendLayout(FriendCircleData data,int position);
        public void showSendLayoutByItem(int position1,int position2,CircleCommonBean data);

    }
   private CallBack callBack;
    public void  setInterface(CallBack callBack){
        this.callBack=callBack;
    }
    @NonNull
    @Override
    public FriendCircleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_friendcircle, parent, false);
        FriendCircleAdapter.ViewHolder viewHolder = new FriendCircleAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final FriendCircleAdapter.ViewHolder holder,final int position) {
        final FriendCircleData data = persons.get(position);

        holder.item_name.setText(data.getNick_name());
        if(Constant.USERNAME.equals(data.getUser_name())){
            holder.delete_text.setVisibility(View.VISIBLE);
        }else{
            holder.delete_text.setVisibility(View.GONE);
        }
        holder.delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ss","删除");

                showDialog2(position,data.getId());
            }
        });
        if(TextUtils.isEmpty(data.getContent())){
            holder.item_content.setVisibility(View.GONE);
        }else{
            holder.item_content.setVisibility(View.VISIBLE);
            holder.item_content.setText(data.getContent());
        }

        holder.zan_num.setText(data.getLike_num()+"人赞"+" . ");
        holder.review_num.setText(data.getComment_num()+"条评论");

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.logo);
        Glide.with(context).load(data.getHead_url()).apply(options).into(holder.item_face);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,3);

        holder.recyclerView.setLayoutManager(gridLayoutManager);
        FriendGridViewAdapter recyclerGridViewAdapter = new FriendGridViewAdapter(context,(ArrayList<String>) data.getPic());
        holder.recyclerView.setAdapter(recyclerGridViewAdapter);
        List<CircleCommonBean> comments =data.getComments();
        if(null!=comments && comments.size()!=0){
            holder.item_comment.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

            holder.item_comment.setLayoutManager(linearLayoutManager);
            FriendCircleCommentAdapter friendCircleCommentAdapter = new FriendCircleCommentAdapter(context,data.getComments());
            holder.item_comment.setAdapter(friendCircleCommentAdapter);
            friendCircleCommentAdapter.setOnItemClick(new FriendCircleCommentAdapter.ItemClick() {
                @Override
                public void onItemClick(int position2, CircleCommonBean data) {
                    callBack.showSendLayoutByItem(position,position2,data);
                }

                @Override
                public void onItemLongClick(int pos) {
                    if(Constant.USERNAME.equals(data.getComments().get(pos).getUser_name())){
                        showDialog(position,pos);
                    }

//                    delteComments(position,pos);

                }
            });
        }else{
            holder.item_comment.setVisibility(View.GONE);
        }

        holder.img_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.showSendLayout(data,position);
            }
        });

        if(data.getIs_like()!=null){
            holder.img_zan.setImageResource(R.drawable.icon_zan_done);
        }else{
            holder.img_zan.setImageResource(R.drawable.icon_zan);
        }
        holder.img_zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.getIs_like()!=null){
                    zan(data.getId()+"",holder.img_zan,1,data,position,holder.zan_num);
                }else {
                    zan(data.getId()+"",holder.img_zan,0,data,position,holder.zan_num);
                }
//
//                callBack.showSendLayout(data,position);
            }
        });
        holder.time_text.setText(data.getCreated_at());
    }



    private void zan(String moment_id, final ImageView imageView, final int type, FriendCircleData data, final int posotion,final TextView textview){
        String url;
        if(type==0){
            url = URL.ZAN;
        }else{
            url = URL.ZAN_CANCEL;
        }
        OkGo.<String>post(url).tag(this)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("user_name",Constant.USERNAME)
                .params("moment_id",moment_id)


                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body().toString());
                        CodeAndMsg bean = (CodeAndMsg)JsonUitl.stringToObject(response.body(),CodeAndMsg.class);


                        if(bean.getCode()==200){
                            String s = persons.get(posotion).getIs_like();
                            if(type==0){
                                imageView.setImageResource(R.drawable.icon_zan_done);
                                persons.get(posotion).setIs_like("1");
                              if(TextUtils.isEmpty(s)){
                                  persons.get(posotion).setLike_num("1");
                                  textview.setText(1+"人赞"+" . ");

                              }else{
                                  persons.get(posotion).setLike_num((Integer.parseInt(s)+1)+"");
                                  textview.setText((Integer.parseInt(s)+1)+"人赞"+" . ");
                              }

                            }else{
                                persons.get(posotion).setLike_num((Integer.parseInt(s)-1)+"");
                                textview.setText((Integer.parseInt(s)-1)+"人赞"+" . ");
                                persons.get(posotion).setIs_like(null);
                                imageView.setImageResource(R.drawable.icon_zan);

                            }


                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private void showDialog(final int index1,final int index2){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
                delteComments(index1,index2);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void showDialog2(final int index1,final int index2){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
                delte(index1,index2);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_name,item_content,time_text,delete_text,zan_num,review_num;
        ImageView img_zan,img_comment;
        RecyclerView recyclerView,item_comment;
        CircleImageView item_face;
        public ViewHolder(View itemView) {
            super(itemView);
            zan_num= itemView.findViewById(R.id.zan_num);
            review_num= itemView.findViewById(R.id.review_num);
            item_name = itemView.findViewById(R.id.item_name);
            time_text= itemView.findViewById(R.id.time_text);
            delete_text= itemView.findViewById(R.id.delete_text);
            item_content = itemView.findViewById(R.id.item_content);
            item_face = itemView.findViewById(R.id.item_face);
            recyclerView = itemView.findViewById(R.id.item_grid);
            item_comment= itemView.findViewById(R.id.item_comment);
            img_zan= itemView.findViewById(R.id.img_zan);
            img_comment= itemView.findViewById(R.id.img_comment);
        }
    }
    private void delteComments(final int index1,final int index2){

        OkGo.<String>post(URL.CIRCLE_DELETE_COMMENT).tag(context)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("user_name",Constant.USERNAME)
                .params("comment_id",persons.get(index1).getComments().get(index2).getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        CodeAndMsg bean = (CodeAndMsg)JsonUitl.stringToObject(response.body(),CodeAndMsg.class);
                        if(bean.getCode()==200){
                            persons.get(index1).getComments().remove(index2);
                            callBack.refresh();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
    private void delte(final int index,int id){

        OkGo.<String>post(URL.CIRCLE_DELETE).tag(context)
                .headers("Authorization", "Bearer " + Constant.TOKEN)
                .params("user_name",Constant.USERNAME)
                .params("moment_id",id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        CodeAndMsg bean = (CodeAndMsg)JsonUitl.stringToObject(response.body(),CodeAndMsg.class);
                        if(bean.getCode()==200){
                            persons.remove(index);
                            callBack.refresh();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
}
