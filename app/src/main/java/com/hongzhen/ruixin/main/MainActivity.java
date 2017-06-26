package com.hongzhen.ruixin.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.db.InviteMessgeDao;
import com.hongzhen.ruixin.factory.FragmentFactory;
import com.hongzhen.ruixin.modle.InviteMessage;
import com.hongzhen.ruixin.utils.LogUtils;
import com.hongzhen.ruixin.utils.ToastUtils;
import com.hongzhen.ruixin.view.activity.AddFriendActivity;
import com.hongzhen.ruixin.view.activity.BaseActivity;
import com.hongzhen.ruixin.view.fragment.BaseFragment;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_bar)
    BottomNavigationBar bottomBar;
    private BadgeItem mBadgeItemConversation;
    private BadgeItem mBadgeItemContact;
    public boolean mIsUnReadMeg;
    private int mUnreadMsgsCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarColor();//设置状态栏颜色
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initToolBar();//初始化ToolBar
        initBottomBar();//初始化底部导航按钮栏
        initFirstFragment();//初始化第一个Fragment


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage message){
        updateUnreadCount();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(InviteMessage message){
        LogUtils.i("收到好友请求");

        boolean b = message.getStatus() == InviteMessage.InviteMesageStatus.BEAGREED;
        if (b){
            ToastUtils.showToast(this,message.getFrom()+"同意了你的好友邀请");
        }
        updateUnreadMessCount();
    }

    /**
     * 好友请求消息数量更新
     */
    private void updateUnreadMessCount() {
        //获取所有的未读消息
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(this);
        int unreadMessagesCount = inviteMessgeDao.getUnreadMessagesCount();
        ToastUtils.showToast(this,unreadMessagesCount+"");
        if (unreadMessagesCount >99){
            mBadgeItemContact.setText("99+");
            mBadgeItemContact.show(true);
            mIsUnReadMeg=true;
            FragmentFactory.mContactFragment.mTvUnread.setVisibility(View.VISIBLE);
            FragmentFactory.mContactFragment.mTvUnread.setText(""+unreadMessagesCount);
        }else if (unreadMessagesCount >0){
            mBadgeItemContact.setText(""+unreadMessagesCount);
            mBadgeItemContact.show(true);
            mIsUnReadMeg=true;
            FragmentFactory.mContactFragment.mTvUnread.setVisibility(View.VISIBLE);
            FragmentFactory.mContactFragment.mTvUnread.setText(""+unreadMessagesCount);
        }else{
            mBadgeItemContact.hide(true);
            FragmentFactory.mContactFragment.mTvUnread.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadCount();
    }

    //更新消息数量角标
    public void updateUnreadCount() {
        //获取所有的未读消息
        int unreadMsgsCount = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if (unreadMsgsCount>99){
            mBadgeItemConversation.setText("99+");
            mBadgeItemConversation.show(true);
        }else if (unreadMsgsCount>0){
            mBadgeItemConversation.setText(unreadMsgsCount+"");
            mBadgeItemConversation.show(true);
        }else{
            mBadgeItemConversation.hide(true);
        }

    }
    /**
     *
     */
    private void initFirstFragment() {
        /**
         * 如果这个Activity中已经有老（就是Activity保存的历史的状态，又恢复了）的Fragment，先全部移除
         */
        FragmentManager supportFragmentManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        for (int i=0;i<3;i++){
            Fragment fragment = supportFragmentManager.findFragmentByTag(i+"");
            if (fragment!=null){
                fragmentTransaction.remove(fragment);
            }

        }
        fragmentTransaction.commit();
        //加载默认的fragment
        getSupportFragmentManager().beginTransaction().add(R.id.framelayout, FragmentFactory.getFragment(0), "0").commit();
    }

    private void initBottomBar() {
        bottomBar.setMode(BottomNavigationBar.MODE_FIXED);
        BottomNavigationItem conversationBottomItem = new BottomNavigationItem(R.mipmap.conversation_selected_2, "会话");
        //为消息模块添加消息数量的角标
        mBadgeItemConversation = new BadgeItem();
        mBadgeItemConversation.setGravity(Gravity.RIGHT);
        mBadgeItemConversation.setTextColor("#ffffff");
        mBadgeItemConversation.setBackgroundColor("#ff0000");
        mBadgeItemConversation.setText("1");
        mBadgeItemConversation.show();//必须先show，否则有问题
        conversationBottomItem.setBadgeItem(mBadgeItemConversation);
        bottomBar.addItem(conversationBottomItem);
        BottomNavigationItem contactBottomItem = new BottomNavigationItem(R.mipmap.contact_selected_2, "通讯录");
        //为通讯录模块添加消息数量的角标
        mBadgeItemContact = new BadgeItem();
        mBadgeItemContact.setGravity(Gravity.RIGHT);
        mBadgeItemContact.setTextColor("#ffffff");
        mBadgeItemContact.setBackgroundColor("#ff0000");
        mBadgeItemContact.setText("  ");
        mBadgeItemContact.show();//必须先show，否则有问题
        mBadgeItemContact.hide();
        contactBottomItem.setBadgeItem(mBadgeItemContact);
        bottomBar.addItem(contactBottomItem);
        bottomBar.addItem(new BottomNavigationItem(R.mipmap.plugin_selected_2, "动态"));
        bottomBar.addItem(new BottomNavigationItem(R.mipmap.profile, "我"));
        bottomBar.setActiveColor(R.color.colorRuiXin);
        bottomBar.setInActiveColor(R.color.inActive);
        bottomBar.initialise();
        bottomBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                /**
                 * 先判断当前Fragment是否被添加到了MainActivity中
                 * 如果添加了则直接显示即可
                 * 如果没有添加则添加，然后显示
                 */
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                BaseFragment fragment = FragmentFactory.getFragment(position);

                if (!fragment.isAdded()){
                    fragmentTransaction.add(R.id.framelayout,fragment,position+"");
                }
                fragmentTransaction.show(fragment).commit();
            }

            @Override
            public void onTabUnselected(int position) {
                getSupportFragmentManager().beginTransaction().hide(FragmentFactory.getFragment(position)).commit();
            }

            @Override
            public void onTabReselected(int position) {

            }
        });

    }



    private void initToolBar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示回退按钮
    }

    /**
     * ToolBar右侧按钮点击菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * ToolBar右侧按钮点击菜单,其条目的ICON图片显示
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_friend:
                startActivity(AddFriendActivity.class,false);
                break;
            case R.id.menu_scan:
                break;
            case R.id.menu_about:
                break;
            //设置回退按钮点击事件
            case android.R.id.home:
                break;
        }
        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
