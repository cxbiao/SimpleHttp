package com.bryan.simplehttp.net;

import android.text.TextUtils;

import com.bryan.simplehttp.net.callback.RequestCallback;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


public class SimpleGetHttpRequest  extends  SimpleHttpRequest{

    public SimpleGetHttpRequest(String url, Map<String, String> params, RequestCallback callBack) {
        super(url, params, callBack);
    }

    @Override
    protected void initConnection() throws Exception {
        String getStr = appendParams(params);
        if(!TextUtils.isEmpty(getStr)){
            url+="?"+getStr;
        }
        URL netUrl = new URL(url);
        conn = (HttpURLConnection) netUrl.openConnection();
        if (url.startsWith("https")){
            //设置SSLContext
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, new SecureRandom());
            HttpsURLConnection httpsConn=
                    (HttpsURLConnection)conn;
            //设置套接工厂
            httpsConn.setSSLSocketFactory(sslcontext.getSocketFactory());
            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
        conn.setUseCaches(false);
        conn.setConnectTimeout(timeOut);
        conn.setRequestMethod("GET");
    }

    @Override
    protected void buildRequestBody() throws Exception {

    }



}
