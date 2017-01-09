/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */
package com.type.sdk.notification;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.type.sdk.android.TypeSDKLogger;

public class PushService extends Service {

	//private boolean isFirst = true;
	//private ScheduledThreadPoolExecutor poolExecutor;
	private Timer timer;
	public static String channelName = "";
	public static boolean stop = false;
	public static Map<String, String> pushMap = new HashMap<String, String>();
	public SharedPreferencesUtil util;


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("PushService onBind");
		return null;
	}

	@Override
	public void onDestroy() {
		TypeSDKLogger.d("PushService onDestroy");
		super.onDestroy();
		if (timer != null) {
			stopForeground(true);
			timer.cancel();
			timer = null;
		}
		Intent intent = new Intent(this, PushService.class);
		TypeSDKLogger.d("Try to restart PushService");
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		this.startService(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("PushService onStartCommand. channelName="
				+ channelName);

		if (null == timer) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					util = null;
					util = new SharedPreferencesUtil(PushService.this);
					if (channelName == null || channelName.isEmpty()) {
						channelName = util.read("channelName");
					}
					if (util.read("id") != null) {
						if (pushMap.size() != util.read("id").split(";").length) {
							pushMap.clear();
							String[] stringsId = util.read("id").split(";");
							for (int i = 0; i < stringsId.length; i++) {
								if (util.read(stringsId[i]) != null) {
									String[] strings = util.read(stringsId[i])
											.split(";");
									pushMap.put(strings[1],
											util.read(stringsId[i]));
								} else {
									TypeSDKLogger.i("util.read(stringsId[i])");
								}
							}
							TypeSDKLogger.d("pushMap:" + pushMap.toString());
						}

						Date curTime = new Date(System.currentTimeMillis());
						String curKey = "";
						for (int i = 1; i < 5; i++) {
							if (i == 1) {
								SimpleDateFormat formatter1 = new SimpleDateFormat(
										"MM-dd HH:mm");
								String formatCurTime1 = formatter1
										.format(curTime);
								if (pushMap.get(formatCurTime1) != null) {
									curKey = formatCurTime1;
								}
							}
							if (i == 2) {
								SimpleDateFormat formatter2 = new SimpleDateFormat(
										"dd HH:mm");
								String formatCurTime2 = formatter2
										.format(curTime);
								if (pushMap.get(formatCurTime2) != null) {
									curKey = formatCurTime2;
								}
							}
							if (i == 3) {
								final Calendar c = Calendar.getInstance();
								c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
								String stringWeek = String.valueOf(c
										.get(Calendar.DAY_OF_WEEK)) + " ";
								SimpleDateFormat formatter3 = new SimpleDateFormat(
										"HH:mm");
								String formatCurTime3 = formatter3
										.format(curTime);
								if (pushMap.get(stringWeek + formatCurTime3) != null) {
									curKey = stringWeek + formatCurTime3;
								}
							}
							if (i == 4) {
								SimpleDateFormat formatter4 = new SimpleDateFormat(
										"HH:mm");
								String formatCurTime4 = formatter4
										.format(curTime);
								if (pushMap.get(formatCurTime4) != null) {
									curKey = formatCurTime4;
								}
							}
						}

						curKey = "19:25";
						//TypeSDKLogger.i("CurKey=" + curKey);

						if ((curKey != null) && (!curKey.equals(""))) {
							String[] contents = null;
							try {
								contents = pushMap.get(curKey).split(";");
							} catch (Exception e) {
								TypeSDKLogger.e(e.toString());
							}

							NotificationManager mn = (NotificationManager) PushService.this
									.getSystemService(NOTIFICATION_SERVICE);

							
							//TypeSDKLogger.i("channelName:" + channelName);
							Class<?> launchClass = null;
							try {
								launchClass = Class
										.forName("com.type.sdk.android."
												+ channelName + ".MainActivity");
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Intent notificationIntent = new Intent(
									PushService.this, launchClass);// 点击跳转位置
							notificationIntent.putExtra("push_need_notify",
									contents[5]);
							notificationIntent.putExtra("push_receive_type",
									contents[6]);
							notificationIntent.putExtra("push_receive_info",
									contents[7]);
							PendingIntent contentIntent = PendingIntent
									.getActivity(PushService.this, 0,
											notificationIntent, 0);
							
//API > 16
//							Notification.Builder builder = new Notification.Builder(
//									PushService.this);
//							builder.setContentIntent(contentIntent);
//							builder.setSmallIcon(R.drawable.app_icon);
//							builder.setTicker(contents[3]); // 测试通知栏标题
//															// intent.getStringExtra("tickerText")
//							builder.setContentTitle(contents[3]);// 下拉通知栏标题
//																	// intent.getStringExtra("contentTitle")
//							builder.setContentText(contents[4]); // 下拉通知啦内容
//																	// intent.getStringExtra("contentText")
//							builder.setAutoCancel(true);
//							builder.setDefaults(Notification.DEFAULT_ALL);
//							Notification notification = builder.build();
							
							Notification notification = new Notification.Builder(PushService.this)
																.setContentIntent(contentIntent)
																.setTicker(contents[3])
																.setContentTitle(contents[3])
																.setContentText(contents[4])
																.setAutoCancel(true)
																.setDefaults(Notification.DEFAULT_ALL)
																.getNotification();
							mn.notify((int) System.currentTimeMillis(),notification);
						}
					}
				}
			}, (long) 1 * 1000, (long) 60 * 1000);
		}

		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
}
