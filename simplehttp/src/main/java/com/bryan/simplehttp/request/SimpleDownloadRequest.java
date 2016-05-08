package com.bryan.simplehttp.request;


import android.text.TextUtils;

import com.bryan.simplehttp.callback.RequestCallback;
import com.bryan.simplehttp.callback.SimpleType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class SimpleDownloadRequest  extends  SimpleGetHttpRequest{

    private String destFileDir;
    private String destFileName;

    public SimpleDownloadRequest(String url,String contentType, List<FormParam> params, Map<String,String> headers, RequestCallback callBack, String destFileName, String destFileDir) {
        super(url, contentType,params,headers, callBack);
        this.destFileDir=destFileDir;
        this.destFileName=destFileName;
    }

    @Override
    protected  <T>T execute(SimpleType<T> resultType) throws Exception {
        InputStream is = conn.getInputStream();
        if(TextUtils.isEmpty(destFileDir)){
            throw  new RuntimeException("destFileDir must not be null");
        }
        File desFile=new File(destFileDir);
        if(!desFile.exists()){
            desFile.mkdirs();
        }
        if(TextUtils.isEmpty(destFileName)){
            destFileName=new File(url).getName();
        }
        FileOutputStream fos=new FileOutputStream(new File(destFileDir,destFileName));
        long flength=conn.getContentLength();
        sendProgress(flength,0,false);
        byte[] buf = new byte[1024];
        int len;
        long current=0;
        while ((len= is.read(buf)) != -1) {
            if(isCancel) {
                sendCancel();
                return null;
            }
            current+=len;
            fos.write(buf, 0, len);
            sendProgress(flength,current,false);
        }
        fos.close();
        is.close();
        conn.disconnect();
        sendSuccess("200 OK");
        return null;

    }
}
