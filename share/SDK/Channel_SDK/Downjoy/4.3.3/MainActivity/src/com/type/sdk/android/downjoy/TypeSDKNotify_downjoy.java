package com.type.sdk.android.downjoy;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEvent.EventType;

public class TypeSDKNotify_downjoy
{
		public void sendToken(String _token_string, String _uid_string)
		{
			// TODO Auto-generated method stub
			String userToken = _token_string;
			String uid = _uid_string;
			TypeSDKLogger.i("login success intent extra:" + userToken);
		
			TypeSDKData.UserInfoData userData = TypeSDKBonjour_downjoy.Instance().userInfo;
			userData.SetData(TypeSDKDefine.AttName.USER_TOKEN, userToken);
			userData.SetData(TypeSDKDefine.AttName.USER_ID, uid);			
			userData.CopyAtt(TypeSDKBonjour_downjoy.Instance().platform, AttName.CP_ID);
			userData.CopyAtt(TypeSDKBonjour_downjoy.Instance().platform, AttName.SDK_NAME);
			userData.CopyAtt(TypeSDKBonjour_downjoy.Instance().platform, AttName.PLATFORM);
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGIN, ReceiveFunction.MSG_LOGIN, 
					userData.DataToString(), TypeSDKBonjour_downjoy.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Init()
		{
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_INIT_FINISH, ReceiveFunction.MSG_INITFINISH, 
					TypeSDKBonjour_downjoy.Instance().platform.DataToString(), TypeSDKBonjour_downjoy.Instance().platform.GetData(AttName.PLATFORM));
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_UPDATE_FINISH, ReceiveFunction.MSG_UPDATEFINISH, 
					TypeSDKBonjour_downjoy.Instance().platform.DataToString(), TypeSDKBonjour_downjoy.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Pay(String string)
		{
			TypeSDKLogger.i("pay");
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_PAY_RESULT, ReceiveFunction.MSG_PAYRESULT, 
					string, TypeSDKBonjour_downjoy.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Logout()
		{
			TypeSDKLogger.i("user sdk logout");
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGOUT, ReceiveFunction.MSG_LOGOUT, 
					TypeSDKBonjour_downjoy.Instance().userInfo.DataToString(), TypeSDKBonjour_downjoy.Instance().platform.GetData(AttName.PLATFORM));
			
		}

		public void reLogin()
		{
			TypeSDKLogger.i("user sdk reLogin");
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_RELOGIN, ReceiveFunction.MSG_RELGOIN, 
					TypeSDKBonjour_downjoy.Instance().userInfo.DataToString(), TypeSDKBonjour_downjoy.Instance().platform.GetData(AttName.PLATFORM));
			
			TypeSDKLogger.i("user sdk reLogin2");
		}
		
		public void localPush(TypeSDKData.BaseData baseData)
		{
			TypeSDKLogger.i("localPush send event start");
			TypeSDKLogger.i("localPush info:" + baseData.DataToString());
			TypeSDKEventManager.Instance().SendEvent(EventType.AND_EVENT_LOCAL_PUSH, ReceiveFunction.MSG_RECEIVE_LOCAL_PUSH, 
					baseData.DataToString(), 
					TypeSDKBonjour_downjoy.Instance().platform.GetData(AttName.PLATFORM));		
			TypeSDKLogger.i("localPush send event end");
		}

}
