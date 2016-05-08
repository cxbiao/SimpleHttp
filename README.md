
#SimpleHttp

- 使用原生UrlConnection（无第三方依赖）
- 支持HTTPS
- 支持同步和异步
- 自动解析实体对象（默认使用fastjson）

###用法
```
compile 'com.bryan:simplehttp:1.0.0'
```

###1.同步Get请求

```
String result=new SimpleHttpRequest.Builder()
                  .url("http://www.baidu.com")
                  .getSync(String.class);
```

###2.异步Get请求

```
final List<FormParam> formParams=new ArrayList<>();
        formParams.add(new FormParam("id","12"));
        formParams.add(new FormParam("username","张明明"));
        formParams.add(new FormParam("address","北京海淀区"));
        
 new SimpleHttpRequest.Builder()
                .url(BASE_URL+"/rest/findUserForGet")
                .params(formParams)
                .get(userCallBack);
```





###3 异步Post请求
```
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

  new SimpleHttpRequest.Builder()
                .url(BASE_URL+"/rest/findUserForPost")
                .addParam("id","9")
                .addParam("username","陈玄功")
                .addParam("address","恶人谷")
                .post(userCallBack);

```


###4 Post请求json

```
 String json="{\"id\":2,\"username\":\"李明\",\"birthday\":\"1995-09-06 09-09-08\",\"sex\":\"1\"}";
         new SimpleHttpRequest.Builder()
                 .url(BASE_URL+"/rest/postBodyJson")
                 .contentType("application/json;charset=utf-8")
                 .content(json)
                 .post(userCallBack);

```

###5 下载（支持下载进度）

```
 SimpleHttpRequest request=new SimpleHttpRequest.Builder()
                //.url(BASE_URL+"/image/测试01.jpg")
              // .url(BASE_URL+"/image/测试02.jpg")
                .url(BASE_URL+"/image/girl.jpg")
                .destFileDir(Environment.getExternalStorageDirectory().getAbsolutePath())
                .destFileName(null)
                .download(stringCallBack);
```

###6 文件上传（支持上传进度）

```
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
```

###7 添加header

```
 SimpleHttpRequest request=new SimpleHttpRequest.Builder()
                .addHeader("content-Type","text/plain")
                .addHeader("Connection","keep-alive")
                ......
```

###8 自定义CallBack

```
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
```


###9 取消单个请求
```
     SimpleHttpRequest request= new SimpleHttpRequest.Builder()...
     request.cancel();
```