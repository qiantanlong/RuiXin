package com.hongzhen.ruixin.view.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongzhen.ruixin.R;


/**
 * Created by yuhongzhen on 2017/5/22.
 */

public class ContactLayout extends RelativeLayout {
    private RecyclerView recyclerView;
    private TextView textView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SlideBar slideBar;

    public ContactLayout(Context context) {
        this(context,null);
    }

    public ContactLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.contact_layout, this);
        textView = (TextView) inflate.findViewById(R.id.tv_title_center);
        recyclerView= (RecyclerView) inflate.findViewById(R.id.recycleView);
        swipeRefreshLayout= (SwipeRefreshLayout) inflate.findViewById(R.id.swipeRefreshLayout);
        slideBar= (SlideBar) inflate.findViewById(R.id.sliderBar);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorActive),getResources().getColor(R.color.colorAccent));

    }

    /**
     * 代理模式，ContactLayout代理RecycleView实现setAdapter的功能
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener){
        swipeRefreshLayout.setOnRefreshListener(listener);
    }
    public void setRefreshing(boolean refreshing){

        swipeRefreshLayout.setRefreshing(refreshing);
    }

}
