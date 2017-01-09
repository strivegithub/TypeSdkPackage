/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.app.Activity;
import android.os.Environment;

import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKLogger;
import com.type.utils.SystemUtils;

/**
 *ftp 上传工具 主要用作crash的上传
 * @author TypeSDK
 * @created 2015年10月16日 下午3:04:25
 * @version 1.0
 */
public class FtpUploadUtil {

	public static String fileFolder = "CrashLog";
	public static String crashURL = "";
	public static String crashPort = "";
	public static String userName = "";
	public static String userPassword = "";
	/**
	 * 通过ftp上传文件
	 * 
	 * @param url
	 *            ftp服务器地址 如： 192.168.1.110
	 * @param port
	 *            端口如 ： 21
	 * @param username
	 *            登录名
	 * @param password
	 *            密码
	 * @param remotePath
	 *            上到ftp服务器的磁盘路径
	 * @param fileNamePath
	 *            要上传的文件路径
	 * @param fileName
	 *            要上传的文件名
	 * @return
	 */
	public static String ftpUpload(String url, String port, String username,
			String password, String remotePath,String remoteChildPath, String fileNamePath,
			String fileName) {
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		String returnMessage = "0";
		try {
			//Log.e("Type", "connect begin");
			try{
				ftpClient.connect(url, Integer.parseInt(port));
			}catch(Exception e){
				TypeSDKLogger.e(e.toString());
				return "";
			}
			
			//Log.e("Type", "login begin");
			boolean loginResult = ftpClient.login(username, password);
			//Log.e("Type", loginResult?"true":"false");
			int returnCode = ftpClient.getReplyCode();
			if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
				ftpClient.makeDirectory(remotePath);
				// 设置上传目录
				ftpClient.changeWorkingDirectory(remotePath);
				TypeSDKLogger.i("ftpClient remoteChildPath:"+remoteChildPath);
				ftpClient.makeDirectory(remoteChildPath);
				// 设置上传目录
				ftpClient.changeWorkingDirectory(remoteChildPath);
				ftpClient.setBufferSize(1024);
				//ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.enterLocalPassiveMode();
				fis = new FileInputStream(fileNamePath + fileName);
				try {
					if (ftpClient.storeFile(fileName, fis)) {  // && ftpClient.completePendingCommand()
						returnMessage = "1"; // 上传成功
						TypeSDKLogger.i("上传成功");
					}else {
						returnMessage = "0"; // 上传失败
						TypeSDKLogger.i("上传失败");
					}
				} catch (Exception e) {
					TypeSDKLogger.e("upload exception:"+e.toString());
					// TODO Auto-generated catch block
					e.printStackTrace();
					returnMessage = "0"; // 上传失败
				}

				returnMessage = "1"; // 上传成功
			} else {// 如果登录失败
				returnMessage = "0";
			}

		} catch (IOException e) {
			e.printStackTrace();
			TypeSDKLogger.i("FTP客户端出错！");
//			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			// IOUtils.closeQuietly(fis);
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				TypeSDKLogger.i("关闭FTP连接发生异常！");
//				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		return returnMessage;
	}
	public static boolean uploadFiles(Activity appActivity){
		if(fileFolder.isEmpty()||fileFolder==""){
			fileFolder = "CrashLog";
		}
		TypeSDKLogger.i("fileFolder:"+fileFolder);
		TypeSDKLogger.i(SystemUtils.getNetworkType(appActivity));
		if(SystemUtils.getNetworkType(appActivity).equals("Wi-Fi")){
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				TypeSDKLogger.i("MEDIA_MOUNTED");
				boolean isUpload = false;
				String childPath = "now_default";
				String path = "/sdcard/."+BaseMainActivity.sdkName+"/CrashLogs/";
				File file = new File(path);
				if (file.exists()&&file.isDirectory()) {
					TypeSDKLogger.i("file.exists()&&file.isDirectory()");
					if(file.list().length>0){
						isUpload = true;
						Date curTime = new Date(System.currentTimeMillis());
						SimpleDateFormat formatter = new SimpleDateFormat(
								"yyyyMMddHHmmss");
						String firstUpTime = formatter.format(curTime);
						childPath = firstUpTime+"_"+BaseMainActivity.sdkName;
						TypeSDKLogger.i("file.list().length>0");
						String[] strs = file.list();
						for(int i=0;i<strs.length;i++){
							File singleFile = new File(path+strs[i]);
							if (singleFile.length() <= 512*1024) {
								String result = ftpUpload(crashURL, crashPort, userName, userPassword, fileFolder,childPath, path, strs[i]);
								TypeSDKLogger.i("result:"+result);
								TypeSDKLogger.i("=================================");
								if(singleFile.isFile()&&result.equals("1")){
									singleFile.delete();
								}
							}else {
								TypeSDKLogger.i("file.length>1M");
							}
						}
						TypeSDKLogger.i("crash logs upload finished");
						
					}
				}
//				if(isUpload){
//					File gameLog = new File("/sdcard/."+BaseMainActivity.sdkName+"/CatchLogs/gameLog.txt");
//					String gameLogPath = "/sdcard/."+BaseMainActivity.sdkName+"/CatchLogs/";
//					if(gameLog.exists()&&gameLog.isFile()){
//						String result = ftpUpload("ftp.wjy.yinheshuyu.com", "21", "crash", "crashlog", fileFolder,childPath, gameLogPath, gameLog.getName());
//						if(result.equals("1")){
//							TypeSDKLogger.i("catch logs upload finished");
//							isUpload = false;
//							gameLog.delete();
//						}						
//					}
//				}				
				
			}

		}
		TypeSDKLogger.i("upload function finished");
		return true;
	}
}
