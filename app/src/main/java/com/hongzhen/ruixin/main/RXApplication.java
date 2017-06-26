package com.hongzhen.ruixin.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.facebook.stetho.Stetho;
import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.db.InviteMessgeDao;
import com.hongzhen.ruixin.db.UserDao;
import com.hongzhen.ruixin.listener.OnContactListnerEvent;
import com.hongzhen.ruixin.modle.InviteMessage;
import com.hongzhen.ruixin.sp.PreferenceManager;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.view.activity.BaseActivity;
import com.hongzhen.ruixin.view.activity.ChartActivity;
import com.hongzhen.ruixin.view.activity.NewFriendActivity;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.Bmob;


/**
 * Created by yuhongzhen on 2017/6/7.
 */

public class RXApplication extends Application {
    private static RXApplication instance;
    private static Context mContext;
    public static boolean isDebug;
    private List<BaseActivity> mBaseActivityList = new ArrayList<>();
    private SoundPool mSoundPool;
    private int mDuanSound;
    private int mYuluSound;
    private DisplayMetrics displayMetrics = null;
    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;


    @Override
    public void onCreate() {
        MultiDex.install(this);//初始化分包设置，解决65536方法数错误
        super.onCreate();
        mContext = this;
        instance = this;
        isDebug=true;//日志打印开关
        initDbDao();//初始化本地数据库相关
        initBmob();//初始化BMOB
        initHuanXin();//初始化环信
        initSoundPool();//声音池初始化
        initStetho();//初始化Stetho
        PreferenceManager.init(this);//初始化SP存储


    }
    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(mContext);
        userDao = new UserDao(mContext);
    }


    private void initStetho() {
        Stetho.initializeWithDefaults(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initBmob() {
        Bmob.initialize(this,"1d0c7f48e57575d3d6185d53dae370a8");
    }
    /**
     * 初始化环信
     */
    private void initHuanXin() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //添加好友时，不需要验证的
        // options.setAcceptInvitationAlways(true);
        /**
         * 下面的代码是为了避免环信被初始化2次
         */
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        initContactsListner();//设置通讯录监听
        initMessageListener();//添加消息的监听
    }

    /**
     * 设置环信消息的监听
     */
    private void initMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                LogUtils.i("新的消息");
                //收到消息
                if (list != null && list.size() > 0) {
                    /**
                     * 1. 判断当前应用是否在后台运行
                     * 2. 如果是在后台运行，则发出通知栏
                     * 3. 如果是在后台发出长声音
                     * 4. 如果在前台发出短声音
                     */
                    if (isRuninBackground()) {
                        sendNotification(list.get(0));
                        //发出长声音
                        //参数2/3：左右喇叭声音的大小
                        mSoundPool.play(mYuluSound,1,1,0,0,1);
                    } else {
                        //发出短声音
                        mSoundPool.play(mDuanSound,1,1,0,0,1);
                    }
                    //将接收的消息通过eventbus发送出去，在ChartActivity中处理
                    EventBus.getDefault().post(list.get(0));
                }

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> list) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {
                //消息状态变动
            }
        });
    }

    /**
     * 判断应用是否后台运行中
     * @return
     */
    private boolean isRuninBackground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
        if (runningTaskInfo.topActivity.getPackageName().equals(getPackageName())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 发通知栏通知
     * @param message
     */
    private void sendNotification(EMMessage message) {
        EMTextMessageBody messageBody = (EMTextMessageBody) message.getBody();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //延时意图
        /**
         * 参数2：请求码 大于1
         */
        Intent mainIntent = new Intent(this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chatIntent = new Intent(this, ChartActivity.class);
        chatIntent.putExtra("username",message.getFrom());

        Intent[] intents = {mainIntent,chatIntent};
        PendingIntent pendingIntent = PendingIntent.getActivities(this,1,intents,PendingIntent.FLAG_UPDATE_CURRENT) ;
        Notification notification = new Notification.Builder(this)
                .setAutoCancel(true) //当点击后自动删除
                .setSmallIcon(R.mipmap.message) //必须设置
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.default_avatar))
                .setContentTitle("您有一条新消息")
                .setContentText(messageBody.getMessage())
                .setContentInfo(message.getFrom())
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        notificationManager.notify(1,notification);
    }
    private void sendNotification(InviteMessage inviteMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //延时意图
        /**
         * 参数2：请求码 大于1
         */
        Intent mainIntent = new Intent(this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chatIntent = new Intent(this, NewFriendActivity.class);
        chatIntent.putExtra("username",inviteMessage.getFrom());
        Intent[] intents = {mainIntent,chatIntent};
        PendingIntent pendingIntent = PendingIntent.getActivities(this,1,intents,PendingIntent.FLAG_UPDATE_CURRENT) ;
        Notification notification = new Notification.Builder(this)
                .setAutoCancel(true) //当点击后自动删除
                .setSmallIcon(R.mipmap.message) //必须设置
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.default_avatar))
                .setContentTitle("您有一条新消息")
                .setContentText("好友请求")
                .setContentInfo(inviteMessage.getFrom())
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        notificationManager.notify(1,notification);
    }

    /**
     * 设置环信通讯录的监听，监听好友的添加、删除等操作
     */
    private void initContactsListner() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(String s) {
                //好友被添加时回调
                EventBus.getDefault().post(new OnContactListnerEvent(s,true));
                LogUtils.i(s+"添加我为好友");
            }

            @Override
            public void onContactDeleted(String s) {
                //好友被删除时回调
                EventBus.getDefault().post(new OnContactListnerEvent(s,false));
            }

            @Override
            public void onContactInvited(final String username, String reason) {
                //被邀请时回调
                LogUtils.i(username+"要请我好友");
                onNewFriendInvited(username,reason);
                /*ThreadUtils.runOnSubThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().contactManager().acceptInvitation(s);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                });*/
            }

            @Override
            public void onFriendRequestAccepted(String username) {
                //添加好友请求被接受时回调
                LogUtils.i(username+"接受我为好友请求了");
                onAccepterRequest(username);
            }



            @Override
            public void onFriendRequestDeclined(String s) {
                //添加好友请求被拒绝时回调
                LogUtils.i(s+"拒绝了我好友请求");
            }
        });
    }

    /**
     * 当好友请求被接受时
     * @param username
     */
    private void onAccepterRequest(String username) {
        List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
        for (InviteMessage inviteMessage : msgs) {
            if (inviteMessage.getFrom().equals(username)) {
                return;
            }
        }
        // save invitation as message
        InviteMessage msg = new InviteMessage();
        msg.setFrom(username);
        msg.setTime(System.currentTimeMillis());
        msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
        notifyNewInviteMessage(msg);
        //将接收的消息通过eventbus发送出去，在MainActivity中处理
        EventBus.getDefault().post(msg);
    }

    /**
     * 当被邀请添加好友时
     */
    private void onNewFriendInvited(String username,String reason){
        List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

        for (InviteMessage inviteMessage : msgs) {
            if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                inviteMessgeDao.deleteMessage(username);
            }
        }
        //创建请求的消息
        InviteMessage msg = new InviteMessage();
        msg.setFrom(username);
        msg.setTime(System.currentTimeMillis());
        msg.setReason(reason);
        msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
        notifyNewInviteMessage(msg);//保存到本地数据库
        /**
         * 1. 判断当前应用是否在后台运行
         * 2. 如果是在后台运行，则发出通知栏
         * 3. 如果是在后台发出长声音
         * 4. 如果在前台发出短声音
         */
        if (isRuninBackground()) {
            sendNotification(msg);//状态栏通知
            //发出长声音
            //参数2/3：左右喇叭声音的大小
            mSoundPool.play(mYuluSound,1,1,0,0,1);
        } else {
            //发出短声音
            mSoundPool.play(mDuanSound,1,1,0,0,1);
        }
        //将接收的消息通过eventbus发送出去，在MainActivity中处理
        EventBus.getDefault().post(msg);

    }
    /**
     * save and notify invitation message
     *
     * @param msg
     */
    private void notifyNewInviteMessage(InviteMessage msg) {
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(mContext);
        }
        inviteMessgeDao.saveMessage(msg);
        //increase the unread message count
        inviteMessgeDao.saveUnreadMessageCount(1);
        // notify there is new message
    }

    /**
     * 获取当前application的实例
     *
     * @return
     */
    public static RXApplication getInstance() {
        return instance;
    }

    /**
     * 获取全局上下文
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    /**
     * activity管理，添加activity到集合中
     *
     * @param activity
     */
    public void addActivity(BaseActivity activity) {
        if (!mBaseActivityList.contains(activity) && activity != null) {
            mBaseActivityList.add(activity);
        }
    }

    /**
     * 从activity集合中移除全部activi
     */
    public void removeActivitys() {
        if (mBaseActivityList != null && mBaseActivityList.size() != 0) {
            for (Activity activity : mBaseActivityList) {
                mBaseActivityList.remove(activity);
            }
        }
    }

    /**
     * 将所有activity finish掉
     */
    public void finishActivities() {
        for (Activity activity : mBaseActivityList) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }
    /**
     * 移除activity 单个实例
     */
    public void removeActivity(BaseActivity activity) {
        if (activity instanceof BaseActivity && mBaseActivityList != null && mBaseActivityList.contains(activity)) {
            mBaseActivityList.remove(activity);
        }
    }

    /**
     * 获取appname
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }

    /**
     * 初始化声音池
     */
    private void initSoundPool() {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        mDuanSound = mSoundPool.load(this, R.raw.duan, 1);
        mYuluSound = mSoundPool.load(this, R.raw.yulu, 1);

    }

    /**
     * 获取屏幕的长宽信息
     * @return
     */
    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }
    /**
     * 获取屏幕高度
     * @return
     */
    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    //设置DisplayMetrics
    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }
    public int dp2px(float f)
    {
        return (int)(0.5F + f * getScreenDensity());
    }

    public int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }
}
