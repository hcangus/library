# library
我的工具
[![](https://jitpack.io/v/hcangus/library.svg)](https://jitpack.io/#hcangus/library)

## 引入

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Step 2. Add the dependency
```
dependencies {
  compile 'com.github.hcangus:library:1.0.1'
}
```

## 使用
1.相关初始化
    ```java
    //崩溃日志保存到SDCard
    if (!BuildConfig.DEBUG) {
        CrashHandler.getInstance().init(this);
    }
    //Logcat优化
    MyLogger.init("tag", BuildConfig.DEBUG);
    //Volley请求队列初始化
    VolleyHandler.intializeRequestQueue(this);
    ```

2.MyLogger
    *如要使用，在application里进行初始化：
    ```java
    MyLogger.init(String tag, final boolean debug)
    ```

    *使用：
    ```java
     Logger.i(String, Object...)
     Logger.d(Object)
     Logger.d(String, Object...)
     Logger.v(String, Object...)
     Logger.w(String, Object...)
     Logger.wtf(String, Object...)
     Logger.e(String, Object...)
     Logger.e(Throwable, String, Object...)
     Logger.xml(String)
     Logger.json(String)
     ```

3.Banner
    * 设置样式（setBannerStyle）
    |常量名称|描述
    |---|---|
    |BannerConfig.NOT_INDICATOR| 不显示指示器和标题
    |BannerConfig.CIRCLE_INDICATOR| 显示圆形指示器
    |BannerConfig.NUM_INDICATOR| 显示数字指示器
    |BannerConfig.NUM_INDICATOR_TITLE| 显示数字指示器和标题
    |BannerConfig.CIRCLE_INDICATOR_TITLE| 显示圆形指示器和标题（垂直显示）
    |BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE| 显示圆形指示器和标题（水平显示）
    |BannerConfig.CIRCLE_INDICATOR_BELOW| 显示圆形指示器在图片下方

    * 设置指示器位置（setIndicatorGravity）
    |常量名称|描述
    |---|---|
    |BannerConfig.LEFT| 指示器居左
    |BannerConfig.CENTER| 指示器居中
    |BannerConfig.RIGHT| 指示器居右

    * 方法
    |方法名|描述|备注
    |---|---|---|
    |setBannerStyle(int bannerStyle)| 设置轮播样式（默认为CIRCLE_INDICATOR）
    |setIndicatorGravity(int type)| 设置指示器位置（没有标题默认为右边,有标题时默认左边）
    |isAutoPlay(boolean isAutoPlay)| 设置是否自动轮播（默认自动）
    |setViewPagerIsScroll(boolean isScroll)| 设置是否允许手动滑动轮播图（默认true）
    |update(List<?> imageUrls,List<String> titles)| 更新图片和标题
    |update(List<?> imageUrls)| 更新图片
    |startAutoPlay()|开始轮播|此方法只作用于banner加载完毕-->需要在start()后执行
    |stopAutoPlay()|结束轮播|此方法只作用于banner加载完毕-->需要在start()后执行
    |start()|开始进行banner渲染
    |setOffscreenPageLimit(int limit)|同viewpager的方法作用一样
    |setBannerTitles(List<String> titles)| 设置轮播要显示的标题和图片对应（如果不传默认不显示标题）
    |setDelayTime(int time)| 设置轮播图片间隔时间（单位毫秒，默认为2000）
    |setImages(List<?> imagesUrl)| 设置轮播图片(所有设置参数方法都放在此方法之前执行)
    |setOnBannerListener(this)|设置点击事件，下标是从0开始
    |setImageLoader(Object implements ImageLoader)|设置图片加载器
    |setOnPageChangeListener(this)|设置viewpager的滑动监听
    |setBannerAnimation(Class<? extends PageTransformer> transformer)|设置viewpager的默认动画,传值见动画表
    |setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer)|设置viewpager的自定义动画

    * Attributes属性（banner布局文件中调用）
    |Attributes|format|describe
    |---|---|---|
    |delay_time| integer|轮播间隔时间，默认2000
    |scroll_time| integer|轮播滑动执行时间，默认800
    |is_auto_play| boolean|是否自动轮播，默认true
    |title_background| color|reference|标题栏的背景色
    |title_textcolor| color|标题字体颜色
    |title_textsize| dimension|标题字体大小
    |title_height| dimension|标题栏高度
    |indicator_width| dimension|指示器圆形按钮的宽度
    |indicator_height| dimension|指示器圆形按钮的高度
    |indicator_margin| dimension|指示器之间的间距
    |indicator_drawable_selected| reference|指示器选中效果
    |indicator_drawable_unselected| reference|指示器未选中效果
    |image_scale_type| enum |和imageview的ScaleType作用一样
    |banner_default_image| reference | 当banner数据为空是显示的默认图片
    |banner_layout| reference |自定义banner布局文件，但是必须保证id的名称一样（你可以将banner的布局文件复制出来进行修改）

    * 使用方法
    #### Step 1.在布局文件中添加Banner，可以设置自定义属性
    ！！！此步骤可以省略，直接在Activity或者Fragment中new Banner();
    ```xml
    <net.hcangus.banner.view.Banner
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/banner"
        android:layout_width="match_parent"
        android:layout_height="高度自己设置" />
    ```

    #### Step 2.如有必要，重写图片加载器
    [参考-GlideImageLoader](library/lib-base/src/main/java/net/hcangus/banner/loader/GlideImageLoader.java)

    #### Step 3.设置Banner
    ```java
    int imgWidth = DeviceUtil.getScreenWidth(mContext);
    int imgHeight = ViewUtils.getHeightByWidth(imgWidth, 2f);
    ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();
    layoutParams.height = imgHeight;
    banner.setLayoutParams(layoutParams);
    banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
            .setImageLoader(new BannerImageLoader(mContext, imgWidth, imgHeight))
            .setOffscreenPageLimit(3)
            .setDelayTime(5000)
            .setOnBannerListener(this);
    //设置自动轮播，默认为true
    banner.isAutoPlay(true);
    ```
    ##### 如果图片列表已知：
    ```java
    //设置图片集合
    banner.setImages(images);
    //banner设置方法全部调用完毕时最后调用
    banner.start();
    ```
    ##### 如果从网络获取到相关列表：
    ```java
    banner.update(List<T> imageUrls)
    ```

    #### Step 4.（可选）增加体验
    ```java
    //如果你需要考虑更好的体验，可以这么操作
    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }
    ```

4.PasswordView 密码输入框
    ```xml
    <net.hcangus.password.PayEditText
            android:id="@+id/PayEditText_pay"
            android:layout_width="match_parent"
            android:layout_marginTop="20dp"
            android:paddingLeft="12dp"
            android:paddingRight="12dp"
            android:layout_height="48dp"/>
    <Space
        android:layout_width="match_parent"
        android:layout_weight="1"
        android:layout_height="0dp"/>
    <net.hcangus.password.Keyboard
        android:id="@+id/KeyboardView_pay"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
    ```

    ```java
    keyboard.setTarget(payEditText);
    /**
     * 当密码输入完成时的回调
     */
    payEditText.setOnInputFinishedListener(new PayEditText.OnInputFinishedListener() {
        @Override
        public void onInputFinished(String password) {

        }
    });
    ```