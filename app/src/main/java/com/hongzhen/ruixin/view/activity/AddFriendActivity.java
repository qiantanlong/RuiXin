package com.hongzhen.ruixin.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.adapter.AddFriendAdapter;
import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.presenter.AddFriendPresenter;
import com.hongzhen.ruixin.presenter.impl.AddFriendPresenterImpl;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by yuhongzhen on 2017/6/2.
 */

public class AddFriendActivity extends BaseActivity implements AddFriendView {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_user)
    TextView mTvNoUser;


    private AddFriendPresenter mAddFriendPresenter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarColor();//设置状态栏颜色
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//返回按钮可见
        mAddFriendPresenter = new AddFriendPresenterImpl(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        mSearchView = (SearchView) item.getActionView();
        mSearchView.setQueryHint("输入手机号");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAddFriendPresenter.onSearchUser(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    mTvNoUser.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }else {

                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onSearchUserResult(List<BUser> listUsersBMOB, List<BUser> listUsersDB) {
        if (listUsersBMOB.size() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            AddFriendAdapter adapterAddFriend = new AddFriendAdapter(listUsersBMOB, listUsersDB);
            adapterAddFriend.setOnItemClickListner(new AddFriendAdapter.onItemClickListner() {
                @Override
                public void onItemClick(String username) {
                    mAddFriendPresenter.onAddFriend(username);
                    LogUtils.i(username);
                }
            });
            recyclerView.setAdapter(adapterAddFriend);
            mTvNoUser.setVisibility(View.GONE);
        } else {
            mTvNoUser.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }


    }

    @Override
    public void onAddFriendResult(String userName, boolean success, String msg) {
        ToastUtils.showToast(this,"添加好友"+userName+success);
    }
}
