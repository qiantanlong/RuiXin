package com.hongzhen.ruixin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.db.RXDbManager;
import com.hongzhen.ruixin.utils.GlideUtils;
import com.hongzhen.ruixin.utils.StringUtils;

import java.util.List;


/**
 * Created by yuhongzhen on 2017/5/23.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolderContact> implements IAdapterContact, View.OnClickListener {

    public static final int HEADER_NEW_FRIEND = 0;
    public static final int HEADER_GROUP = 1;
    private List<BUser> data;
    private Context mContext;
    private onItemClickListner itemClickListner;
    private onItemLongClickListner itemLongClickListner;
    private View mHeadView;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;


    public ContactAdapter(Context context, List<BUser> data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public ViewHolderContact onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new ViewHolderContact(mHeadView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_contact, null);
        WindowManager systemService = (WindowManager) parent.getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        int width = systemService.getDefaultDisplay().getWidth();
        view.setMinimumWidth(width);
        ViewHolderContact viewHolderContact = new ViewHolderContact(view);
        return viewHolderContact;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeadView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public void onBindViewHolder(ViewHolderContact holder, final int position) {
        int id = position - 1;
        if (getItemViewType(position) == TYPE_HEADER) {
            mHeadView.findViewById(R.id.re_newfriends).setOnClickListener(this);
            mHeadView.findViewById(R.id.re_chatroom).setOnClickListener(this);
            return;
        }
        final String contact = data.get(id).getNick();
        final String username = data.get(id).getUsername();
        String initial = StringUtils.getInitial(contact);
        holder.mTvSection.setText(initial);
        if (position == 1) {
            holder.mTvSection.setVisibility(View.VISIBLE);
        } else {
            String preContact = data.get(id - 1).getNick();
            BUser contact1 = RXDbManager.getInstance().getContact(username);
            if (contact1 != null) {
                GlideUtils.setImageToImageView(mContext, holder.mIvAvatar, contact1.getAvatar());
            }
            String initialPre = StringUtils.getInitial(preContact);
            if (preContact.equals(initial)) {
                holder.mTvSection.setVisibility(View.GONE);
            } else {
                holder.mTvSection.setVisibility(View.VISIBLE);
            }

        }
        holder.mTvUsername.setText(data.get(id).getNick() + "(" + data.get(id).getUsername() + ")");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListner.onItemClick(username, contact, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemLongClickListner.onItemLongClick(username, contact, position);
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_newfriends:
                mOnHeaderViewClickListener.onHeaderViewClick(HEADER_NEW_FRIEND);
                break;
            case R.id.re_chatroom:
                mOnHeaderViewClickListener.onHeaderViewClick(HEADER_GROUP);
                break;

        }
    }

    public interface onHeaderViewClickListener {
        void onHeaderViewClick(int id);
    }

    private onHeaderViewClickListener mOnHeaderViewClickListener;

    public void setOnHeaderViewClickListener(onHeaderViewClickListener listener) {
        this.mOnHeaderViewClickListener = listener;

    }

    public interface onItemClickListner {
        void onItemClick(String username, String nickname, int position);
    }

    public interface onItemLongClickListner {
        void onItemLongClick(String username, String nickname, int position);
    }

    public void setOnItemClickListner(onItemClickListner listner) {
        this.itemClickListner = listner;
    }

    public void setOnItemLongClickListner(onItemLongClickListner listner) {
        this.itemLongClickListner = listner;
    }

    @Override
    public int getItemCount() {
        return data == null ? 1 : data.size() + 1;
    }

    @Override
    public List<BUser> getData() {
        return data;
    }

    class ViewHolderContact extends RecyclerView.ViewHolder {
        TextView mTvSection;
        TextView mTvUsername;
        private final ImageView mIvAvatar;

        public ViewHolderContact(View itemView) {
            super(itemView);
            mTvSection = (TextView) itemView.findViewById(R.id.tv_section);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }

    public void addHeadView(View view) {
        mHeadView = view;
    }

}