package com.hongzhen.ruixin.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.adapter.IAdapterContact;
import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.utils.StringUtils;
import com.hyphenate.util.DensityUtil;

import java.util.List;



/**
 * Created by yuhongzhen on 2017/5/22.
 */

public class SlideBar extends View {
    private static final String[] SECTIONS = {"搜","A","B","C","D","E","F","G","H","I","J"
            ,"K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private Paint mPaint;
    private int mSlideBarX;
    private int mSlideBarY;
    private TextView flostText;
    private RecyclerView recycleView;

    public SlideBar(Context context) {
        this(context,null);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#9c9c9c"));
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(DensityUtil.sp2px(getContext(),10));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        mSlideBarX = measuredWidth/2;
        mSlideBarY = measuredHeight/SECTIONS.length;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i=0;i<SECTIONS.length;i++){
            canvas.drawText(SECTIONS[i],mSlideBarX,mSlideBarY*(i+1),mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setBackgroundResource(R.drawable.slide_bg);
                showFloatAndScrollRecycleView(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundColor(Color.TRANSPARENT);
                if (flostText!=null){
                    flostText.setVisibility(GONE);
                }
                break;
        }
        return true;
    }

    private void showFloatAndScrollRecycleView(float y) {
        /**
         * 根据点击的y的坐标来判断点击了哪个文本
         */
        int index= (int) (y/mSlideBarY);
        if (index<0){
            index=0;
        }else if (index>(SECTIONS.length-1)){
            index=SECTIONS.length-1;
        }
        String section=SECTIONS[index];
        if (flostText==null){
            ViewGroup parent = (ViewGroup) getParent();
            flostText = (TextView) parent.findViewById(R.id.tv_title_center);
            recycleView = (RecyclerView) parent.findViewById(R.id.recycleView);
        }
        flostText.setVisibility(VISIBLE);
        flostText.setText(section);

        RecyclerView.Adapter adapter = recycleView.getAdapter();
        if (adapter instanceof IAdapterContact){
            List<BUser> data = ((IAdapterContact) adapter).getData();
            for (int i=0;i<data.size();i++){
                if (section.equals(StringUtils.getInitial(data.get(i).getNick()))){
                    recycleView.smoothScrollToPosition(i);
                    return;
                }
            }
        }else{
            throw  new RuntimeException("使用SlideBar时绑定的Adapter必须实现IContactAdapter接口");
        }
    }
}
