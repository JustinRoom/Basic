# 仿Toast工具ImitateToast

#### 1.1、在Application中初始化

```
public class MainApp extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ...
        ImitateToast.init(this, null);
    }
        
    @Override
    public void onTerminate() {
        super.onTerminate();
        ...
        ImitateToast.unInit();
    }
}
```

#### 1.2、悬浮窗权限申请
```
   ActivityResultLauncher<Intent> mDrawOverlaysLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
       @Override
       public void onActivityResult(ActivityResult result) {
           onDrawOverlaysLaunchBack(result.getResultCode(), result.getData());
       }
   });
            
    public final boolean canDrawOverlays(boolean toSetting) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        //Android6及以上版本，申请悬浮窗权限
        boolean result = Settings.canDrawOverlays(this);
        if (!result && toSetting) {
            //manifest文件中需要申明"android.permission.SYSTEM_ALERT_WINDOW"权限
            mDrawOverlaysLauncher.launch(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
        }
        return result;
    }
```

#### 1.3、ImitateToast使用
```
ImitateToast.show("clicked button");
```