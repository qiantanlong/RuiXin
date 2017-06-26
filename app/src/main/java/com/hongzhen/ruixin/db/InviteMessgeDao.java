/**
 * Created by yuhongzhen on 2017/6/7.
 */
package com.hongzhen.ruixin.db;

import android.content.ContentValues;
import android.content.Context;

import com.hongzhen.ruixin.modle.InviteMessage;

import java.util.List;


public class InviteMessgeDao {
	static final String TABLE_NAME = "new_friends_msgs";
	static final String COLUMN_NAME_ID = "id";
	static final String COLUMN_NAME_FROM = "username";
	static final String COLUMN_NAME_GROUP_ID = "groupid";
	static final String COLUMN_NAME_GROUP_Name = "groupname";
	
	static final String COLUMN_NAME_TIME = "time";
	static final String COLUMN_NAME_REASON = "reason";
	public static final String COLUMN_NAME_STATUS = "status";
	static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
	static final String COLUMN_NAME_GROUPINVITER = "groupinviter";
	
	static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";
	
		
	public InviteMessgeDao(Context context){
	}
	
	/**
	 * 保存 InviteMessage
	 * @param message
	 * @return  return cursor of the message
	 */
	public Integer saveMessage(InviteMessage message){
		return RXDbManager.getInstance().saveMessage(message);
	}
	
	/**
	 * 更新 InviteMessage
	 * @param msgId
	 * @param values
	 */
	public void updateMessage(int msgId,ContentValues values){
	    RXDbManager.getInstance().updateMessage(msgId, values);
	}
	
	/**
	 * 获取InviteMessage
	 * @return
	 */
	public List<InviteMessage> getMessagesList(){
		return RXDbManager.getInstance().getMessagesList();
	}
	/**
	 * 删除InviteMessage
	 * @return
	 */
	public void deleteMessage(String from){
		RXDbManager.getInstance().deleteMessage(from);
	}
	/**
	 * 获取InviteMessage未读数量
	 * @return
	 */
	public int getUnreadMessagesCount(){
	    return RXDbManager.getInstance().getUnreadNotifyCount();
	}
	/**
	 * 保存InviteMessage未读数量
	 * @return
	 */
	public void saveUnreadMessageCount(int count){
		RXDbManager.getInstance().setUnreadNotifyCount(count);
	}
}
