package com.bryan.simplehttp.net;


import com.bryan.simplehttp.net.callback.RequestCallback;
import com.bryan.simplehttp.net.callback.SimpleType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

public class SimpleDownloadRequest  extends  SimpleGetHttpRequest{

    private String destFileDir;
    private String destFileName;

    public SimpleDownloadRequest(String url, Map<String, String> params, RequestCallback callBack, String destFileName,String destFileDir) {
        super(url, params, callBack);
        this.destFileDir=destFileDir;
        this.destFileName=destFileName;
    }

    @Override
    protected  <T>T execute(SimpleType<T> resultType) throws Exception {
        InputStream is = conn.getInputStream();
        File desFile=new File(destFileDir);
        if(!desFile.exists()){
            desFile.mkdirs();
        }
        FileOutputStream fos=new FileOutputStream(new File(destFileDir,destFileName));
        long flength=conn.getContentLength();
        sendProgress(flength,0,false);
        byte[] buf = new byte[1024];
        int len;
        long current=0;
        while ((len= is.read(buf)) != -1) {
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
