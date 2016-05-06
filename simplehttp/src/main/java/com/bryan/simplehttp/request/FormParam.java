package com.bryan.simplehttp.request;

/**
 * Author：Cxb on 2016/5/6 09:30
 */
public class FormParam {
    public String key;
    public String value;


    public FormParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "FormParam{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
