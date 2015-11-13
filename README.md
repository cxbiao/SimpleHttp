
#非常简单的HTTP封装类库，支持HTTPS，仅使用原生的UrlConnection,并集成fastjson自动解析实体对象

- Get用法1
```
new SimpleHttpRequest.Builder()
                .url("https://kyfw.12306.cn/otn/")
                .get(myCallBack);
```

- Get用法2

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

Map<String, String> params = new HashMap<>();
        params.put("format", "json");
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/ListServlet")
                .params(params)
                .get(model2CallBack);

```


- Post用法

```
  Map<String, String> params = new HashMap<>();
        params.put("username", "qq");
        params.put("password", "说明");
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/LoginServlet")
                .params(params)
                .post(myCallBack);

```

- 下载（支持进度回调）

```
 new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/files/abc.apk")
                .destFileDir(Environment.getExternalStorageDirectory()+"/okhttp")
                .destFileName("abc中国.apk")
                .download(myCallBack);
```

- 上传（支持进度回调）

```
  Map<String, String> params = new HashMap<>();
        params.put("filename", "music");
        params.put("filedes", "中国心");
        Pair<String,File>[] files=new Pair[]{
                new Pair("file",new File(
                Environment.getExternalStorageDirectory(),"qq中国.jpg"))
        };
        new SimpleHttpRequest.Builder()
                .url("http://192.168.6.59:8080/web/UploadFileServlet")
                .params(params)
                .files(files)
                .upload(myCallBack);
```


