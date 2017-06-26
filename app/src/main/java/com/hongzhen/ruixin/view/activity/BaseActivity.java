package com.hongzhen.ruixin.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.main.RXApplication;
import com.hongzhen.ruixin.modle.Constant;
import com.hongzhen.ruixin.utils.ToastUtils;


/**
 * Created by yuhongzhen on 2017/6/7.
 */

public class BaseActivity extends AppCompatActivity {

    private RXApplication mApplication;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/**
 *  所有的Activity都依附于一个Application，在Activity中只要通过 getApplication（）方法，就能拿到当前应用中的Application对象
 *谁继承basesactivity，添加的就是谁
 */
        mApplication = (RXApplication) getApplication();
        mApplication.addActivity(this);
        //获取全局SP对象实例
        mSharedPreferences=getSharedPreferences("confing",MODE_PRIVATE);
        //获取全局提示对话框实例
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);

    }

    /**
     * 全局开启activity封装方法
     * @param clazz
     * @param isFinish
     */
    public void startActivity(Class clazz, boolean isFinish){
        startActivity(new Intent(this,clazz));
        if (isFinish){
            finish();
        }
    }
    public void setBarColor(){
        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            //底部导航栏(针对屏幕内有虚拟按键的手机)
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            View statusBarView = new View(this);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(this));
            statusBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            contentView.addView(statusBarView, lp);
        }
    }
    public static int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取用户名，用户回显UI
     * @return
     */
    public String getUserName(){
        return mSharedPreferences.getString(Constant.SP_USER_NAME,"");
    }
    /**
     * 获取用户密码，用户回显UI
     * @return
     */
    public String getPwd(){
        return mSharedPreferences.getString(Constant.SP_PASSWORD,"");
        /**
         * showDialog
         */
    }
    public void showDialog(String msg){
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();

    }
    public void hideDialog(){
        mProgressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.removeActivity(this);//移除当前activity
        mProgressDialog.dismiss();
    }
    //toast的共有方法
    public void showToast(String msg){
        ToastUtils.showToast(this,msg);
    }
    public void saveUser(String username,String pwd){
        mSharedPreferences.edit()
                .putString(Constant.SP_USER_NAME,username)
                .putString(Constant.SP_PASSWORD,pwd)
                .commit();
    }
}
