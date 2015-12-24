package com.bryan.simplehttp;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.bryan.simplehttp.model.Course;
import com.bryan.simplehttp.model.Person;
import com.bryan.simplehttp.net.SimpleHttpRequest;
import com.bryan.simplehttp.net.callback.RequestCallback;
import com.bryan.simplehttp.net.callback.SimpleType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/13.
 */
public class HttpActivity extends AppCompatActivity {


    private static final String TAG = "HttpActivity";

    RequestCallback<String> myCallBack = new RequestCallback<String>() {
        @Override
        public void onSuccess(String response) {
            webContent.setText(Html.fromHtml(response));
            Log.d(TAG, response);
        }

        @Override
        public void onError(Exception e) {
            webContent.setText(Html.fromHtml(e.getMessage()));
            Log.e(TAG,e.getMessage());
        }

        @Override
        public void onProgress(long total, long current, boolean isUploading) {
            Log.d(TAG,"total:"+total+",current:"+current+",isUploading:"+isUploading);
        }

        @Override
        public void onCancel() {
            webContent.setText("onCancel");
            Log.d(TAG, "onCancel");
        }
    };

    RequestCallback<Person> model1CallBack=new RequestCallback<Person>() {
        @Override
        public void onSuccess(Person response) {
            webContent.setText(Html.fromHtml(response.toString()));
            Log.d(TAG,response.toString());
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
                Log.d(TAG,c.toString());
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

        final Map<String, String> params = new HashMap<>();
        params.put("username", "qq");
        params.put("password", "说明");
//        new SimpleHttpRequest.Builder()
//                .url("http://192.168.6.59:8080/web/LoginServlet")
//                .params(params)
//                .post(myCallBack);

        new Thread(new Runnable() {
            @Override
            public void run() {
               String result= new SimpleHttpRequest.Builder()
                 .url("http://192.168.6.59:8080/web/LoginServlet")
                .params(params)
                .postSync(new SimpleType<String>(){});
                Log.d(TAG,result);
            }
        }).start();
    }

    public void get(View v){
        SimpleHttpRequest request=new SimpleHttpRequest.Builder()
                .url("https://kyfw.12306.cn/otn/")
                .get(myCallBack);

        request.cancel();
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
        final Map<String, String> params = new HashMap<>();
        params.put("format", "json");
//        new SimpleHttpRequest.Builder()
//                .url("http://192.168.6.59:8080/web/ListServlet")
//                .params(params)
//                .get(model2CallBack);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Course> courses=new SimpleHttpRequest.Builder()
                        .url("http://192.168.6.59:8080/web/ListServlet")
                        .params(params)
                        .getSync(new SimpleType<List<Course>>(){});
                Log.d(TAG,courses.get(0).toString());
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
        Map<String, String> params = new HashMap<>();
        params.put("filename", "music");
        params.put("filedes", "发如雪");
        Pair<String,File>[] files=new Pair[]{
                new Pair("file",new File(
                Environment.getExternalStorageDirectory(),"qq中国.jpg"))
        };
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/UploadFileServlet")
                .params(params)
                .files(files)
                .upload(myCallBack);
    }
}
