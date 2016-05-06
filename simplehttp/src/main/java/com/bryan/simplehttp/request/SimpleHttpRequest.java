

package com.bryan.simplehttp.request;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.bryan.simplehttp.callback.RequestCallback;
import com.bryan.simplehttp.callback.SimpleType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * a simple http libray ,not use another library
 * support https
 * default use fastjsom for model parse
 * Authro：Cxb
 */

public abstract class SimpleHttpRequest {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);
    public static Executor taskExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue);


    protected String url;
    protected List<FormParam> params;
    protected Map<String,String> headers;
    protected String contentType;
    protected RequestCallback callBack;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    protected int timeOut = 3000;
    protected String charset = "UTF-8";
    protected HttpURLConnection conn;
    protected boolean isCancel;


    public SimpleHttpRequest(String url,String contentType, List<FormParam> params,Map<String,String> headers, RequestCallback callBack) {
        this.url = url;
        this.contentType=contentType;
        this.params = params;
        this.headers=headers;
        this.callBack = callBack;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public static class Builder {
        private String url;
        private List<FormParam> params;
        private List<FileParam> files;
        protected Map<String,String> headers;
        private String destFileDir;
        private String destFileName;
        private int timeOut;
        private String contentType;

        //for post
        private String content;
        private byte[] bytes;
        private File file;


        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder timeOut(int timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        public Builder contentType(String contentType){
            this.contentType=contentType;
            return  this;
        }

        public Builder params(List<FormParam> params) {
            this.params = params;
            return this;
        }

        public Builder addParam(String key,String value){
            if(this.params==null || this.params.size()==0){
                this.params=new ArrayList<>();
            }
            params.add(new FormParam(key,value));
            return  this;
        }

        public Builder files(List<FileParam> files) {
            this.files = files;
            return this;
        }

        public Builder addFile(String key,String fileName,File file){
            if(this.files==null || this.files.size()==0){
                files=new ArrayList<>();
            }
            files.add(new FileParam(key,fileName,file));
            return this;
        }

        public Builder headers(Map<String,String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key,String value){
            if(this.headers==null || this.headers.size()==0){
                headers=new LinkedHashMap<>();
            }
            headers.put(key,value);
            return this;
        }

        public Builder destFileDir(String destFileDir) {
            this.destFileDir = destFileDir;
            return this;
        }

        public Builder destFileName(String destFileName) {
            this.destFileName = destFileName;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder bytes(byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        /**
         * async get request
         *
         * @param callBack
         * @return
         */
        public SimpleHttpRequest get(RequestCallback callBack) {
            SimpleGetHttpRequest request = new SimpleGetHttpRequest(url,contentType, params, headers,callBack);
            request.timeOut = timeOut <= 0 ? 3000 : timeOut;
            request.asynExecute();
            return request;
        }

        /**
         * Sync get request
         *
         * @param resultType
         * @param <T>
         * @return
         */
        public <T> T getSync(SimpleType<T> resultType) throws Exception {
            SimpleGetHttpRequest request = new SimpleGetHttpRequest(url,contentType, params, headers,null);
            request.timeOut = timeOut <= 0 ? 3000 : timeOut;
            return request.syncExecute(resultType);
        }

        /**
         * async post request
         *
         * @param callBack
         * @return
         */
        public SimpleHttpRequest post(RequestCallback callBack) {
            SimplePostHttpRequest request = new SimplePostHttpRequest(url,contentType, params, headers,content, bytes, file, callBack);
            request.timeOut = timeOut <= 0 ? 3000 : timeOut;
            request.asynExecute();
            return request;
        }

        /**
         * Sync post request
         *
         * @param resultType
         * @param <T>
         * @return
         */
        public <T> T postSync(SimpleType<T> resultType) throws Exception {
            SimplePostHttpRequest request = new SimplePostHttpRequest(url,contentType, params,headers, content, bytes, file, null);
            request.timeOut = timeOut <= 0 ? 3000 : timeOut;
            return request.syncExecute(resultType);
        }

        /**
         * file download
         *
         * @param callBack
         * @return
         */
        public SimpleHttpRequest download(RequestCallback callBack) {
            SimpleDownloadRequest request = new SimpleDownloadRequest(url,contentType, params,headers, callBack, destFileName, destFileDir);
            request.timeOut = timeOut <= 0 ? 3000 : timeOut;
            request.asynExecute();
            return request;
        }

        /**
         * async file upload
         *
         * @param callBack
         * @return
         */
        public SimpleHttpRequest upload(RequestCallback callBack) {
            SimpleUploadRequest request = new SimpleUploadRequest(url, contentType,params, files,headers, callBack);
            request.timeOut = timeOut <= 0 ? 3000 : timeOut;
            request.asynExecute();
            return request;
        }

        /**
         * sync file upload
         *
         * @param resultType
         * @param <T>
         * @return
         */
        public <T> T uploadSync(SimpleType<T> resultType) throws Exception {
            SimpleUploadRequest request = new SimpleUploadRequest(url, contentType,params, files,headers, null);
            request.timeOut = timeOut <= 0 ? 3000 : timeOut;
            return request.syncExecute(resultType);
        }


    }

    protected void asynExecute() {
        if (callBack == null) {
            callBack = RequestCallback.DEFAULT_RESULT_CALLBACK;
        }
        callBack.onStart();
        taskExecutor.execute(runnable);
    }


    protected <T> T syncExecute(SimpleType<T> resultType) throws Exception {
        if (isCancel) {
            sendCancel();
            return null;
        }
        initConnection();
        writeHeaders();
        if (isCancel) {
            sendCancel();
            return null;
        }
        buildRequestBody();
        if (isCancel) {
            sendCancel();
            return null;
        }
        if (conn.getResponseCode() > 400 && conn.getResponseCode() < 599) {
            throw new RuntimeException(conn.getResponseCode() + " " + conn.getResponseMessage());
        }
        return execute(resultType);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (isCancel) {
                    sendCancel();
                    return;
                }
                initConnection();
                writeHeaders();
                if (isCancel) {
                    sendCancel();
                    return;
                }
                buildRequestBody();
                if (conn.getResponseCode() > 400 && conn.getResponseCode() < 599) {
                    throw new RuntimeException(conn.getResponseCode() + " " + conn.getResponseMessage());
                }
                if (isCancel) {
                    sendCancel();
                    return;
                }
                execute(null);
            } catch (Exception e) {
                sendError(e);
            }

        }
    };

    protected void sendError(final Exception ex) {
        if (callBack == null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onError(ex);
            }
        });
    }

    protected void sendCancel() {
        if (callBack == null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onCancel();
            }
        });
    }

    protected void sendSuccess(final Object obj) {
        if (callBack == null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onSuccess(obj);
            }
        });
    }

    protected void sendProgress(final long total, final long current, final boolean isUploading) {
        if (callBack == null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onProgress(total, current, isUploading);
            }
        });
    }


    protected String appendParams(List<FormParam> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            for(FormParam param:params){
                sb.append(param.key).append("=").append(param.value).append("&");
            }
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    protected <T> T execute(SimpleType<T> resultType) throws Exception {
        InputStream is = conn.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            if (isCancel) {
                sendCancel();
                return null;
            }
            baos.write(buffer, 0, length);
        }
        baos.close();
        is.close();
        conn.disconnect();
        String body = new String(baos.toByteArray(), charset);
        Type type = (callBack == null) ? resultType.getType() : callBack.mType;
        if (type == String.class) {
            if (resultType != null) return (T) body;
            else {
                sendSuccess(body);
            }
        } else {
            Object object = JSON.parseObject(body, type);
            if (resultType != null) return (T) object;
            else {
                sendSuccess(object);
            }
        }
        return null;
    }


    public void cancel() {
        isCancel = true;
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

    protected void writeHeaders() throws Exception{
        if(!TextUtils.isEmpty(contentType)){
            conn.setRequestProperty("Content-Type",contentType);
        }
        if(headers==null || headers.size()==0){
            return;
        }
        for(Map.Entry<String,String> entry:headers.entrySet()){
            conn.setRequestProperty(entry.getKey(),entry.getValue());
        }

    }
}
