package com.hongzhen.ruixin.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.eventbus.EBAvator;
import com.hongzhen.ruixin.presenter.MyInfoPresenter;
import com.hongzhen.ruixin.presenter.impl.MyInfoPresenterImpl;
import com.hongzhen.ruixin.sp.PreferenceManager;
import com.hongzhen.ruixin.utils.GlideUtils;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.view.widget.RXAlertDialog;
import com.hyphenate.util.PathUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hongzhen.ruixin.R.id.tv_name;
import static com.hongzhen.ruixin.utils.IntentUtils.getAppDetailSettingIntent;

/**
 * Created by yuhongzhen on 2017/6/13.
 */

public class MyInfoActivity extends BaseActivity implements MyInfoView {
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.title)
    RelativeLayout mTitle;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.re_avatar)
    RelativeLayout mReAvatar;
    @BindView(R.id.tv_temp_name)
    TextView mTvTempName;
    @BindView(tv_name)
    TextView mTvName;
    @BindView(R.id.re_name)
    RelativeLayout mReName;
    @BindView(R.id.tv_temp_fxid)
    TextView mTvTempFxid;
    @BindView(R.id.tv_fxid)
    TextView mTvFxid;
    @BindView(R.id.re_fxid)
    RelativeLayout mReFxid;
    @BindView(R.id.tv_temp_erweima)
    TextView mTvTempErweima;
    @BindView(R.id.re_qrcode)
    RelativeLayout mReQrcode;
    @BindView(R.id.tv_temp_address)
    TextView mTvTempAddress;
    @BindView(R.id.re_address)
    RelativeLayout mReAddress;
    @BindView(R.id.tv_temp_sex)
    TextView mTvTempSex;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.re_sex)
    RelativeLayout mReSex;
    @BindView(R.id.tv_temp_region)
    TextView mTvTempRegion;
    @BindView(R.id.tv_region)
    TextView mTvRegion;
    @BindView(R.id.re_region)
    RelativeLayout mReRegion;
    @BindView(R.id.tv_temp_sign)
    TextView mTvTempSign;
    @BindView(R.id.tv_sign)
    TextView mTvSign;
    @BindView(R.id.re_sign)
    RelativeLayout mReSign;
    private String imageName;

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int UPDATE_FXID = 4;
    private static final int UPDATE_NICK = 5;
    private static final int UPDATE_SIGN = 6;
    private static final int UPDATE_REGION = 7;
    private boolean hasChange;
    private MyInfoPresenter mMyInfoPresenter;
    private int TAKE_PHOTO_REQUEST_CODE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        ButterKnife.bind(this);
        mMyInfoPresenter = new MyInfoPresenterImpl(this);
        String currentUserAvatar = PreferenceManager.getInstance().getCurrentUserAvatar();
        if (currentUserAvatar == null || TextUtils.isEmpty(currentUserAvatar)) {
            mMyInfoPresenter.onGetAvatorToSp();
        } else {
            GlideUtils.setImageToImageView(this, mIvAvatar, currentUserAvatar);
        }
    }

    @OnClick({R.id.title, R.id.iv_back, R.id.re_avatar, R.id.re_name, R.id.re_fxid, R.id.re_qrcode, R.id.re_address, R.id.re_sex, R.id.re_region, R.id.re_sign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.re_avatar:
                showPhotoDialog();//弹出拍照和相册的对话框
                break;
            case R.id.re_name:
                break;
            case R.id.re_fxid:
                break;
            case R.id.re_qrcode:
                //二维码
                startActivity(ErWeiMaActivity.class,false);
                break;
            case R.id.re_address:
                break;
            case R.id.re_sex:
                break;
            case R.id.re_region:
                break;
            case R.id.re_sign:
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void showPhotoDialog() {

        List<String> items = new ArrayList<String>();
        items.add("拍照");
        items.add("相册");
        RXAlertDialog fxAlertDialog = new RXAlertDialog(MyInfoActivity.this, null, items);
        fxAlertDialog.init(new RXAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        //判断当前系统是否6.0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //做了拒绝后的处理
                            if (ActivityCompat.checkSelfPermission(MyInfoActivity.this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(MyInfoActivity.this, Manifest.permission.CAMERA)) {
                                    new AlertDialog.Builder(MyInfoActivity.this).setMessage("必须相机权限").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent appDetailSettingIntent = getAppDetailSettingIntent(MyInfoActivity.this);
                                            MyInfoActivity.this.startActivityForResult(appDetailSettingIntent, 0);
                                        }
                                    }).create()
                                            .show();
                                }
                                ActivityCompat.requestPermissions(MyInfoActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
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
                        imageName = getNowTime() + ".png";
                        Intent intent2 = new Intent(Intent.ACTION_PICK, null);
                        intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent2, PHOTO_REQUEST_GALLERY);
                        break;
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("myinfo-result");
        LogUtils.i("myinfo-result"+"request:"+requestCode+"__"+resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:
                    LogUtils.i("拍照" + PathUtil.getInstance().getImagePath());
                    startPhotoZoom(
                            Uri.fromFile(new File(PathUtil.getInstance().getImagePath(), imageName)),
                            240);
                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (data != null)
                        LogUtils.i("相册" + data.getData().toString());
                    startPhotoZoom(data.getData(), 240);

                    File file = new File(PathUtil.getInstance().getImagePath(), imageName);
                    LogUtils.i("file" + file.toString());

                    break;

                case PHOTO_REQUEST_CUT:
                    LogUtils.i(PathUtil.getInstance().getImagePath()+imageName);
                    File file1 = new File(PathUtil.getInstance().getImagePath(), imageName);
                    mMyInfoPresenter.onSaveToBMOB(file1);
                    break;
                case UPDATE_FXID:
                    String fxid = data.getStringExtra("value");
                    if (fxid != null) {
                        mTvFxid.setText(fxid);
                        hasChange = true;
                    }

                    break;
                case UPDATE_NICK:
                    String nick = data.getStringExtra("value");
                    if (nick != null) {
                        mTvName.setText(nick);
                        hasChange = true;
                    }
                    break;
                case UPDATE_SIGN:
                    String sign = data.getStringExtra("value");
                    if (sign != null) {
                        mTvSign.setText(sign);

                    }
                    break;
                case UPDATE_REGION:
                    if (data != null) {
                        String province = data.getStringExtra("province");
                        String city = data.getStringExtra("city");
                        boolean isRegion = true;
                        mTvRegion.setText(province + " " + city);
//                        updateInServer(province,city,isRegion);
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                //被授权了
                imageName = getNowTime() + ".png";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(PathUtil.getInstance().getImagePath(), imageName)));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
            } else {
                showToast("没有给予该应用权限，不让你用了");
                return;
            }
        }
    }

    private void startPhotoZoom(Uri uri1, int size) {
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
                Uri.fromFile(new File(PathUtil.getInstance().getImagePath(), imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 获取当前时间，并转化格式
     *
     * @return
     */
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    @Override
    public void onUpdateAvatorResult() {
        GlideUtils.setImageToImageView(this, mIvAvatar, PreferenceManager.getInstance().getCurrentUserAvatar());
        EventBus.getDefault().post(new EBAvator());
    }
}
