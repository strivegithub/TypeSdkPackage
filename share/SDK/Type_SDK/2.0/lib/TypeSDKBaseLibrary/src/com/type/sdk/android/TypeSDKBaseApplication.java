package com.type.sdk.android;

import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.type.sdk.notification.PushService;
import com.type.sdk.notification.Receiver1;
import com.type.sdk.notification.Receiver2;
import com.type.sdk.notification.Service2;

import android.app.Application;
import android.content.Context;

public class TypeSDKBaseApplication extends Application {
	
	private DaemonClient mDaemonClient;

	@Override
	protected void attachBaseContext(Context base) {
		//TypeSDKLogger.e("Application attachBaseContext");
		super.attachBaseContext(base);
		mDaemonClient = new DaemonClient(createDaemonConfigurations());
		mDaemonClient.onAttachBaseContext(base);
	}
	
	private DaemonConfigurations createDaemonConfigurations() {
		DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
				"com.hengxiang.wsxsm.m4399:process1", 
				PushService.class.getCanonicalName(),
				Receiver1.class.getCanonicalName());
		DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
				"com.hengxiang.wsxsm.m4399:process2", 
				Service2.class.getCanonicalName(),
				Receiver2.class.getCanonicalName());
		//TypeSDKLogger.e("Daemon createDaemonConfigurations");
		return new DaemonConfigurations(configuration1,configuration2);
	}
	
}
