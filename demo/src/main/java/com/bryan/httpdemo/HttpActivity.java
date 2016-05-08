package com.bryan.httpdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bryan.simplehttp.callback.RequestCallback;
import com.bryan.simplehttp.request.FileParam;
import com.bryan.simplehttp.request.FormParam;
import com.bryan.simplehttp.request.SimpleHttpRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/13.
 */
public class HttpActivity extends AppCompatActivity {


    private static final String TAG = "HttpActivity";

    private static final String BASE_URL="http://192.168.1.104:8080/mobile";

    RequestCallback<String> stringCallBack = new RequestCallback<String>() {
        @Override
        public void onSuccess(String response) {
            webContent.setText(Html.fromHtml(response));
            Log.e(TAG, response);
        }

        @Override
        public void onError(Exception e) {
            webContent.setText(Html.fromHtml(e.getMessage()));
            Log.e(TAG,e.getMessage());
        }

        @Override
        public void onProgress(long total, long current, boolean isUploading) {
            Log.e(TAG,"total:"+total+",current:"+current+",isUploading:"+isUploading);
        }

        @Override
        public void onCancel() {
            webContent.setText("onCancel");
            Log.e(TAG, "onCancel");
        }
    };

    RequestCallback<User> userCallBack=new RequestCallback<User>() {
        @Override
        public void onSuccess(User user) {
            webContent.setText(Html.fromHtml(user.toString()));
            Log.e(TAG,user.toString());
        }

        @Override
        public void onError(Exception e) {
            webContent.setText(Html.fromHtml(e.getMessage()));
            Log.e(TAG, e.getMessage());
        }

    };


    RequestCallback<List<User>> userListCallBack=new RequestCallback<List<User>>() {
        @Override
        public void onSuccess(List<User> list) {
            webContent.setText(Html.fromHtml(list.toString()));
            Log.e(TAG,list.toString());
        }

        @Override
        public void onError(Exception e) {
            webContent.setText(Html.fromHtml(e.getMessage()));
            Log.e(TAG, e.getMessage());
        }
    };


    TextView webContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        webContent= (TextView) findViewById(R.id.web);

    }


    public void getHttps(View v){
        SimpleHttpRequest request=new SimpleHttpRequest.Builder()
                .url("https://kyfw.12306.cn/otn/")
                .get(stringCallBack);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String result=new SimpleHttpRequest.Builder()
//                        .url("http://www.baidu.com")
//                        .getSync(String.class);
//                Log.d(TAG, result);
//            }
//        }).start();

    }

    public void findUserForGet(View v){

        final List<FormParam> formParams=new ArrayList<>();
        formParams.add(new FormParam("id","12"));
        formParams.add(new FormParam("username","张明明"));
        formParams.add(new FormParam("address","北京海淀区"));


        //同步请求
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String result= null;
//                try {
//                    result = new SimpleHttpRequest.Builder()
//                            .url(BASE_URL+"/rest/findUserForGet")
//                            .params(formParams)
//                            .getSync(new SimpleType<String>(){});
//                    Log.d(TAG, result);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();

        //异步请求
        new SimpleHttpRequest.Builder()
                .url(BASE_URL+"/rest/findUserForGet")
                .params(formParams)
                .get(userCallBack);


    }


    public void findUserForPost(View v) {

        new SimpleHttpRequest.Builder()
                .url(BASE_URL+"/rest/findUserForPost")
                .addParam("id","9")
                .addParam("username","陈玄功")
                .addParam("address","恶人谷")
                .post(userCallBack);


    }


    public  void findUserList(View v){
        new SimpleHttpRequest.Builder()
                .url(BASE_URL+"/rest/findUserList")
                .post(userListCallBack);
    }

    public void postBodyJson(View v){
        String json="{\"id\":2,\"username\":\"李明\",\"birthday\":\"1995-09-06 09-09-08\",\"sex\":\"1\"}";
         new SimpleHttpRequest.Builder()
                 .url(BASE_URL+"/rest/postBodyJson")
                 .contentType("application/json;charset=utf-8")
                 .content(json)
                 .post(userCallBack);

    }
    public void postBodyString(View v){
        String json="{\"id\":3,\"username\":\"小明\",\"birthday\":\"1985-09-06 09-09-08\",\"sex\":\"0\"}";
        new SimpleHttpRequest.Builder()
                .url(BASE_URL+"/rest/postBodyString")
                .content(json)
                .post(userCallBack);

    }



    public void download(View v){
        SimpleHttpRequest request=new SimpleHttpRequest.Builder()
                //.url(BASE_URL+"/image/测试01.jpg")
              // .url(BASE_URL+"/image/测试02.jpg")
                .url(BASE_URL+"/image/girl.jpg")
                .destFileDir(Environment.getExternalStorageDirectory().getAbsolutePath())
                .destFileName(null)
                .download(stringCallBack);


        //取消请求
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        request.cancel();

    }

    public void upload(View v){

        List<FileParam> fileParams=new ArrayList<>();
        fileParams.add(new FileParam("file",null,new File(
                Environment.getExternalStorageDirectory(),"测试01.jpg")));
        fileParams.add(new FileParam("file",null,new File(
                Environment.getExternalStorageDirectory(),"测试02.jpg")));
        fileParams.add(new FileParam("file",null,new File(
                Environment.getExternalStorageDirectory(),"girl.jpg")));
        new SimpleHttpRequest.Builder()
                .url(BASE_URL+"/rest/upload")
                .addParam("id","5")
                .addParam("username","刘冰")
                .addParam("address","天津市")
                .files(fileParams)
                .upload(stringCallBack);
    }
}
