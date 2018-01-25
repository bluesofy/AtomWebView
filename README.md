## AtomWebView

[ ![Download](https://api.bintray.com/packages/blueyuki/maven/AtomWebView/images/download.svg) ](https://bintray.com/blueyuki/maven/AtomWebView/_latestVersion)

使用Tencent X5内核，封装常用配置和方法，不想用X5，可以直接改源码，很简单的。
<br>利用onJsPrompt来实现JS交互，避开Android 4.2以下的WebView漏洞，不需要使用@JavascriptInterface注释。
<br>加了部分常用的功能调用，支持重写自定义扩充。
<br><br>

### 使用说明  Usage
#### 添加依赖
```Gradle
api 'cn.byk.pandora:atomwebview:1.0.0'
```

#### 使用方法
- 直接在布局中使用或者继承后使用都可以
```XML
<com.byk.pandora.atomwebview.AtomWebView
        android:id="@+id/webview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

- 自定义WebChromeClient需要继承AtomWebChromeClient，类似的，自定义WebViewClient需要继承AtomWebViewClient

- 主要API说明
```Java
// 调用Js方法，API19以下调用loadUrl()，19以上调用evaluateJavascript()，
// 区别，loadUrl会重载网页，evaluateJavascript不会，且自带回调
invokeJs(final String jsMethod, final ValueCallback<String> callback)

// 滑动监听，可监听到底事件
setScrollWatcher(IScrollWatcher watcher)

// 长按监听，接口方法watcher.onLongClick(int type, HitTestResult result)
// type - 资源类型，常量值见AtomWebView定义
// result.getExtra() - 获取数据
setLongClickWatcher(ILongClickWatcher watcher)

// 需要继承重写，设定是否启用Js，默认true
enableJs()

// 需要继承重写，设定是否需要图片延迟加载，即先加载完网页基本数据再加载图片，默认true
enableDelayLoadImages()

// 释放WebView资源，注意WebView的清理资源影响的是全局进程的，在界面销毁的时候调用
close()

// Js调用Android方法监听，注意IJsBridge的onReceive带String结果值返回，可回调数据给网页Js
setJsBridge(IJsBridge bridge)
```

- 已实现的原生方法调用说明，以后可能会以atoms的scheme扩充
```JavaScript
// 注意这个是写在Js里的，function名字无所谓
function callAndroid() {
    // 打开网页（调起第三方浏览器）
    var result = prompt("http://www.baidu.com");

    // 打开拨号盘
    var result = prompt("tel:114");
    // 直接拨号（需要权限）
    var result = prompt("tel:114$");

    // 打开短信
    var result = prompt("smsto:10086");
    // 打开短信，预写短信内容
    var result = prompt("smsto:10086$查询流量");

    // 打开相机，request是AcitvityForResult的回调code
    var result = prompt("atoms://camera?request=1");

    // 打开第三方App，写入第三方App的scheme即可
    var result = prompt("tbopen://xxx?xxx=1");
}
```

- 如果都不在以上已实现的原生方法调用中，或者打开第三方App失败，会抛出AtomResult自定义处理
```Java
public class AtomResult {

    // uri.getScheme()
    private String scheme;

    // uri.getAuthority()
    private String action;

    // 参数字符串，即问号后面的那一串，uri.getQuery()
    private String data;

    private Uri uri;

    // Js.prompt传入的完整String数据
    private String content;
}
```

- 如果需要增加加载进度条，可继承AtomWebChromeClient
```Java
// 让进度条控件实现接口
public interface IProgress {
    void onProgress(int progress);
    void onVisible(boolean visible);
}

// 重写
public IProgress progress()
```

- 如果需要自定义加载错误页面，可继承AtomWebViewClient
```Java
// 让自定义View实现接口
public interface IErrorView {
    void onError(WebView webView, int errorCode, String desc, String errorUrl);
}

// 重写
public IErrorView errorView()
```


### 关于混淆  ProGuard
#### 无需添加混淆
<br>

### 特别鸣谢  Tks to
- [TBS腾讯浏览服务](http://x5.tencent.com/)
<br>

### 联系方式  Support or Contact
- E-Mail: bluesofy@qq.com
- E-Mail: bluesofy@live.cn