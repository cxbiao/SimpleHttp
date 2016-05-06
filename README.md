
#SimpleHttp

- 使用原生UrlConnection（无第三方依赖）
- 支持HTTPS
- 支持同步和异步
- 自动解析实体对象（默认使用fastjson）

###用法
```
compile 'com.bryan:simplehttp:1.0.0'
```

###1.异步Get请求

```
new SimpleHttpRequest.Builder()
                .url("https://kyfw.12306.cn/otn/")
                .get(myCallBack);
```


###2.同步Get请求

```
String result=new SimpleHttpRequest.Builder()
                  .url("http://www.baidu.com")
                  .getSync(String.class);
```


###3 异步Post请求
```
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

List<FormParam> params = new ArrayList<>();
params.add(new FormParam("username", "qq"));
params.add(new FormParam("password", "说明"));
params.put("format", "json");
new SimpleHttpRequest.Builder()
             .url("http://192.168.6.59:8080/web/ListServlet")
             .params(params)
             .post(model2CallBack);

```


###4 Post请求json

```
 String json="{\"id\":2,\"name\":\"liky\"}";
 new SimpleHttpRequest.Builder()
               .url("http://192.168.6.59:8080/web/LoginServlet")
               .contentType(SimplePostHttpRequest.MEDIA_TYPE_JSON)
               .content(json)
               .post(model1CallBack);

```

###5 下载（支持下载进度）

```
 new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/files/abc.apk")
                .destFileDir(Environment.getExternalStorageDirectory()+"/okhttp")
                .destFileName("abc中国.apk")
                .download(myCallBack);
```

###6 文件上传（支持上传进度）

```
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
```

###7 自定义CallBack

```
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
```


###8 取消单个请求
```
     SimpleHttpRequest request= new SimpleHttpRequest.Builder()...
     request.cancel();
```