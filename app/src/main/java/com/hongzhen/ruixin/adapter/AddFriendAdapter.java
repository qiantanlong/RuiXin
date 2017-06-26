package com.hongzhen.ruixin.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.bmob.BUser;

import java.util.List;



/**
 * Created by yuhongzhen on 2017/6/2.
 */

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolderAddFriend> {
    private List<BUser> mUserListBMOB;
    private List<BUser> mUserListDB;
    private onItemClickListner mItemClickListner;

    public AddFriendAdapter(List<BUser> listUsersBMOB, List<BUser> listUsersDB) {
        mUserListBMOB = listUsersBMOB;
        mUserListDB = listUsersDB;
    }

    @Override
    public ViewHolderAddFriend onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = View.inflate(parent.getContext(), R.layout.list_item_search, null);
        ViewHolderAddFriend viewHolderAddFriend = new ViewHolderAddFriend(inflate);
        return viewHolderAddFriend;
    }

    @Override
    public void onBindViewHolder(ViewHolderAddFriend holder, int position) {
        holder.mTvUsername.setText(mUserListBMOB.get(position).getUsername());
        holder.mTvNickName.setText(mUserListBMOB.get(position).getNick());
        final String username = mUserListBMOB.get(position).getUsername();
        for (int i=0;i<mUserListDB.size();i++){
            if (mUserListDB.get(i).getUsername().equals(username)){
                holder.mBtnAdd.setText("已经是好友");
                holder.mBtnAdd.setEnabled(false);
            }else{
                holder.mBtnAdd.setText("加为好友");
                holder.mBtnAdd.setEnabled(true);
            }
        }
        holder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListner.onItemClick(username);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUserListBMOB == null ? 0 : mUserListBMOB.size();
    }

    class ViewHolderAddFriend extends RecyclerView.ViewHolder {

        TextView mTvUsername;
        TextView mTvNickName;
        Button mBtnAdd;

        public ViewHolderAddFriend(View itemView) {
            super(itemView);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mTvNickName = (TextView) itemView.findViewById(R.id.tv_nickname);
            mBtnAdd = (Button) itemView.findViewById(R.id.btn_add);
        }
    }
    public void setOnItemClickListner(onItemClickListner listner){
        mItemClickListner=listner;
    }
    public interface onItemClickListner{
        void onItemClick(String username);
    }
}
