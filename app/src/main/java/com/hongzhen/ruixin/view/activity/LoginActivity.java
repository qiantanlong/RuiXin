package com.hongzhen.ruixin.view.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.main.MainActivity;
import com.hongzhen.ruixin.presenter.LoginPresenter;
import com.hongzhen.ruixin.presenter.impl.LoginPresenterImpl;
import com.hongzhen.ruixin.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by yuhongzhen on 2017/5/19.
 */

public class LoginActivity extends BaseActivity implements LoginView,TextView.OnEditorActionListener {
    private static final int REQUESTCODE = 1;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.til_username)
    TextInputLayout tilUsername;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.til_pwd)
    TextInputLayout tilPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_newuser)
    TextView tvNewuser;

    private LoginPresenter loginPresenter;
    private static final int REQUEST_SDCARD = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginPresenter=new LoginPresenterImpl(this);
        etUsername.setText(getUserName());
        etPwd.setText(getPwd());
        etPwd.setOnEditorActionListener(this);

    }

    @OnClick({R.id.btn_login, R.id.tv_newuser})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_newuser:
                startActivity(RegistActivity.class,false);
                break;
        }
    }

    private void login() {
        String userName = etUsername.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        if (!StringUtils.checkUserName(userName)) {
            tilUsername.setErrorEnabled(true);
            tilUsername.setError("用户名不合法！");
            return;
        } else {
            tilUsername.setErrorEnabled(false);
        }
        if (!StringUtils.checkPwd(pwd)) {
            tilPwd.setErrorEnabled(true);
            tilPwd.setError("密码不合法！");
            return;
        } else {
            tilPwd.setErrorEnabled(false);
        }
        //申请权限
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_SDCARD);
            }
        }
        showDialog("正在登录");
        loginPresenter.login(userName,pwd);
    }


    /**
     * 当再次startActivity的时候，接收新的Intent对象
     * 调用的前提是该启动模式是singleTask，或者singleTop但是他得在最上面才有效
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        etUsername.setText(getUserName());
        etPwd.setText(getPwd());
    }


    @Override
    public void onLogin(String username, String pwd, boolean success, String msg) {
        if (success){
            //登录成功
            saveUser(username,pwd);
            startActivity(MainActivity.class,true);
        }else {
            //登录失败
            showToast("登录失败："+msg);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId()==R.id.et_pwd){
            if (actionId== EditorInfo.IME_ACTION_DONE){
                login();
                return true;
            }
        }
        return false;
    }

    private void applyPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PermissionChecker.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUESTCODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUESTCODE){
            if (grantResults[0]==PermissionChecker.PERMISSION_GRANTED){
                login();
            }else {
                login();
            }
        }
    }
}
