package com.bryan.httpdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bryan.httpdemo.model.Course;
import com.bryan.httpdemo.model.Person;
import com.bryan.simplehttp.callback.RequestCallback;
import com.bryan.simplehttp.callback.SimpleType;
import com.bryan.simplehttp.request.FileParam;
import com.bryan.simplehttp.request.FormParam;
import com.bryan.simplehttp.request.SimpleHttpRequest;
import com.bryan.simplehttp.request.SimplePostHttpRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/13.
 */
public class HttpActivity extends AppCompatActivity {


    private static final String TAG = "HttpActivity";

    RequestCallback<String> myCallBack = new RequestCallback<String>() {
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

    RequestCallback<Person> model1CallBack=new RequestCallback<Person>() {
        @Override
        public void onSuccess(Person response) {
            webContent.setText(Html.fromHtml(response.toString()));
            Log.e(TAG,response.toString());
        }

        @Override
        public void onError(Exception e) {
            webContent.setText(Html.fromHtml(e.getMessage()));
            Log.e(TAG, e.getMessage());
        }

    };

    RequestCallback<List<Course>> model2CallBack=new RequestCallback<List<Course>>() {
        @Override
        public void onSuccess(List<Course> response) {
            for(Course c:response){
                Log.e(TAG,c.toString());
            }
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


    public void post(View v) {

        final List<FormParam> params = new ArrayList<>();
        params.add(new FormParam("username", "qq"));
        params.add(new FormParam("password", "说明"));
//        new SimpleHttpRequest.Builder()
//                .url("http://192.168.6.59:8080/web/LoginServlet")
//                .params(params)
//                .post(myCallBack);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result= "";
                try {
                    result = new SimpleHttpRequest.Builder()
                      .url("http://192.168.6.59:8080/web/LoginServlet")
                      .addParam("username", "qq")
                      .addParam("password", "说明")
                     .postSync(new SimpleType<String>() {
                     });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG,result);
            }
        }).start();
    }


    public void postJson(View v){
        String json="{\"id\":2,\"name\":\"liky\"}";
         new SimpleHttpRequest.Builder()
                 .url("http://192.168.6.59:8080/web/LoginServlet")
                 .contentType(SimplePostHttpRequest.MEDIA_TYPE_JSON)
                 .content(json)
                 .post(model1CallBack);


    }

    public void get(View v){
        SimpleHttpRequest request=new SimpleHttpRequest.Builder()
                .url("https://kyfw.12306.cn/otn/")
                .get(myCallBack);

        //request.cancel();
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

    public void getPerson(View v){
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/json.txt")
                .get(model1CallBack);

//
//                new Thread(new Runnable() {
//            @Override
//            public void run() {
//               Person p=new SimpleHttpRequest.Builder()
//                        .url("http://192.168.6.59:8080/web/json.txt")
//                        .getSync(new SimpleType<Person>(){});
//                Log.d(TAG,p.toString());
//            }
//        }).start();
    }

    public void getCourse(View v){
        final List<FormParam> params = new ArrayList<>();
        params.add(new FormParam("format", "json"));
//        new SimpleHttpRequest.Builder()
//                .url("http://192.168.6.59:8080/web/ListServlet")
//                .params(params)
//                .get(model2CallBack);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Course> courses= null;
                try {
                    courses = new SimpleHttpRequest.Builder()
                            .url("http://192.168.6.59:8080/web/ListServlet")
                            .params(params)
                            .getSync(new SimpleType<List<Course>>(){});
                    Log.e(TAG,courses.get(0).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void download(View v){
        final SimpleHttpRequest request=new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/files/abc.apk")
                .destFileDir(Environment.getExternalStorageDirectory()+"/okhttp")
                .destFileName("abc中国.apk")
                .download(myCallBack);


        //取消请求
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        request.cancel();

    }

    public void upload(View v){

        List<FileParam> fileParams=new ArrayList<>();
        fileParams.add(new FileParam("file",null,new File(
                Environment.getExternalStorageDirectory(),"qq中国.jpg")));
        fileParams.add(new FileParam("file",null,new File(
                Environment.getExternalStorageDirectory(),"abc.jpg")));
        fileParams.add(new FileParam("file",null,new File(
                Environment.getExternalStorageDirectory(),"hehe.doc")));
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/UploadFileServlet")
                .addParam("filename", "music")
                .addParam("filedes", "发如雪")
                .files(fileParams)
                .upload(myCallBack);
    }
}
