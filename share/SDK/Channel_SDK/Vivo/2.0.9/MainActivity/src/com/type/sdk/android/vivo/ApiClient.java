package com.type.sdk.android.vivo;

import android.util.Log;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.exception.CrashHandler;

import java.io.*;
import java.util.*;

/**
 * API客户端接口：用于访问网络数据
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

    public static final String UTF_8 = "UTF-8";
    public static final String DESC = "descend";
    public static final String ASC = "ascend";

    private final static int TIMEOUT_CONNECTION = 20000;
    private final static int TIMEOUT_SOCKET = 15000;
    private final static int RETRY_TIME = 3;
    
    public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";
	public final static String HOST = "123.59.53.211:30000";
	public final static String GET_BILL_NUMBER = HTTP + HOST + URL_SPLITTER + "A1/Vivo/CreateChannelOrder/";

    private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
        // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        // httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler());
        // 设置 连接超时时间
        httpClient.getHttpConnectionManager().getParams()
                .setConnectionTimeout(TIMEOUT_CONNECTION);
        // 设置 读数据超时时间
        httpClient.getHttpConnectionManager().getParams()
                .setSoTimeout(TIMEOUT_SOCKET);
        // 设置 字符集     
        httpClient.getParams().setContentCharset(UTF_8);
        return httpClient;
    }

    private static GetMethod getHttpGet(String url) {
        GetMethod httpGet = new GetMethod(url);
        // 设置 请求超时时间
        httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
        // httpGet.setRequestHeader("Host", URLs.HOST);
        httpGet.setRequestHeader("Connection", "Keep-Alive");
        // httpGet.setRequestHeader("Cookie", cookie);
        // httpGet.setRequestHeader("User-Agent", userAgent);
        return httpGet;
    }

    private static GetMethod getHttpGet(String url, int socketTime) {
        GetMethod httpGet = new GetMethod(url);
        // 设置 请求超时时间
        httpGet.getParams().setSoTimeout(socketTime);
        // httpGet.setRequestHeader("Host", URLs.HOST);
        httpGet.setRequestHeader("Connection", "Keep-Alive");
        // httpGet.setRequestHeader("Cookie", cookie);
        // httpGet.setRequestHeader("User-Agent", userAgent);
        return httpGet;
    }

    private static PostMethod getHttpPost(String url) {
        PostMethod httpPost = new PostMethod(url);
        // 设置 请求超时时间
        httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
        // httpPost.setRequestHeader("Cookie", "");
        httpPost.setRequestHeader("Connection", "Keep-Alive");
        return httpPost;
    }

    private static String _MakeURL(String p_url, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(p_url);
        if (url.indexOf("?") < 0)
            url.append('?');

        for (String name : params.keySet()) {
            url.append('&');
            url.append(name);
            url.append('=');
            url.append(String.valueOf(params.get(name)));
            // 不做URLEncoder处理
            // url.append(URLEncoder.encode(String.valueOf(params.get(name)),
            // UTF_8));
        }

        return url.toString().replace("?&", "?");
    }

    /**
     * get请求URL
     *
     * @param url
     * @throws CrashHandler
     */
    private static String http_get(String url) throws CrashHandler {

        HttpClient httpClient = null;
        GetMethod httpGet = null;

        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    throw CrashHandler.http(statusCode);
                }
                responseBody = httpGet.getResponseBodyAsString();
                // System.out.println("XMLDATA=====>"+responseBody);
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw CrashHandler.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw CrashHandler.network(e);
            } finally {
                // 释放连接
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);

		/*
         * responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
		 * if(responseBody.contains("result") &&
		 * responseBody.contains("errorCode") &&
		 * appContext.containsProperty("user.uid")){ try { Result res =
		 * Result.parse(new ByteArrayInputStream(responseBody.getBytes()));
		 * if(res.getErrorCode() == 0){ appContext.Logout();
		 * appContext.getUnLoginHandler().sendEmptyMessage(1); } } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */

        Log.i("info_out", "response:" + responseBody);
        return responseBody;
    }


    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param files
     * @throws CrashHandler
     */
    private static String _post(String url, Map<String, Object> params,
                                Map<String, File> files, boolean againFlag) throws CrashHandler {
        // System.out.println("post_url==> "+url);
        // String cookie = getCookie(appContext);
        // String userAgent = getUserAgent(appContext);

        HttpClient httpClient = null;
        PostMethod httpPost = null;
        // post表单参数处理
        int length = (params == null ? 0 : params.size())
                + (files == null ? 0 : files.size());
        Part[] parts = new Part[length];
        int i = 0;
        if (params != null)
            for (String name : params.keySet()) {
                parts[i++] = new StringPart(name, String.valueOf(params
                        .get(name)), UTF_8);
                // System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
            }
        if (files != null)
            for (String file : files.keySet()) {
                try {
                    parts[i++] = new FilePart(file, files.get(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // System.out.println("post_key_file==> "+file);
            }

        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(url);
                httpPost.setRequestEntity(new MultipartRequestEntity(parts,
                        httpPost.getParams()));
                int statusCode = httpClient.executeMethod(httpPost);
                if (statusCode != HttpStatus.SC_OK) {
                    throw CrashHandler.http(statusCode);
                }
                // else if(statusCode == HttpStatus.SC_OK)
                // {
                // Cookie[] cookies = httpClient.getState().getCookies();
                // String tmpcookies = "";
                // for (Cookie ck : cookies) {
                // tmpcookies += ck.toString()+";";
                // }
                // Log.i("info_out","cookie:"+tmpcookies);
                // if(appContext != null && tmpcookies != ""){
                // appContext.setProperty("cookie", tmpcookies);
                // appCookie = tmpcookies;
                // }
                // }

//               responseBody = new String(httpPost.getResponseBody(),UTF_8);
//             String  responseBody2 = new String(httpPost.getResponseBody(),"GBK");
//                Log.i("info_out","res"+responseBody2);
                responseBody = httpPost.getResponseBodyAsString();
                // System.out.println("XMLDATA=====>"+responseBody);
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw CrashHandler.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw CrashHandler.network(e);
            } finally {
                // 释放连接
                httpPost.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME && againFlag);

        // responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        // if(responseBody.contains("result") &&
        // responseBody.contains("errorCode") &&
        // appContext.containsProperty("user.uid")){
        // try {
        // Result res = Result.parse(new
        // ByteArrayInputStream(responseBody.getBytes()));
        // if(res.getErrorCode() == 0){
        // appContext.Logout();
        // appContext.getUnLoginHandler().sendEmptyMessage(1);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        return responseBody;
    }

    
    /**
     * 
     *
     * @return
     * @throws CrashHandler
     */
    public static String getBillNo(final String playerid, final String price, final String cporder, final String subject, final String sign, final String url)
            throws CrashHandler {
//        String newUrl = _MakeURL(LOGIN_VALIDATE_HTTP,
//                new HashMap<String, Object>() {
//                    {
//                        put("playerid", playerid);
//                        put("price", price);
//                        put("cporder", cporder);
//                        put("subject", subject);
//                    }
//                });
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("playerid", playerid);
    	map.put("price", price);
        map.put("cporder", cporder);
        map.put("subject", subject);
        map.put("sign", sign);
        String result = "";
        try {
        	TypeSDKLogger.e( map.toString());
            result = _post(url, map, null, false);
            TypeSDKLogger.e( "result:" + result);
        } catch (Exception e) {
//            if (e instanceof CrashHandler)
//                throw (CrashHandler) e;
//            throw CrashHandler.network(e);
        }
        return result;
    }

    
}
