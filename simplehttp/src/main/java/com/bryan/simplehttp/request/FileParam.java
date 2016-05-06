package com.bryan.simplehttp.request;

import java.io.File;

/**
 * Author：Cxb on 2016/5/6 09:30
 */
public class FileParam {
    public String key;
    public String fileName;
    public File file;



    public FileParam(String key, String fileName,File file) {
        this.key = key;
        this.file = file;
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FileParam{" +
                "key='" + key + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
