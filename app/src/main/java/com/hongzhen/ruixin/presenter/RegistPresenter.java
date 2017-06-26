package com.hongzhen.ruixin.presenter;

import java.io.File;

/**
 * Created by yuhongzhen on 2017/5/19.
 */

public interface RegistPresenter {
    void regist(String username, String pwd, String nickname);

    void onSaveToBMOB(File file);
}
