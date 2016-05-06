package com.bryan.simplehttp.request;

import android.text.TextUtils;

import com.bryan.simplehttp.callback.RequestCallback;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


public class SimpleGetHttpRequest  extends  SimpleHttpRequest{

    public SimpleGetHttpRequest(String url,String contentType, List<FormParam> params, Map<String,String> headers,RequestCallback callBack) {
        super(url,contentType, params, headers,callBack);
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
            //set SSLContext
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, new SecureRandom());
            HttpsURLConnection httpsConn=
                    (HttpsURLConnection)conn;
            //set socketFactory
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
        conn.setReadTimeout(timeOut);
        conn.setRequestMethod("GET");
    }

    @Override
    protected void buildRequestBody() throws Exception {

    }



}
