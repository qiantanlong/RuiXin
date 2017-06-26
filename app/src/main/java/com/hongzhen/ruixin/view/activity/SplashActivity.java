package com.hongzhen.ruixin.view.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.listener.MyAnimatorListener;
import com.hongzhen.ruixin.main.MainActivity;
import com.hongzhen.ruixin.presenter.SplashPresenter;
import com.hongzhen.ruixin.presenter.impl.SplashPresenterimpl;


public class SplashActivity extends BaseActivity implements SplashView{

    private ImageView ivSplash;
    private SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ivSplash = (ImageView) findViewById(R.id.iv_splash);
        mSplashPresenter=new SplashPresenterimpl(this);
        mSplashPresenter.checkLogin();
        /**
         * 1、检查登录状态
         * 2、如果已经登录，跳转到mainactivity
         * 3、没有登录，跳转到登录界面
         */
    }

    @Override
    public void onCheckLoginned(boolean isLoginned) {
        if (isLoginned){
            //登录了
            startActivity(MainActivity.class,true);
        }else {
            //没有登录
            ObjectAnimator alpha = ObjectAnimator.ofFloat(ivSplash, "alpha", 0, 1).setDuration(2000);
            alpha.start();
            alpha.addListener(new MyAnimatorListener(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startActivity(LoginActivity.class,true);
                }
            });
        }
    }
}
