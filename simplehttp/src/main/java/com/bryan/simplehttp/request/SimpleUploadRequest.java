package com.bryan.simplehttp.request;

import android.text.TextUtils;

import com.bryan.simplehttp.callback.RequestCallback;

import java.io.DataOutputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


public class SimpleUploadRequest extends SimplePostHttpRequest {

    private static final String boundary = "--------hellobryan";
    private  List<FileParam> files;
    private DataOutputStream dos;

    public SimpleUploadRequest(String url,String contentType, List<FormParam> params, List<FileParam> files, Map<String,String> headers, RequestCallback callBack) {
        super(url,contentType, params, headers,null,null,null,callBack);
        this.files = files;
    }

    @Override
    protected void initConnection() throws Exception {
        if(files==null || files.size()==0){
            throw new IllegalArgumentException("the files cannot be null");
        }
        URL netUrl = new URL(url);
        conn = (HttpURLConnection) netUrl.openConnection();
        if (url.startsWith("https")){
            //set SSLContext
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, new SecureRandom());
            HttpsURLConnection httpsConn=
                    (HttpsURLConnection)conn;
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
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

    }

    @Override
    protected void buildRequestBody() throws Exception {
        dos = new DataOutputStream(conn.getOutputStream());
        writeStringParams();
        writeFileParams();
        paramsEnd();

    }


    //simple form data
    private void writeStringParams() throws Exception {

        if(params==null || params.isEmpty()){
            return;
        }
        for (FormParam param:params ) {
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"" + param.key
                    + "\"\r\n");
            dos.writeBytes("\r\n");
            dos.write(param.value.getBytes());
            dos.writeBytes("\r\n");
        }
    }

    //file bytes
    private void writeFileParams() {
        try {
            for (FileParam fileParam:files) {
                String fname = TextUtils.isEmpty(fileParam.fileName)?fileParam.file.getName():fileParam.fileName;
                String encodeName = new String(fname.getBytes("UTF-8"), "ISO-8859-1");
                dos.writeBytes("--" + boundary + "\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"" + fileParam.key
                        + "\"; filename=\"" + encodeName + "\"\r\n");
                dos.writeBytes("Content-Type: " + guessMimeType(fileParam.file.getName()) + "\r\n");
                dos.writeBytes("\r\n");
                dos.write(getFileBytes(fileParam.file));
                dos.writeBytes("\r\n");
            }

        } catch (Exception ex) {
            sendError(ex);
        }

    }

    //append end data
    private void paramsEnd() throws Exception {
        dos.writeBytes("--" + boundary + "--" + "\r\n");
        dos.writeBytes("\r\n");
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
