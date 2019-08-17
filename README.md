# kt-first

#### 介绍
用kotlin重新将Java版本的[BaseFrame](https://github.com/robinvanPersie/BaseFrame)又写了一遍，将base类，http模块，common工具包进行了模块化拆分，因为用到了[ARouter](https://github.com/alibaba/ARouter/blob/master/README_CN.md)进行路由，所以顺便使用了其包含的di，所以将Java版的dagger2移除了；另用kotlin-android-extension 代替了 databinding的findView功能

