package com.bryan.simplehttp.callback;

import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Author：Cxb on 2015/12/23 14:32
 */
public class SimpleType<T> {
    private final Type type;

    protected SimpleType(){
        Type superClass = getClass().getGenericSuperclass();

        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

    public final static Type LIST_STRING = new TypeReference<List<String>>() {}.getType();
}
