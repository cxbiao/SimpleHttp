package com.bryan.simplehttp.net;


import com.bryan.simplehttp.net.callback.RequestCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

public class SimplePostHttpRequest extends SimpleHttpRequest {


    private String content;
    private byte[] bytes;
    private File file;

    private int type = 0;
    private static final int TYPE_PARAMS = 1;
    private static final int TYPE_STRING = 2;
    private static final int TYPE_BYTES = 3;
    private static final int TYPE_FILE = 4;

    private final String  MEDIA_TYPE_FORM = "application/x-www-form-urlencoded";
    private final String  MEDIA_TYPE_STREAM = "application/octet-stream";
    private final String  MEDIA_TYPE_STRING = "text/plain;charset=utf-8";

    public SimplePostHttpRequest(String url, Map<String, String> params, String content,byte[] bytes,File file,RequestCallback callBack) {
        super(url, params, callBack);
        this.content = content;
        this.bytes = bytes;
        this.file = file;
        validParams();
    }


    protected void validParams()
    {
        int count = 0;
        if (params != null && !params.isEmpty())
        {
            type = TYPE_PARAMS;
            count++;
        }
        if (content != null)
        {
            type = TYPE_STRING;
            count++;
        }
        if (bytes != null)
        {
            type = TYPE_BYTES;
            count++;
        }
        if (file != null)
        {
            type = TYPE_FILE;
            count++;
        }

        if (count <= 0 || count > 1)
        {
            throw new IllegalArgumentException("the params , content , file , bytes must has one and only one .");
        }
    }

    @Override
    protected void initConnection() throws Exception {
        if(params==null || params.isEmpty()){
            throw new IllegalArgumentException("the parmas cannot be null");
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
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(timeOut);
        conn.setReadTimeout(timeOut);
        conn.setRequestMethod("POST");



    }

    @Override
    protected void buildRequestBody() throws Exception {

        OutputStream os;
        switch (type){
            case TYPE_PARAMS:
                conn.setRequestProperty("Content-Type", MEDIA_TYPE_FORM);
                String postStr = appendParams(params);
                os=conn.getOutputStream();
                os.write(postStr.getBytes("UTF-8"));
                break;
            case TYPE_STRING:
                conn.setRequestProperty("Content-Type", MEDIA_TYPE_STRING);
                os=conn.getOutputStream();
                os.write(content.getBytes("UTF-8"));
                break;
            case TYPE_BYTES:
                conn.setRequestProperty("Content-Type", MEDIA_TYPE_STREAM);
                os=conn.getOutputStream();
                os.write(bytes);
                break;
            case TYPE_FILE:
                conn.setRequestProperty("Content-Type", MEDIA_TYPE_STREAM);
                os=conn.getOutputStream();
                os.write(getFileBytes(file));
                break;
            default:
                break;
        }


    }

    //把文件转换成字节数组
    protected byte[] getFileBytes(File f) throws Exception {
        long flength=f.length();
        sendProgress(flength,0,true);
        FileInputStream in = new FileInputStream(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf= new byte[1024];
        int len;
        long current=0;
        while ((len = in.read(buf)) != -1) {
            current+=len;
            out.write(buf, 0, len);
            sendProgress(flength,current,true);
        }
        in.close();
        out.close();
        return out.toByteArray();
    }


}
