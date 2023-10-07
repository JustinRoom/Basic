# 仿Toast工具ImitateToast

#### 1.1、在Application中初始化

```
public class MainApp extends BaseApplication {
        ImitateToast.init(this);
        ...
}
```

#### 1.2、ImitateToast使用
```
ImitateToast.show("clicked button");

new ImitateToast.Builder()
        .gravity(Gravity.CENTER)
        .x(0)
        .y(48)
        .time(3_000L)
        .show("提示文案");
```