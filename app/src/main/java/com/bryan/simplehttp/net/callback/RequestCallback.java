package com.bryan.simplehttp.net.callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract  class RequestCallback<T> {
    public Type mType;

    public RequestCallback() {
        mType = getSuperclassTypeParameter(getClass());
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    public void onStart() {

    }

    public abstract void onSuccess(T response);



    public void onProgress(long total, long current, boolean isUploading) {

    }

    public void onCancel(){}

    public  abstract void onError(Exception e);


    public static final RequestCallback<String> DEFAULT_RESULT_CALLBACK = new RequestCallback<String>() {

        @Override
        public void onSuccess(String response) {

        }

        @Override
        public void onError(Exception e) {

        }
    };


}