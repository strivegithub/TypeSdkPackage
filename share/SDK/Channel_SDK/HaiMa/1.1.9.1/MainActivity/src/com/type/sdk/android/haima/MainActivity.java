package com.type.sdk.android.haima;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKEvent.EventType;
import com.type.sdk.android.TypeSDKEventListener;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKUpdateManager;
import com.type.utils.HttpUtil;
public class MainActivity extends BaseMainActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        TypeSDKLogger.i("android on create finish");
        TypeSDKUpdateManager update = new TypeSDKUpdateManager(this, 
        		TypeSDKBonjour_haima.Instance().platform.GetData(AttName.CHANNEL_ID), TypeSDKBonjour_haima.Instance().platform.GetData("check_update_url"));
        update.checkUpdateInfo();
        TypeSDKEventManager.Instance().AddEventListener(EventType.AND_EVENT_INIT_FINISH, initListener);
  }
  
    TypeSDKEventListener initListener = new TypeSDKEventListener(){
  		@Override
  		public Boolean NotifySDKEvent(TypeSDKEvent event){
  			RealOncreate();
  			return null;
  		}
  	};
	
	private void RealOncreate(){
//		SharkSDK.Instance().ClientInit(_in_context, "");
	}
    
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TypeSDKBonjour_haima.Instance().onPause();
	}


	@Override
    protected void onDestroy(){
		super.onDestroy();
    	TypeSDKLogger.e("sdk do destory");
    	TypeSDKBonjour_haima.Instance().onDestroy();
    }

    @Override
    protected void onResume(){
    	super.onResume();
    	TypeSDKBonjour_haima.Instance().onResume();
    }
    
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		TypeSDKBonjour_haima.Instance().onStart(_in_context);
	}
    
    /**
     *  ���ⲿ call �� init����
     * @param _in_context
     * @param _in_data
     */
    public  void CallInitSDK(){
    	String _in_data = "";
    	TypeSDKBonjour_haima.Instance().initSDK(_in_context, _in_data);
    }
    /**
     * ���ⲿ call�� login����
     * @param _in_context
     * @param _in_data
     */
    public void CallLogin(String _in_data)
    {
    	TypeSDKLogger.i("call login:" + _in_data);
    	TypeSDKBonjour_haima.Instance().ShowLogin(_in_context,_in_data);
    }
    /**
     * ���ⲿ call ��logout����
     * @param _in_context
     */
    public  void CallLogout()
    {
    	TypeSDKBonjour_haima.Instance().ShowLogout(_in_context);
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
					payMessage = HttpUtil.http_get(TypeSDKBonjour_haima
							.Instance().platform
							.GetData(AttName.SWITCHCONFIG_URL));
					if (((payMessage.equals("") || payMessage.isEmpty()) && openPay)
							|| TypeSDKTool.openPay(TypeSDKBonjour_haima
									.Instance().platform
									.GetData(AttName.SDK_NAME), payMessage)) {
						TypeSDKBonjour_haima.Instance().PayItem(_in_context,
								_in_data);
					} else {
						TypeSDKNotify_haima notify = new TypeSDKNotify_haima();
						TypeSDKData.PayInfoData payResult = new TypeSDKData.PayInfoData();
						payResult.SetData(AttName.PAY_RESULT, "0");
						notify.Pay(payResult.DataToString());
						Handler dialogHandler = new Handler(Looper.getMainLooper());
						dialogHandler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								TypeSDKTool.showDialog("暂未�?放充值！！！", _in_context);
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
     * ���ⲿcall �� �Ƕ���֧������һ��ƶ�����Ʒ��?
     * @param _in_context
     * @param _in_data
     * @return
     */
    public  String CallExchangeItem(String _in_data)
    {
    	return TypeSDKBonjour_haima.Instance().ExchangeItem(_in_context, _in_data);
    }
    /***
     * ���ⲿ���õ� ��ʵ����������
     * @param _in_context
     */
    public  void CallToolBar()
    {
    	TypeSDKBonjour_haima.Instance().ShowToolBar(_in_context);
    }
    public void CallHideToolBar()
    {
    	TypeSDKBonjour_haima.Instance().HideToolBar(_in_context);
    }
    /***
     * ���ⲿ���õ���ʵ�û����ĺ���
     * @param _in_context
     */
    public  void CallPersonCenter()
    {
    	TypeSDKBonjour_haima.Instance().ShowPersonCenter(_in_context);
    }
    public void CallHidePersonCenter()
    {
    	TypeSDKBonjour_haima.Instance().HidePersonCenter(_in_context);
    }
    public void CallShare(String _in_data)
    {
    	TypeSDKBonjour_haima.Instance().ShowShare(_in_context, _in_data);
    }
    public void CallSetPlayerInfo(String _in_data)
    {
    	TypeSDKBonjour_haima.Instance().SetPlayerInfo(_in_context, _in_data);
    }
    public void CallExitGame()
    {
    	TypeSDKBonjour_haima.Instance().ExitGame(_in_context);
    }
    public void CallDestory()
    {
    	TypeSDKBonjour_haima.Instance().onDestroy();
    }
    public int CallLoginState()
    {
    	return TypeSDKBonjour_haima.Instance().LoginState(_in_context);
    }
    public String CallUserData()
    {
    	return TypeSDKBonjour_haima.Instance().GetUserData();
    }
    public String CallPlatformData()
    {
    	return TypeSDKBonjour_haima.Instance().GetPlatformData();
    }
    public boolean CallIsHasRequest(String _in_data)
    {
    	return TypeSDKBonjour_haima.Instance().isHasRequest(_in_data);
    }
    public String CallAnyFunction(String FuncName,String _in_data)
    {
    	Method[] me = TypeSDKBonjour_haima.Instance().getClass().getMethods();
    	for(int i = 0;i<me.length;++i)
    	{
    		if(me[i].getName().equals(FuncName))
    		{
    			try 
    			{
					return (String) me[i].invoke(TypeSDKBonjour_haima.Instance(),_in_context ,_in_data);
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
    	TypeSDKBonjour_haima.Instance().AddLocalPush(_in_context, _in_data);
    }
    
    public void RemoveLocalPush(String _in_data)
    {
    	TypeSDKLogger.i(_in_data);
    	TypeSDKBonjour_haima.Instance().RemoveLocalPush(_in_context, _in_data);
    }
    
    public void RemoveAllLocalPush()
    {
    	
    	TypeSDKBonjour_haima.Instance().RemoveAllLocalPush(_in_context);
    }
	
}
