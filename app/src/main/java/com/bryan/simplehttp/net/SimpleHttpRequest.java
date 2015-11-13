

package com.bryan.simplehttp.net;

import android.os.Handler;
import android.util.Pair;

import com.alibaba.fastjson.JSON;
import com.bryan.simplehttp.net.callback.RequestCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 简单封装Http的操作，不使用第三方类库
 * 支持https
 * 模型解析使用了FastJson，也可以不用
 *
 * Authro：Cxb
 */

public abstract  class SimpleHttpRequest {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);
    public  static Executor taskExecutor=new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue);


    protected String url;
    protected Map<String, String> params;
    protected RequestCallback callBack;
    private Handler mHandler=new Handler();
    protected  int timeOut=3000;
    protected  String charset="UTF-8";
    protected HttpURLConnection conn;




    public SimpleHttpRequest(String url,Map<String, String> params,RequestCallback callBack){
        this.url=url;
        this.params=params;
        this.callBack=callBack;
    }

    public Handler getHandler(){
        return  mHandler;
    }

    public static class Builder {
        private String url;
        private Map<String, String> params;
        private Pair<String, File>[] files;

        private String destFileDir;
        private String destFileName;
        private int timeOut;


        public Builder url(String url)
        {
            this.url = url;
            return this;
        }
        public Builder timeOut(int timeOut)
        {
            this.timeOut = timeOut;
            return this;
        }

        public Builder params(Map<String, String> params)
        {
            this.params = params;
            return this;
        }

        public Builder files(Pair<String, File>[] files){
            this.files=files;
            return  this;
        }

        public Builder destFileDir(String destFileDir){
            this.destFileDir=destFileDir;
            return this;
        }

        public Builder destFileName(String destFileName){
            this.destFileName=destFileName;
            return  this;
        }

        public SimpleHttpRequest get(RequestCallback callBack){
            SimpleGetHttpRequest request=new SimpleGetHttpRequest(url,params,callBack);
            request.timeOut=timeOut<=0?3000:timeOut;
            request.asynExecute();
            return  request;
        }

        public SimpleHttpRequest post(RequestCallback callBack){
            SimplePostHttpRequest request=new SimplePostHttpRequest(url,params,callBack);
            request.timeOut=timeOut<=0?3000:timeOut;
            request.asynExecute();
            return  request;
        }

        public SimpleHttpRequest download(RequestCallback callBack){
            SimpleDownloadRequest request=new SimpleDownloadRequest(url,params,callBack,destFileName,destFileDir);
            request.timeOut=timeOut<=0?3000:timeOut;
            request.asynExecute();
            return  request;
        }

        public SimpleHttpRequest upload(RequestCallback callBack){
            SimpleUploadRequest request=new SimpleUploadRequest(url,params,files,callBack);
            request.timeOut=timeOut<=0?3000:timeOut;
            request.asynExecute();
            return  request;
        }


    }

    protected  void asynExecute(){
        if(callBack==null)  {
            callBack=RequestCallback.DEFAULT_RESULT_CALLBACK;
        }
        callBack.onStart();
        taskExecutor.execute(runnable);
    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                initConnection();
                buildRequestBody();
                if(conn.getResponseCode()>400 && conn.getResponseCode()<599){
                    throw new RuntimeException(conn.getResponseCode()+" "+conn.getResponseMessage());
                }
                execute();
            } catch (Exception e) {
                sendError(e);
            }

        }
    };

    protected  void sendError(final Exception ex){
        if(callBack==null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onError(ex);
            }
        });
    }
    protected  void sendSuccess(final Object obj){
        if(callBack==null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(obj);
            }
        });
    }

    protected  void sendProgress(final long total, final long current, final boolean isUploading){
        if(callBack==null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onProgress(total, current, isUploading);
            }
        });
    }


    protected String appendParams(Map<String, String> params)
    {
       if (params==null || params.isEmpty()){
           return  null;
       }
        StringBuilder sb = new StringBuilder();
        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    protected  void execute() throws Exception{
        InputStream is = conn.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        baos.close();
        is.close();
        conn.disconnect();
        String body = new String(baos.toByteArray(), charset);
        if (callBack.mType == String.class) {
            sendSuccess(body);
        } else {
            Object object = JSON.parseObject(body, callBack.mType);
            sendSuccess(object);
        }
    }


    public static TrustManager myX509TrustManager = new X509TrustManager() {


        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }
    };


    protected abstract void initConnection() throws Exception;
    protected abstract void buildRequestBody() throws Exception;
}
