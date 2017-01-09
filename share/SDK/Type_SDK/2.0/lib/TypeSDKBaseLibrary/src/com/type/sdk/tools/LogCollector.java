/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */
package com.type.sdk.tools;

import java.io.File;

import android.os.Environment;

import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKLogger;

public class LogCollector implements Runnable {

	private String catchTags;
    public LogCollector(String strs){
    	this.catchTags = strs;
    }
    private String logBuffers(String str){
    	String[] strs = str.split(";");
    	String logBuffer = "";
    	for(String s : strs){
    		logBuffer+=(s+":V ");
    	}
		return logBuffer;
    	
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String fileName1 = "GameLog.txt";
		File txtFolder = new File(Environment.getExternalStorageDirectory()
				.toString() + "/."+BaseMainActivity.sdkName+"/CatchLogs");
		if (!txtFolder.exists()) {
			txtFolder.mkdirs();
		}
		if(!catchTags.equals("")&&!catchTags.isEmpty()&&catchTags.contains(";")){
			TypeSDKLogger.i("logBuffers:"+logBuffers(catchTags));
			String cmds = "logcat -f " + txtFolder + File.separator + fileName1
					+ " -r 20 -v time "+logBuffers(catchTags)+"*:S";
			try {
				Runtime.getRuntime().exec(cmds);
			} catch (Exception e) {
				TypeSDKLogger.i("CollectorThread == >" + e.getMessage(), e);
			}	
		}
		
	}

}
