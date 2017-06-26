package com.hongzhen.ruixin.factory;


import com.hongzhen.ruixin.view.fragment.BaseFragment;
import com.hongzhen.ruixin.view.fragment.ContactFragment;
import com.hongzhen.ruixin.view.fragment.ConversationFragment;
import com.hongzhen.ruixin.view.fragment.PluginFragment;
import com.hongzhen.ruixin.view.fragment.ProfileFrgment;

/**
 * Created by yuhongzhen on 2017/5/22.
 */

public class FragmentFactory {
    public static ConversationFragment mConversationFragment;
    public static ContactFragment mContactFragment=new ContactFragment();
    public static PluginFragment mPluginFragment;
    public static ProfileFrgment mProfileFragment;
    public static BaseFragment getFragment(int position){
        BaseFragment baseFragment=null;
        switch (position) {
            case 0:
                if (mConversationFragment==null){
                    mConversationFragment=new ConversationFragment();
                }
                baseFragment=mConversationFragment;
                break;
            case 1:
                if (mContactFragment==null){
                    mContactFragment=new ContactFragment();
                }
                baseFragment=mContactFragment;
                break;
            case 2:
                if (mPluginFragment==null){
                    mPluginFragment=new PluginFragment();
                }
                baseFragment=mPluginFragment;
                break;
            case 3:
                if (mProfileFragment==null){
                    mProfileFragment=new ProfileFrgment();
                }
                baseFragment=mProfileFragment;
                break;

        }
        return baseFragment;
    }
}
