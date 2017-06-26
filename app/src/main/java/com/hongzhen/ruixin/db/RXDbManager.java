package com.hongzhen.ruixin.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hongzhen.ruixin.bmob.BUser;
import com.hongzhen.ruixin.main.RXApplication;
import com.hongzhen.ruixin.modle.Constant;
import com.hongzhen.ruixin.modle.EaseUser;
import com.hongzhen.ruixin.modle.InviteMessage;
import com.hongzhen.ruixin.modle.InviteMessage.InviteMesageStatus;
import com.hongzhen.ruixin.utils.EaseCommonUtils;
import com.hongzhen.ruixin.utils.LogUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;



/**
 * Created by yuhongzhen on 2017/6/7.
 */

public class RXDbManager  {
    private DbOpenHelper mDbOpenHelper;
    private static RXDbManager dbMgr;

    public RXDbManager() {
        mDbOpenHelper = DbOpenHelper.getInstance(RXApplication.getContext());
    }
    public static synchronized RXDbManager getInstance(){
        if(dbMgr == null){
            dbMgr = new RXDbManager();
        }
        return dbMgr;
    }
    /**
     * 保存用户列表
     *
     * @param contactList
     */
    synchronized public void saveContactList(List<EaseUser> contactList) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);//删除用户表
            for (EaseUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUserName());
                if(user.getNickName() != null)
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNickName());
                if(user.getAvatar() != null)
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                if(user.getUserInfo()!= null)
                    values.put(UserDao.COLUMN_NAME_INFO, user.getUserInfo());
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }
    /**
     * 获取用户实例列表
     *
     * @return
     */
    synchronized public Map<String, EaseUser> getContactList() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        Map<String, EaseUser> users = new Hashtable<String, EaseUser>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String userInfo = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_INFO));
                EaseUser user = new EaseUser(username);
                user.setNickName(nick);
                user.setAvatar(avatar);
                user.setUserInfo(userInfo);
                if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                        || username.equals(Constant.CHAT_ROOM)|| username.equals(Constant.CHAT_ROBOT)) {
                    user.setInitialLetter("");
                } else {
                    EaseCommonUtils.setUserInitialLetter(user);
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }
    public  List<BUser> getUserNameList(){
        BUser bUser=null;
        SQLiteDatabase readableDatabase = mDbOpenHelper.getReadableDatabase();
        Cursor query = readableDatabase.rawQuery("SELECT * FROM uers;", null);
//        Cursor query = readableDatabase.query(UserDao.TABLE_NAME, new String[]{"*"}, UserDao.COLUMN_NAME_ID + "=?", new String[]{"*"}, null, null, null);
        List<BUser> contactsList = new ArrayList<>();
        while (query.moveToNext()){
            bUser=new BUser();
            String nickName = query.getString(0);
            String avatar = query.getString(1);
            String userInfo = query.getString(2);
            String userName = query.getString(3);
            bUser.setAvatar(avatar);
            bUser.setUsername(userName);
            bUser.setUserInfo(userInfo);
            bUser.setNick(nickName);
            contactsList.add(bUser);
        }
        query.close();
        readableDatabase.close();
        return contactsList;
    }

    /**
     * delete a contact
     * @param username
     */
    synchronized public void deleteContact(String username){
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }/**
     * delete a contact
     * @param username
     */
    synchronized public BUser getContact(String username){
        BUser bUser = new BUser();
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        Cursor cursor =db.rawQuery("select * from uers where username=?", new String[]{username});
        if(cursor.moveToFirst()){
            String nickName = cursor.getString(0);
            String avatar = cursor.getString(1);
            String userInfo = cursor.getString(2);
            String userName = cursor.getString(3);
            bUser.setNick(nickName);
            bUser.setUsername(username);
            bUser.setUserInfo(userInfo);
            bUser.setAvatar(avatar);
            LogUtils.i(nickName+avatar+userInfo+userName);
           return bUser;
        }
        cursor.close();
        return null;
    }

    /**
     * save a contact
     * @param user
     */
    synchronized public void saveContact(EaseUser user){
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUserName());
        if(user.getNickName() != null)
            values.put(UserDao.COLUMN_NAME_NICK, user.getNickName());
        if(user.getAvatar() != null)
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        if(user.getUserInfo()!= null)
            values.put(UserDao.COLUMN_NAME_INFO, user.getUserInfo());
        if(db.isOpen()){
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }
    /**
     * save a message
     * @param message
     * @return  return cursor of the message
     */
    public synchronized Integer saveMessage(InviteMessage message){
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        int id = -1;
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(InviteMessgeDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessgeDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            db.insert(InviteMessgeDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMessgeDao.TABLE_NAME,null);
            if(cursor.moveToFirst()){
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    /**
     * update message
     * @param msgId
     * @param values
     */
    synchronized public void updateMessage(int msgId,ContentValues values){
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            db.update(InviteMessgeDao.TABLE_NAME, values, InviteMessgeDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    /**
     * get messges
     * @return
     */
    synchronized public List<InviteMessage> getMessagesList(){
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        List<InviteMessage> msgs = new ArrayList<InviteMessage>();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + InviteMessgeDao.TABLE_NAME + " desc",null);
            while(cursor.moveToNext()){
                InviteMessage msg = new InviteMessage();
                int id = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_STATUS));
                String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUPINVITER));

                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                msg.setGroupInviter(groupInviter);

                if(status == InviteMessage.InviteMesageStatus.BEINVITEED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEINVITEED);
                else if(status == InviteMesageStatus.BEAGREED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEAGREED);
                else if(status == InviteMesageStatus.BEREFUSED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEREFUSED);
                else if(status == InviteMesageStatus.AGREED.ordinal())
                    msg.setStatus(InviteMesageStatus.AGREED);
                else if(status == InviteMesageStatus.REFUSED.ordinal())
                    msg.setStatus(InviteMesageStatus.REFUSED);
                else if(status == InviteMesageStatus.BEAPPLYED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEAPPLYED);
                else if(status == InviteMesageStatus.GROUPINVITATION.ordinal())
                    msg.setStatus(InviteMesageStatus.GROUPINVITATION);
                else if(status == InviteMesageStatus.GROUPINVITATION_ACCEPTED.ordinal())
                    msg.setStatus(InviteMesageStatus.GROUPINVITATION_ACCEPTED);
                else if(status == InviteMesageStatus.GROUPINVITATION_DECLINED.ordinal())
                    msg.setStatus(InviteMesageStatus.GROUPINVITATION_DECLINED);

                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    /**
     * delete invitation message
     * @param from
     */
    synchronized public void deleteMessage(String from){
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(InviteMessgeDao.TABLE_NAME, InviteMessgeDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }

    synchronized int getUnreadNotifyCount(){
        int count = 0;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select " + InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMessgeDao.TABLE_NAME, null);
            if(cursor.moveToFirst()){
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    synchronized void setUnreadNotifyCount(int count){
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMessgeDao.TABLE_NAME, values, null,null);
        }
    }

    synchronized public void closeDB(){
        if(mDbOpenHelper != null){
            mDbOpenHelper.closeDB();
        }
        dbMgr = null;
    }

}
