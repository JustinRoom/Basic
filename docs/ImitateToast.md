# 仿Toast工具ImitateToast

原生Toast:  
1、提示消息只能一条一条展示(需要等待上一次的消息提示完成)  
2、大量频繁创建Toast消息会出现崩溃，比如用for循环来测试  

ImitateToast:  
1、立即更新提示消息，无需等待上一次消息完成提示  
2、自始至终只创建一个TextView，大量频繁创建提示消息也不会崩溃  
3、可按业务需求在指定位置上显示提示消息

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