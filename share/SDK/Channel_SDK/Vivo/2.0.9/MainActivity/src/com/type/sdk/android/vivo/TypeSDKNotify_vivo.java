package com.type.sdk.android.vivo;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;

public class TypeSDKNotify_vivo
{
		public void sendToken(String _token_string , String _openid_string)
		{
			// TODO Auto-generated method stub
			String userToken = _token_string;
			String onpenId = _openid_string;
			TypeSDKLogger.i("login success intent extra:" + userToken);
		
			TypeSDKData.UserInfoData userData= TypeSDKBonjour_vivo.Instance().userInfo;
			userData.SetData(TypeSDKDefine.AttName.USER_TOKEN, userToken);
			userData.SetData(TypeSDKDefine.AttName.USER_ID, onpenId);
			userData.CopyAtt(TypeSDKBonjour_vivo.Instance().platform, AttName.CP_ID);
			userData.CopyAtt(TypeSDKBonjour_vivo.Instance().platform, AttName.SDK_NAME);
			userData.CopyAtt(TypeSDKBonjour_vivo.Instance().platform, AttName.PLATFORM);
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGIN, ReceiveFunction.MSG_LOGIN, 
					userData.DataToString(), TypeSDKBonjour_vivo.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Init()
		{
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_INIT_FINISH, ReceiveFunction.MSG_INITFINISH, 
					TypeSDKBonjour_vivo.Instance().platform.DataToString(), TypeSDKBonjour_vivo.Instance().platform.GetData(AttName.PLATFORM));
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_UPDATE_FINISH, ReceiveFunction.MSG_UPDATEFINISH, 
					TypeSDKBonjour_vivo.Instance().platform.DataToString(), TypeSDKBonjour_vivo.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Pay(String string)
		{
			TypeSDKLogger.i("pay");
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_PAY_RESULT, ReceiveFunction.MSG_PAYRESULT, 
					string, TypeSDKBonjour_vivo.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Logout()
		{
			TypeSDKLogger.i("user sdk logout");
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGOUT, ReceiveFunction.MSG_LOGOUT, 
					TypeSDKBonjour_vivo.Instance().userInfo.DataToString(), TypeSDKBonjour_vivo.Instance().platform.GetData(AttName.PLATFORM));
			
		}

		public void reLogin(String _in_string, String _uid_string)
		{
			TypeSDKLogger.i("user sdk reLogin");
			String userToken = _in_string;
			String uid = _uid_string;
			TypeSDKLogger.i("login success intent extra:" + userToken);
		
			TypeSDKData.UserInfoData userData= TypeSDKBonjour_vivo.Instance().userInfo;
			userData.SetData(TypeSDKDefine.AttName.USER_TOKEN, userToken);
			userData.SetData(TypeSDKDefine.AttName.USER_ID, uid);
			userData.CopyAtt(TypeSDKBonjour_vivo.Instance().platform, AttName.CP_ID);
			userData.CopyAtt(TypeSDKBonjour_vivo.Instance().platform, AttName.SDK_NAME);
			userData.CopyAtt(TypeSDKBonjour_vivo.Instance().platform, AttName.PLATFORM);
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_RELOGIN, ReceiveFunction.MSG_RELGOIN, 
					userData.DataToString(), TypeSDKBonjour_vivo.Instance().platform.GetData(AttName.PLATFORM));
			
			TypeSDKLogger.i("user sdk reLogin2");
		}

}
