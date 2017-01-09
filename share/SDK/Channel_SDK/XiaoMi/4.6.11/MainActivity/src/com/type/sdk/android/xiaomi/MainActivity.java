package com.type.sdk.android.xiaomi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKEvent.EventType;
import com.type.sdk.android.TypeSDKEventListener;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
//import com.type.sdk.android.TypeSDKUpdateManager;
import android.os.Bundle;
import com.type.sdk.android.BaseMainActivity;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.*;
import android.os.Handler;
import android.os.Looper;
public class MainActivity extends BaseMainActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		String result = "";
		result += "~"
				+ TypeSDKBonjour_xiaomi.Instance().isHasRequest(
						TypeSDKDefine.AttName.REQUEST_INIT_WITH_SEVER);
		result += "~"
				+ TypeSDKBonjour_xiaomi.Instance().isHasRequest(
						TypeSDKDefine.AttName.SUPPORT_PERSON_CENTER);
		result += "~"
				+ TypeSDKBonjour_xiaomi.Instance().isHasRequest(
						TypeSDKDefine.AttName.SUPPORT_SHARE);

		TypeSDKLogger.i("result " + result);
		super.onCreate(savedInstanceState);
		TypeSDKLogger.i("android on create finish");

		
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		TypeSDKLogger.e("sdk do destory");
		TypeSDKBonjour_xiaomi.Instance().onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		TypeSDKLogger.e("sdk do resume");
		TypeSDKBonjour_xiaomi.Instance().onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TypeSDKBonjour_xiaomi.Instance().onStop();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TypeSDKBonjour_xiaomi.Instance().onPause();
	}


	/**
	 * ���ⲿ call �� init����
	 * 
	 * @param _in_context
	 * @param _in_data
	 */
	public void CallInitSDK() {
		String _in_data = "";
		TypeSDKBonjour_xiaomi.Instance().initSDK(_in_context, _in_data);
	}

	/**
	 * ���ⲿ call�� login����
	 * 
	 * @param _in_context
	 * @param _in_data
	 */
	public void CallLogin(String _in_data) {
		TypeSDKLogger.i("call login:" + _in_data);
		TypeSDKBonjour_xiaomi.Instance().ShowLogin(_in_context, _in_data);
	}

	/**
	 * ���ⲿ call ��logout����
	 * 
	 * @param _in_context
	 */
	public void CallLogout() {
		TypeSDKBonjour_xiaomi.Instance().ShowLogout(_in_context);
	}

	/***
	 * 
	 * payData.SetData(U3DSharkAttName.REAL_PRICE,inputStr);
	 * payData.SetData(U3DSharkAttName.ITEM_NAME,"sk bi");
	 * payData.SetData(U3DSharkAttName.ITEM_DESC,"desc");
	 * payData.SetData(U3DSharkAttName.ITEM_COUNT,"1");
	 * payData.SetData(U3DSharkAttName.ITEM_SEVER_ID,"id");
	 * payData.SetData(U3DSharkAttName.SEVER_ID,"1");
	 * payData.SetData(U3DSharkAttName.EXTRA,"extra
	 * 
	 * 
	 * ���ⲿcall�Ķ���֧������(rmb�һ� ��Ϸ��)
	 * 
	 * @param _in_context
	 * @param _in_data
	 * @return
	 */
	public String CallPayItem(final String _in_data) {
		TypeSDKLogger.i("CallPayItem:" + _in_data);
		new Thread() {
			@Override
			public void run() {
				String payMessage;
				try {
					payMessage = HttpUtil.http_get(TypeSDKBonjour_xiaomi
							.Instance().platform
							.GetData(AttName.SWITCHCONFIG_URL));
					if (((payMessage.equals("") || payMessage.isEmpty()) && openPay)
							|| TypeSDKTool.openPay(TypeSDKBonjour_xiaomi
									.Instance().platform
									.GetData(AttName.SDK_NAME), payMessage)) {
						Handler mHandler = new Handler(Looper.getMainLooper());
						mHandler.post(new Runnable(){
							@Override
							public void run() {
								TypeSDKBonjour_xiaomi.Instance().PayItem(_in_context,
								_in_data);
							}
						});
					} else {
						TypeSDKNotify_xiaomi notify = new TypeSDKNotify_xiaomi();
						TypeSDKData.PayInfoData payResult = new TypeSDKData.PayInfoData();
						payResult.SetData(AttName.PAY_RESULT, "0");
						notify.Pay(payResult.DataToString());
						Handler dialogHandler = new Handler(Looper.getMainLooper());
						dialogHandler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								TypeSDKTool.showDialog("暂未开放充值！！！", _in_context);
							}});							
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}.start();
		return "client pay function finished";
	}

	/***
	 * ���ⲿcall �� �Ƕ���֧������һ��ƶ�����Ʒ��
	 * 
	 * @param _in_context
	 * @param _in_data
	 * @return
	 */
	public String CallExchangeItem(String _in_data) {
		return TypeSDKBonjour_xiaomi.Instance().ExchangeItem(_in_context, _in_data);
	}

	/***
	 * ���ⲿ���õ� ��ʵ����������
	 * 
	 * @param _in_context
	 */
	public void CallToolBar() {
		TypeSDKBonjour_xiaomi.Instance().ShowToolBar(_in_context);
	}

	public void CallHideToolBar() {
		TypeSDKBonjour_xiaomi.Instance().HideToolBar(_in_context);
	}

	/***
	 * ���ⲿ���õ���ʵ�û����ĺ���
	 * 
	 * @param _in_context
	 */
	public void CallPersonCenter() {
		TypeSDKBonjour_xiaomi.Instance().ShowPersonCenter(_in_context);
	}

	public void CallHidePersonCenter() {
		TypeSDKBonjour_xiaomi.Instance().HidePersonCenter(_in_context);
	}

	public void CallShare(String _in_data) {
		TypeSDKBonjour_xiaomi.Instance().ShowShare(_in_context, _in_data);
	}

	public void CallSetPlayerInfo(String _in_data) {
		TypeSDKBonjour_xiaomi.Instance().SetPlayerInfo(_in_context, _in_data);
	}

	public void CallExitGame() {
		TypeSDKBonjour_xiaomi.Instance().ExitGame(_in_context);
	}

	public void CallDestory() {
		TypeSDKBonjour_xiaomi.Instance().onDestroy();
	}

	public int CallLoginState() {
		return TypeSDKBonjour_xiaomi.Instance().LoginState(_in_context);
	}

	public String CallUserData() {
		return TypeSDKBonjour_xiaomi.Instance().GetUserData();
	}

	public String CallPlatformData() {
		return TypeSDKBonjour_xiaomi.Instance().GetPlatformData();
	}

	public boolean CallIsHasRequest(String _in_data) {
		return TypeSDKBonjour_xiaomi.Instance().isHasRequest(_in_data);
	}
    
	public String CallAnyFunction(String FuncName, String _in_data) {
		Method[] me = TypeSDKBonjour_xiaomi.Instance().getClass().getMethods();
		for (int i = 0; i < me.length; ++i) {
			if (me[i].getName().equals(FuncName)) {
				try {
					return (String) me[i].invoke(TypeSDKBonjour_xiaomi.Instance(),
							_in_context, _in_data);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "error";
	}
	
	public void AddLocalPush(String _in_data)
    {
    	TypeSDKLogger.i(_in_data);
    	TypeSDKBonjour_xiaomi.Instance().AddLocalPush(_in_context, _in_data);
    }
    
    public void RemoveLocalPush(String _in_data)
    {
    	TypeSDKLogger.i(_in_data);
    	TypeSDKBonjour_xiaomi.Instance().RemoveLocalPush(_in_context, _in_data);
    }
    
    public void RemoveAllLocalPush()
    {
    	
    	TypeSDKBonjour_xiaomi.Instance().RemoveAllLocalPush(_in_context);
    }
}
