### 1、引入libs
```
implementation fileTree(dir: 'libs', include: ['*.jar', '*.aar'])
```

### 2、使用view binding
```
    buildFeatures {
        viewBinding true
    }
```