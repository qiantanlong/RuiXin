package com.hongzhen.ruixin.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.hongzhen.ruixin.R;
import com.hongzhen.ruixin.adapter.NewFriendMsgAdapter;
import com.hongzhen.ruixin.db.InviteMessgeDao;
import com.hongzhen.ruixin.modle.InviteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class NewFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        findViewById(R.id.iv_back).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.list);
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        NewFriendMsgAdapter adapter = new NewFriendMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);
        EventBus.getDefault().post(new InviteMessage());
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
