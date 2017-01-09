package com.type.sdk.android.downjoy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKLogger;
//import com.type.sdk.android.TypeSDKUpdateManager;
import com.type.sdk.android.TypeSDKDefine.AttName;
import android.content.Context;
import android.os.Bundle;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.*;
import android.os.Handler;
import android.os.Looper;
public class MainActivity extends BaseMainActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	
    	String result="";
    	result +="~"+ TypeSDKBonjour_downjoy.Instance().isHasRequest(TypeSDKDefine.AttName.REQUEST_INIT_WITH_SEVER);
    	result +="~"+ TypeSDKBonjour_downjoy.Instance().isHasRequest(TypeSDKDefine.AttName.SUPPORT_PERSON_CENTER);
    	result +="~"+ TypeSDKBonjour_downjoy.Instance().isHasRequest(TypeSDKDefine.AttName.SUPPORT_SHARE);
        CallInitSDK();
    	TypeSDKLogger.i("result "+result);
    	super.onCreate(savedInstanceState);
        TypeSDKLogger.i("android on create finish");
//        TypeSDKUpdateManager update = new TypeSDKUpdateManager(this, 
//        		TypeSDKBonjour_downjoy.Instance().platform.GetData(AttName.CHANNEL_ID), TypeSDKBonjour_downjoy.Instance().platform.GetData("check_update_url"));
//        update.checkUpdateInfo();
    }
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	TypeSDKLogger.e("sdk do destroy");
    	TypeSDKBonjour_downjoy.Instance().onDestroy();
    }

    
    @Override
    protected void onResume() 
    {
    	super.onResume();
    	TypeSDKBonjour_downjoy.Instance().onResume(_in_context);
    }
    
	
    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TypeSDKBonjour_downjoy.Instance().onStop();
	}
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TypeSDKBonjour_downjoy.Instance().onPause();
	}
	
    /**
     *  ���ⲿ call �� init����
     * @param _in_context
     * @param _in_data
     */
    public  void CallInitSDK()
    {
    	String _in_data = "";
    	TypeSDKBonjour_downjoy.Instance().initSDK(_in_context,_in_data);
    }
    /**
     * ���ⲿ call�� login����
     * @param _in_context
     * @param _in_data
     */
    public  void CallLogin(String _in_data)
    {
    	TypeSDKLogger.i("call login:" + _in_data);
    	TypeSDKBonjour_downjoy.Instance().ShowLogin(_in_context,_in_data);
    }
    /**
     * ���ⲿ call ��logout����
     * @param _in_context
     */
    public  void CallLogout()
    {
    	TypeSDKBonjour_downjoy.Instance().ShowLogout(_in_context);
    }
    /***
     * 
     * payData.SetData(U3DSharkAttName.REAL_PRICE,inputStr);
			payData.SetData(U3DSharkAttName.ITEM_NAME,"sk bi");
			payData.SetData(U3DSharkAttName.ITEM_DESC,"desc");
			payData.SetData(U3DSharkAttName.ITEM_COUNT,"1");
			payData.SetData(U3DSharkAttName.ITEM_SEVER_ID,"id");
			payData.SetData(U3DSharkAttName.SEVER_ID,"1");
			payData.SetData(U3DSharkAttName.EXTRA,"extra
			
			
     * ���ⲿcall�Ķ���֧������(rmb�һ� ��Ϸ��)
     * @param _in_context
     * @param _in_data
     * @return
     */
    public  String CallPayItem(final String _in_data)
    {
    	TypeSDKLogger.i("CallPayItem:" + _in_data);
    	new Thread() {
			@Override
			public void run() {
				String payMessage;
				try {
					payMessage = HttpUtil.http_get(TypeSDKBonjour_downjoy
							.Instance().platform
							.GetData(AttName.SWITCHCONFIG_URL));
					if (((payMessage.equals("") || payMessage.isEmpty()) && openPay)
							|| TypeSDKTool.openPay(TypeSDKBonjour_downjoy
									.Instance().platform
									.GetData(AttName.SDK_NAME), payMessage)) {
						TypeSDKBonjour_downjoy.Instance().PayItem(_in_context,
								_in_data);
					} else {
						TypeSDKNotify_downjoy notify = new TypeSDKNotify_downjoy();
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
     * @param _in_context
     * @param _in_data
     * @return
     */
    public  String CallExchangeItem(String _in_data)
    {
    	return TypeSDKBonjour_downjoy.Instance().ExchangeItem(_in_context, _in_data);
    }
    /***
     * ���ⲿ���õ� ��ʵ����������
     * @param _in_context
     */
    public  void CallToolBar()
    {
    	TypeSDKBonjour_downjoy.Instance().ShowToolBar(_in_context);
    }
    public void CallHideToolBar()
    {
    	TypeSDKBonjour_downjoy.Instance().HideToolBar(_in_context);
    }
    /***
     * ���ⲿ���õ���ʵ�û����ĺ���
     * @param _in_context
     */
    public  void CallPersonCenter()
    {
    	TypeSDKBonjour_downjoy.Instance().ShowPersonCenter(_in_context);
    }
    public void CallHidePersonCenter()
    {
    	TypeSDKBonjour_downjoy.Instance().HidePersonCenter(_in_context);
    }
    public void CallShare(String _in_data)
    {
    	TypeSDKBonjour_downjoy.Instance().ShowShare(_in_context, _in_data);
    }
    public void CallSetPlayerInfo(String _in_data)
    {
    	TypeSDKBonjour_downjoy.Instance().SetPlayerInfo(_in_context, _in_data);
    }
    public void CallExitGame()
    {
    	TypeSDKBonjour_downjoy.Instance().ExitGame(_in_context);
    }
    public void CallDestory()
    {
    	TypeSDKBonjour_downjoy.Instance().onDestroy();
    }
    public int CallLoginState()
    {
    	return TypeSDKBonjour_downjoy.Instance().LoginState(_in_context);
    }
    public String CallUserData()
    {
    	return TypeSDKBonjour_downjoy.Instance().GetUserData();
    }
    public String CallPlatformData()
    {
    	return TypeSDKBonjour_downjoy.Instance().GetPlatformData();
    }
    public boolean CallIsHasRequest(String _in_data)
    {
    	return TypeSDKBonjour_downjoy.Instance().isHasRequest(_in_data);
    }
    
    
    public String CallAnyFunction(String FuncName,String _in_data)
    {
    	Method[] me = TypeSDKBonjour_downjoy.Instance().getClass().getMethods();
    	for(int i = 0;i<me.length;++i)
    	{
    		if(me[i].getName().equals(FuncName))
    		{
    			try 
    			{
					return (String) me[i].invoke(TypeSDKBonjour_downjoy.Instance(),_in_context ,_in_data);
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
    	TypeSDKBonjour_downjoy.Instance().AddLocalPush(_in_context, _in_data);
    }
    
    public void RemoveLocalPush(String _in_data)
    {
    	TypeSDKLogger.i(_in_data);
    	TypeSDKBonjour_downjoy.Instance().RemoveLocalPush(_in_context, _in_data);
    }
    
    public void RemoveAllLocalPush()
    {
    	TypeSDKBonjour_downjoy.Instance().RemoveAllLocalPush(_in_context);
    }
    
}
