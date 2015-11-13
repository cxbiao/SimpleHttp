package com.bryan.simplehttp.net;

import android.util.Pair;

import com.bryan.simplehttp.net.callback.RequestCallback;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


public class SimpleUploadRequest extends SimplePostHttpRequest {

    private static final String boundary = "--------hellobryan";
    private Pair<String, File>[] files;
    private DataOutputStream dos;

    public SimpleUploadRequest(String url, Map<String, String> params, Pair<String, File>[] files, RequestCallback callBack) {
        super(url, params, callBack);
        this.files = files;
    }

    @Override
    protected void initConnection() throws Exception {
        if(files==null || files.length==0){
            throw new IllegalArgumentException("the files cannot be null");
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
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    }

    @Override
    protected void buildRequestBody() throws Exception {
        dos = new DataOutputStream(conn.getOutputStream());
        writeStringParams();
        writeFileParams();
        paramsEnd();

    }


    //普通字符串数据
    private void writeStringParams() throws Exception {

        Set<String> keySet = params.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext(); ) {
            String name = it.next();
            String value = params.get(name);
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"" + name
                    + "\"\r\n");
            dos.writeBytes("\r\n");
            dos.write(value.getBytes());
            dos.writeBytes("\r\n");
        }
    }

    //文件数据
    private void writeFileParams() {
        try {
            for (int i = 0; i < files.length; i++) {
                String name = files[i].first;
                File file = files[i].second;
                String encodeName = new String(file.getName().getBytes("UTF-8"), "ISO-8859-1");
                dos.writeBytes("--" + boundary + "\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"" + name
                        + "\"; filename=\"" + encodeName + "\"\r\n");
                dos.writeBytes("Content-Type: " + guessMimeType(file.getName()) + "\r\n");
                dos.writeBytes("\r\n");
                dos.write(getFileBytes(file));
                dos.writeBytes("\r\n");
            }

        } catch (Exception ex) {
            sendError(ex);
        }

    }

    //添加结尾数据
    private void paramsEnd() throws Exception {
        dos.writeBytes("--" + boundary + "--" + "\r\n");
        dos.writeBytes("\r\n");
    }

    //把文件转换成字节数组
    private byte[] getFileBytes(File f) throws Exception {
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


    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
}
