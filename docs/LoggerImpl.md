# 本地日志工具LoggerImpl

#### 1.1、在Application中初始化

```
public class MainApp extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ...
        LoggerImpl.getInstance().init(this, getExternalFilesDir("logs"), false);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                LoggerImpl.getInstance().t(null, e, true);
            }
        });
    }
}
```

#### 1.2、LoggerImpl使用
```
LoggerImpl.getInstance().v("ViewClick", "View was clicked:" + System.currentTimeMillis(), true);
LoggerImpl.getInstance().d("ViewClick", "View was clicked:" + System.currentTimeMillis(), true);
LoggerImpl.getInstance().i("ViewClick", "View was clicked:" + System.currentTimeMillis(), true);
LoggerImpl.getInstance().w("ViewClick", "View was clicked:" + System.currentTimeMillis(), true);
LoggerImpl.getInstance().e("ViewClick", "View was clicked:" + System.currentTimeMillis(), true);
LoggerImpl.getInstance().t("ViewClick", t, true);
```

#### 1.3、本地保存效果示例log0.txt
```
Create Time:2023年09月18日 16:21
Android Version:12
Sdk Version:31
Brand:Redmi
Manufacturer:Xiaomi
Model:Redmi K30
CPU ABI:arm64-v8a

Date:2023年09月18日
Vers:1.0  1
16:21:33 [INFO][ViewClick]-> View was clicked:1695025293183
16:21:33 [INFO][ViewClick]-> View was clicked:1695025293386
16:21:33 [INFO][ViewClick]-> View was clicked:1695025293584
16:21:33 [INFO][ViewClick]-> View was clicked:1695025293791
16:21:33 [INFO][ViewClick]-> View was clicked:1695025293977
16:21:34 [INFO][ViewClick]-> View was clicked:1695025294185
16:21:34 [INFO][ViewClick]-> View was clicked:1695025294375
16:21:35 [ERROR][Logger]-> java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.CharSequence android.widget.TextView.getText()' on a null object reference
	at com.jsc.netreq.LoggerTestActivity$2.onClick(LoggerTestActivity.java:32)
	at android.view.View.performClick(View.java:7751)
	at com.google.android.material.button.MaterialButton.performClick(MaterialButton.java:1194)
	at android.view.View.performClickInternal(View.java:7728)
	at android.view.View.access$3700(View.java:859)
	at android.view.View$PerformClick.run(View.java:29148)
	at android.os.Handler.handleCallback(Handler.java:938)
	at android.os.Handler.dispatchMessage(Handler.java:99)
	at android.os.Looper.loopOnce(Looper.java:210)
	at android.os.Looper.loop(Looper.java:299)
	at android.app.ActivityThread.main(ActivityThread.java:8337)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:556)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1037)
16:25:22 [INFO][ViewClick]-> View was clicked:1695025522918
16:25:23 [INFO][ViewClick]-> View was clicked:1695025523134
16:25:23 [INFO][ViewClick]-> View was clicked:1695025523325
16:25:23 [INFO][ViewClick]-> View was clicked:1695025523526
16:25:23 [INFO][ViewClick]-> View was clicked:1695025523719
16:25:23 [INFO][ViewClick]-> View was clicked:1695025523911
16:25:24 [INFO][ViewClick]-> View was clicked:1695025524077
16:25:25 [ERROR][Logger]-> java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.CharSequence android.widget.TextView.getText()' on a null object reference
	at com.jsc.netreq.LoggerTestActivity$2.onClick(LoggerTestActivity.java:32)
	at android.view.View.performClick(View.java:7751)
	at com.google.android.material.button.MaterialButton.performClick(MaterialButton.java:1194)
	at android.view.View.performClickInternal(View.java:7728)
	at android.view.View.access$3700(View.java:859)
	at android.view.View$PerformClick.run(View.java:29148)
	at android.os.Handler.handleCallback(Handler.java:938)
	at android.os.Handler.dispatchMessage(Handler.java:99)
	at android.os.Looper.loopOnce(Looper.java:210)
	at android.os.Looper.loop(Looper.java:299)
	at android.app.ActivityThread.main(ActivityThread.java:8337)
	at java.lang.reflect.Method.invoke(Native Method)
	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:556)
	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1037)
```