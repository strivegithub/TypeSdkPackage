/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */
package com.type.sdk.android.baidu;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;

public class TypeSDKNotify_baidu
{
		public void sendToken(String _in_uid,String _in_string)
		{
			// TODO Auto-generated method stub
			String userToken = _in_string;
			String uderId = _in_uid;
			TypeSDKLogger.i("login success intent extra:" + userToken);
		
			TypeSDKData.UserInfoData userData= TypeSDKBonjour_baidu.Instance().userInfo;
			userData.SetData(TypeSDKDefine.AttName.USER_TOKEN, userToken);
			userData.SetData(TypeSDKDefine.AttName.USER_ID, uderId);
			userData.CopyAtt(TypeSDKBonjour_baidu.Instance().platform, AttName.CP_ID);
			userData.CopyAtt(TypeSDKBonjour_baidu.Instance().platform, AttName.SDK_NAME);
			userData.CopyAtt(TypeSDKBonjour_baidu.Instance().platform, AttName.PLATFORM);
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGIN, ReceiveFunction.MSG_LOGIN, 
					userData.DataToString(), TypeSDKBonjour_baidu.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Init()
		{
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_INIT_FINISH, ReceiveFunction.MSG_INITFINISH, 
					TypeSDKBonjour_baidu.Instance().platform.DataToString(), TypeSDKBonjour_baidu.Instance().platform.GetData(AttName.PLATFORM));
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_UPDATE_FINISH, ReceiveFunction.MSG_UPDATEFINISH, 
					TypeSDKBonjour_baidu.Instance().platform.DataToString(), TypeSDKBonjour_baidu.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Pay(String string)
		{
			TypeSDKLogger.i("pay");
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_PAY_RESULT, ReceiveFunction.MSG_PAYRESULT, 
					string, TypeSDKBonjour_baidu.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Logout()
		{
			TypeSDKLogger.i("user sdk logout");
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGOUT, ReceiveFunction.MSG_LOGOUT, 
					TypeSDKBonjour_baidu.Instance().userInfo.DataToString(), TypeSDKBonjour_baidu.Instance().platform.GetData(AttName.PLATFORM));
			
		}

		public void reLogin(TypeSDKData.UserInfoData userData)
		{
			TypeSDKLogger.i("user sdk reLogin");		
			TypeSDKLogger.i("logout info:" + userData.DataToString());
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_RELOGIN,
					ReceiveFunction.MSG_RELGOIN, 
					userData.DataToString(), 
					TypeSDKBonjour_baidu.Instance().platform.GetData(AttName.PLATFORM));		
			TypeSDKLogger.i("user sdk reLogin2");
		}

}
