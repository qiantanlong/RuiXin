package com.hongzhen.ruixin.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.modle.Constant;
import com.hongzhen.ruixin.presenter.RegistPresenter;
import com.hongzhen.ruixin.presenter.impl.RegistPresenterImpl;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.view.widget.RXAlertDialog;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

import static com.hongzhen.ruixin.utils.IntentUtils.getAppDetailSettingIntent;


/**
 * Created by yuhongzhen on 2017/5/19.
 */

public class RegistActivity extends BaseActivity implements RegistView {
    private EditText et_usernick;
    private EditText et_usertel;
    private EditText et_password;
    private Button btn_register;
    private TextView tv_xieyi;
    private ImageView iv_hide;
    private ImageView iv_show;
    private ImageView iv_photo;
    private RegistPresenter mRegistPresenter;

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private String imageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarColor();
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        initView();
        mRegistPresenter = new RegistPresenterImpl(this);
    }

    /**
     * 初始化view
     */
    private void initView() {
        et_usernick = (EditText) findViewById(R.id.et_usernick);
        et_usertel = (EditText) findViewById(R.id.et_usertel);
        et_password = (EditText) findViewById(R.id.et_password);

        // 监听多个输入框
        et_usernick.addTextChangedListener(new TextChange());
        et_usertel.addTextChangedListener(new TextChange());
        et_password.addTextChangedListener(new TextChange());
        btn_register = (Button) findViewById(R.id.btn_register);
        tv_xieyi = (TextView) findViewById(R.id.tv_xieyi);
        iv_hide = (ImageView) findViewById(R.id.iv_hide);

        iv_show = (ImageView) findViewById(R.id.iv_show);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);

        //设置控件的监听
        iv_hide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iv_hide.setVisibility(View.GONE);
                iv_show.setVisibility(View.VISIBLE);
                et_password
                        .setTransformationMethod(HideReturnsTransformationMethod
                                .getInstance());
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = et_password.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }

            }

        });
        iv_show.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iv_show.setVisibility(View.GONE);
                iv_hide.setVisibility(View.VISIBLE);
                et_password
                        .setTransformationMethod(PasswordTransformationMethod
                                .getInstance());
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = et_password.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }

        });
        iv_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showCamera();
            }

        });

        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String usernick = et_usernick.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String usertel = et_usertel.getText().toString().trim();
                showDialog("正在注册……");
                mRegistPresenter.regist(usertel, password, usernick);

            }

        });
    }

    // 拍照部分
    private void showCamera() {

        List<String> items = new ArrayList<String>();
        items.add("拍照");
        items.add("相册");
        RXAlertDialog fxAlertDialog = new RXAlertDialog(RegistActivity.this, null, items);
        fxAlertDialog.init(new RXAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        LogUtils.i("photo" + 0);
                        //判断当前系统是否6.0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //做了拒绝后的处理
                            if (ActivityCompat.checkSelfPermission(RegistActivity.this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(RegistActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                                return;
                            }

                        }
                        imageName = getNowTime() + ".png";
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(PathUtil.getInstance().getImagePath(), imageName)));
                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);

                        break;
                    case 1:
                        LogUtils.i("photo" + 1);
                        imageName = getNowTime() + ".png";
                        Intent intent2 = new Intent(Intent.ACTION_PICK, null);
                        intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent2, PHOTO_REQUEST_GALLERY);
                        break;
                }
            }
        });

    }

    /**
     * 获取当前时间
     *
     * @return
     */
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    private void startPhotoZoom(Uri uri1, int size) {
        LogUtils.i("startPhotoZoom");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri1, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Constant.DIR_AVATAR, imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("result");
        LogUtils.i("result"+"request:"+requestCode+"__"+resultCode);
        if (data==null){
            LogUtils.i("data-null");
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:
                    LogUtils.i("Camera");
                    startPhotoZoom(Uri.fromFile(new File(Constant.DIR_AVATAR, imageName)), 240);
                    break;

                case PHOTO_REQUEST_GALLERY:
                    LogUtils.i("相册");
                    if (data != null)
                        startPhotoZoom(data.getData(), 240);
                    break;

                case PHOTO_REQUEST_CUT:
                    LogUtils.i("Cut");
                    File externalStorageDirectory = Environment.getExternalStorageDirectory();
                    LogUtils.i(externalStorageDirectory.getPath()+imageName);
                    File file1 = new File(externalStorageDirectory, imageName);
                    mRegistPresenter.onSaveToBMOB(file1);
                    break;

            }


        }
    }


    @Override
    public void onRegist(String username, String pwd, String nickname, boolean isSuceess, String msg) {
        hideDialog();
        if (isSuceess) {
            showToast("注册成功！");
            saveUser(username, pwd);
            startActivity(LoginActivity.class, true);
        } else {
            showToast("注册失败：" + msg);
        }
    }




    // EditText监听器类
    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {

            boolean Sign1 = et_usernick.getText().length() > 0;
            boolean Sign2 = et_usertel.getText().length() > 0;
            boolean Sign3 = et_password.getText().length() > 0;

            if (Sign1 & Sign2 & Sign3) {

                btn_register.setEnabled(true);
            }
            // 在layout文件中，对Button的text属性应预先设置默认值，否则刚打开程序的时候Button是无显示的
            else {

                btn_register.setEnabled(false);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0]==PermissionChecker.PERMISSION_DENIED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(RegistActivity.this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(RegistActivity.this).setMessage("必须相机权限").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent appDetailSettingIntent = getAppDetailSettingIntent(RegistActivity.this);
                        RegistActivity.this.startActivityForResult(appDetailSettingIntent, 0);
                    }
                }).create()
                        .show();
            }
        }
    }
}
