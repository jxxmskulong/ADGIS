ADGIS
=
## 1.简要介绍
ADGIS是一个能够简易查看当前广告牌信息的应用。使用了Bmob做为后台服务端，集成了高德地图对目标广告信息进行标注，从而可在地图上显示出广告牌对应广告信息的目标位置。在点击地图上对应广告点后显示详细信息。目前数据库尚不完善，仅仅只是制作了框架。能查看广告的四项信息：广告牌的物理信息，广告牌维护信息，广告牌文字信息，广告牌所属公司信息。以下是几张简要的截图：

|界面描述|运行截图|
|----|-----|
|**主界面**|![](https://github.com/xiajunkai/ADGIS/blob/master/art/man_1.jpg)|
|**搜索界面**|![](https://github.com/xiajunkai/ADGIS/blob/master/art/search_1.jpg)|
|**个人中心**|![](https://github.com/xiajunkai/ADGIS/blob/master/art/edituser.jpg)|
|**用户信息修改**|![](https://github.com/xiajunkai/ADGIS/blob/master/art/edituser.jpg)|
|**广告牌详情**|![](https://github.com/xiajunkai/ADGIS/blob/master/art/adsdetail.jpg)|
|**设置界面**|![](https://github.com/xiajunkai/ADGIS/blob/master/art/setting.jpg)|
|**登陆界面**|![](https://github.com/xiajunkai/ADGIS/blob/master/art/login.jpg)|
## 2.使用的库
在ADGIS的编写过程中，使用了大量的第三方库：
<br>详情如下Gradle文件
```Java
//网络框架Okhttp
compile 'com.squareup.okhttp3:okhttp:3.9.0'
//圆形图片
compile 'de.hdodenhof:circleimageview:2.1.0'
//Glide(图形加载库)
compile 'com.github.bumptech.glide:glide:3.7.0'
//Roboto字体
compile 'com.github.johnkil.android-robototextview:robototextview:2.5.0'
//智能刷新框架
compile 'com.scwang.smartrefresh:SmartRefreshLayout:1.0.5.1'
compile 'com.scwang.smartrefresh:SmartRefreshHeader:1.0.5.1'
//选择城市库
compile 'liji.library.dev:citypickerview:3.1.5'
//选择器的库(滚动选择)
compile 'com.contrarywind:Android-PickerView:3.2.7'
// 图像剪切
compile 'com.kevin:crop:1.0.2'
//GSON(解析JSON数据)
compile 'com.google.code.gson:gson:2.8.2'
//butter knife(注解式绑定)
compile 'com.jakewharton:butterknife:8.4.0'
annotationProcessor 'com.jakewharton:butterknife-compiler:8.4.0'
//Bmob云SDK
compile 'cn.bmob.android:bmob-sdk:3.5.7'
//PhotoView(可对图片进行缩放)
compile 'com.github.chrisbanes.photoview:library:1.2.3'
//简易实现圆角矩形背景
compile 'com.flyco.roundview:FlycoRoundView_Lib:1.1.4@aar'
//facebook的弹性动画库
compile 'com.facebook.rebound:rebound:0.3.8'
//大神JakeWharton的动画库
compile 'com.nineoldandroids:library:2.4.0'
```
