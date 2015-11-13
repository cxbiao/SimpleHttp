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

        Map<String, String> params = new HashMap<>();
        params.put("username", "qq");
        params.put("password", "说明");
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/LoginServlet")
                .params(params)
                .post(myCallBack);
    }


    public void get(View v){
        new SimpleHttpRequest.Builder()
                .url("https://kyfw.12306.cn/otn/")
                .get(myCallBack);

    }

    public void getPerson(View v){
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/json.txt")
                .get(model1CallBack);
    }

    public void getCourse(View v){
        Map<String, String> params = new HashMap<>();
        params.put("format", "json");
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/ListServlet")
                .params(params)
                .get(model2CallBack);
    }

    public void download(View v){
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/files/abc.apk")
                .destFileDir(Environment.getExternalStorageDirectory()+"/okhttp")
                .destFileName("abc中国.apk")
                .download(myCallBack);
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
