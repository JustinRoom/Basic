# 可扩展弹窗[CustomExpandableDialog](../basicLibrary/src/main/java/jsc/org/lib/basic/widget/dialog/CustomExpandableDialog.java)

## 使用示例
#### 1.1、简单提示弹窗
```
        new CustomExpandableDialog(this)
                .asTipsStyle()
                .cancelable(false)
                .showMyself()
                .title(false, "")
                .subTitle(false, "")
                .tips("系统繁忙，请稍后。")
                .negativeButton("取消")
                .positiveButton("重试")
                .buttonVisibility(false, true)
                .setActionCallback(new CustomExpandableDialog.SimpleOnActionBack() {
                    @Override
                    public void onPositive(View view, Bundle data) {
                        saveRecord();
                    }
                }).countDown("ok", 5);
```

#### 1.2、按业务需求扩展弹窗
```
        List<BKKSEntity> selectedList = adapter.getSelected();
        CustomExpandableDialog dialog = new CustomExpandableDialog(activity)
                .landscapeSize(.75f, .8f)
                .portraitSize(.95f, .6f)
                .showMyself()
                .title(true, "缺考确认")
                .subTitle(true, Gravity.START, String.format(Locale.US, "考试科目：%s\u3000已选缺考考生：%d人", kmmc, selectedList.size()))
                .negativeButton("取消")
                .positiveButton("确认提交")
                .setActionCallback(new CustomExpandableDialog.SimpleOnActionBack() {
                    @Override
                    public void onPositive(View view, Bundle data) {
                        registerQueKao();
                    }
                });
        DialogContentQkSubmitBinding qkSubmitBinding = DialogContentQkSubmitBinding.inflate(dialog.getLayoutInflater(),
                dialog.getViewBinding().fyContentContainer,
                true);
        qkSubmitBinding.recyclerView.setLayoutManager(new LinearLayoutManager(dialog.getContext()));
        qkSubmitBinding.recyclerView.setAdapter(new ListConfirmAdapter(selectedList));
```