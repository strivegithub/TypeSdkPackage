package com.type.sdk.exception;  
  
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKLogger;

/** 
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告. 
 *  
 * @author user 
 *  
 */
public class CrashHandler extends Exception implements UncaughtExceptionHandler {

	// CrashHandler 实例
	private static CrashHandler INSTANCE = new CrashHandler();

	// 程序的 Context 对象
	private Context mContext;

	// 系统默认的 UncaughtException 处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

	/** 定义异常类型 */
	public final static byte TYPE_NETWORK 	= 0x01;
	public final static byte TYPE_SOCKET	= 0x02;
	public final static byte TYPE_HTTP_CODE	= 0x03;
	public final static byte TYPE_HTTP_ERROR= 0x04;
	public final static byte TYPE_XML	 	= 0x05;
	public final static byte TYPE_IO	 	= 0x06;
	public final static byte TYPE_RUN	 	= 0x07;

	/** 保证只有一个 CrashHandler 实例 */
	private CrashHandler() {
	}
	
	private CrashHandler(byte type, int code, Exception excp) {
		super(excp);
	}

	/** 获取 CrashHandler 实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 *
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
	
		// 获取系统默认的 UncaughtException 处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	
		// 设置该 CrashHandler 为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当 UncaughtException 发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		TypeSDKLogger.e( "uncaughtException");
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				Log.e(TAG, "error : ", e);
//			}
			TypeSDKLogger.e("退出程序");
			// 退出程序
//			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	public static boolean flag = true;
	/**
	 * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
	 * 
	 * @param ex
	 * @return true：如果处理了该异常信息；否则返回 false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
			
		TypeSDKLogger.e(ex.getMessage().toString());
		Intent intent = new Intent();  
        intent.setAction("com.uccrash");  
        intent.putExtra("ex", ex.getMessage().toString());  
        mContext.sendBroadcast(intent);
		
		// 使用 Toast 来显示异常信息
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				Toast.makeText(mContext, "很抱歉，程序出现异常，即将退出。", Toast.LENGTH_LONG).show();
//				Looper.loop();
//			}
//		}.start();

		
		if (flag) {
			flag = false;
			
			TypeSDKLogger.i( "收集设备参数信息");
		
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		
		}
		return true;
	}

	/**
	 * 收集设备参数信息
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);

			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
				infos.put("availMemory", availMemory);
			}
		} catch (NameNotFoundException e) {
			TypeSDKLogger.e("an error occured when collect package info", e);
		}

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
				TypeSDKLogger.e("an error occured when collect crash info", e);
			}
		}
	}

	// 获取可用运存大小
		public static long getAvailMemory(Context context) {
			// 获取android当前可用内存大小
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			MemoryInfo mi = new MemoryInfo();
			am.getMemoryInfo(mi);
			// mi.availMem; 当前系统的可用内存
			// return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
			System.out.println("可用内存:" + mi.availMem / (1024 * 1024) + "M");
			availMemory = mi.availMem / (1024 * 1024) + "M";
			return mi.availMem / (1024 * 1024);
		}
	
	public static String fileName;
	public static String path;
	public static String sdkName = "sdkname";
	public static String availMemory = "null";
	
	/**
	 * 保存错误信息到文件中
	*
	 * @param ex
	 * @return  返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			fileName = "crash-" + sdkName + "-" + time + "-" + timestamp + ".log";
			TypeSDKLogger.e("fileName:" + fileName);
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				path = "/sdcard/." + BaseMainActivity.sdkName + "/CrashLogs/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}

			return fileName;
		} catch (Exception e) {
			TypeSDKLogger.e("an error occured while writing file...", e);
		}

		return null;
	}
	
	public static CrashHandler http(int code) {
		return new CrashHandler(TYPE_HTTP_CODE, code, null);
	}
	
	public static CrashHandler http(Exception e) {
		return new CrashHandler(TYPE_HTTP_ERROR, 0 ,e);
	}

	public static CrashHandler socket(Exception e) {
		return new CrashHandler(TYPE_SOCKET, 0 ,e);
	}
	
	public static CrashHandler io(Exception e) {
		if(e instanceof UnknownHostException || e instanceof ConnectException){
			return new CrashHandler(TYPE_NETWORK, 0, e);
		}
		else if(e instanceof IOException){
			return new CrashHandler(TYPE_IO, 0 ,e);
		}
		return run(e);
	}
	
	public static CrashHandler xml(Exception e) {
		return new CrashHandler(TYPE_XML, 0, e);
	}
	
	public static CrashHandler network(Exception e) {
		if(e instanceof UnknownHostException || e instanceof ConnectException){
			return new CrashHandler(TYPE_NETWORK, 0, e);
		}
		else if(e instanceof ConnectException){
			return http(e);
		}
		else if(e instanceof SocketException){
			return socket(e);
		}
		return http(e);
	}
	
	public static CrashHandler run(Exception e) {
		return new CrashHandler(TYPE_RUN, 0, e);
	}
	
}