package com.type.sdk.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.type.sdk.android.TypeSDKLogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager.getActiveNetworkInfo();
		String netLinkInfo = "网络连接正常";
		if (mobNetInfo != null) {
			if (!mobNetInfo.isConnected()) {
				TypeSDKLogger.w( "当前无网络连接");
				netLinkInfo = "当前无网络连接";
			} else if (mobNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				// 改变背景或者 处理网络的全局变量
				TypeSDKLogger.w("Wifi已连接");
				netLinkInfo = "Wifi已连接";
			} else if (mobNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				TypeSDKLogger.w("数据连接已开");
				netLinkInfo = "数据连接已开";
			}
		}else{
				netLinkInfo = "当前无网络连接";
		}
		try {
			writeFileSdcardFile(netLinkInfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeFileSdcardFile(String write_str) throws IOException {
		try {

			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss",Locale.getDefault());
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String str = formatter.format(curDate);
			String fileName = "NetworkLog.txt";
			File txtFolder = new File(Environment.getExternalStorageDirectory().toString()+"/Logs");
			if(!txtFolder.exists()){
				txtFolder.mkdir();
			}
			File textFile = new File(txtFolder.getAbsolutePath(),fileName);
			String oldText = readFileSdcardFile(textFile);
			FileOutputStream fout = new FileOutputStream(textFile);
			String curText = (str + "\n" + write_str+"\r\n");
			byte[] bytes = (oldText+curText).getBytes();
			fout.write(bytes);
			fout.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String readFileSdcardFile(File mFile) throws IOException{   
		  String res="";   
		  try{   
		         FileInputStream fin = new FileInputStream(mFile);   
		  
		         int length = fin.available();   
		  
		         byte [] buffer = new byte[length];   
		         fin.read(buffer);       
		  
		         res = new String(buffer, "UTF-8");   
		  
		         fin.close();       
		        }   
		  
		        catch(Exception e){   
		         e.printStackTrace();   
		        }   
		        return res;   
		}   
}
