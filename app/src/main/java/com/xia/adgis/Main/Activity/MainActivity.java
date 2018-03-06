package com.xia.adgis.Main.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.swipeback.SwipeBackActivityImpl;
import com.google.gson.Gson;
import com.xia.adgis.Login.LoginActivity;
import com.xia.adgis.Main.Bean.AD;
import com.xia.adgis.R;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.xia.adgis.Register.Bean.User;
import com.xia.imagewatch.RolloutBDInfo;
import com.xia.imagewatch.RolloutInfo;
import com.xia.imagewatch.RolloutPreviewActivity;
import com.xia.toprightmenu.MenuItem;
import com.xia.toprightmenu.TopRightMenu;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;

public class MainActivity extends SwipeBackActivityImpl implements AMap.OnMarkerClickListener,PopupWindow.OnDismissListener{

    private static final int UI_ANIMATION_DELAY = 3;
    private static final int SEARCH = 1;
    private static final int SETTING = 2;
    //需点击隐藏的UI控件
    @BindView(R.id.fullscreen_content_controls)
    View mControlsView;
    //自定义toolbar
    @BindView(R.id.toolbar)
    View mToolBar;
    //自定义下部导航栏
    @BindView(R.id.my_back)
    LinearLayout back;
    @BindView(R.id.back_icon)
    ImageView backIcon;
    @BindView(R.id.back_text)
    TextView backText;
    @BindView(R.id.my_info)
    LinearLayout info;
    @BindView(R.id.info_icon)
    ImageView infoIcon;
    @BindView(R.id.info_text)
    TextView infoText;
    @BindView(R.id.my_advance)
    LinearLayout advance;
    @BindView(R.id.advance_icon)
    ImageView advanceIcon;
    @BindView(R.id.advance_text)
    TextView advanceText;

    //主界面头像
    @BindView(R.id.imageView)
    CircleImageView mCircleImageView;
    //我的位置
    @BindView(R.id.location)
    ImageView mLocation;
    //搜索
    @BindView(R.id.search)
    ImageView mSearch;

    //地图
    @BindView(R.id.map)
    MapView mMapView;
    private AMap mAMap;
    private UiSettings mUiSettings;
    private Marker locationMarker;
    //自定义定位相关
    public AMapLocationClient mAMapLocationClient;
    public AMapLocationClientOption mAMapLocationClientOption;
    //private MyLocationStyle myLocationStyle;
    private int tempMarkerId = 0;
    private ArrayList<LatLng> tempLatLng = new ArrayList<>();
    private ArrayList<Marker> tempMarker = new ArrayList<>();
    //侧滑相关
    @BindView(R.id.right)
    FrameLayout right;
    @BindView(R.id.nav_view)
    NavigationView left;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    private boolean isDrawer = false;
    //侧滑内部控件
    TextView username;
    TextView usermail;
    CircleImageView icon;
    //弹出的popwindow
    private PopupWindow popupWindow;
    private int navigationHeight;   //弹窗弹出的位置
    //弹窗内部UI
    ImageView locationImage;
    TextView locationName;
    ImageView popDetail;
    //弹窗的UI
    View view;
    private TopRightMenu popMenu;
    //图片浏览
    private ArrayList<RolloutInfo> data = new ArrayList<>();
    protected RolloutBDInfo bdInfo;
    protected RolloutInfo imageInfo;
    private String tempImage;
    //云端登陆
    User user;
    //从云端获取的AD数组
    List<AD> bmobData = new ArrayList<>();
    //是否显示UI变量
    private boolean mVisible;
    //进度显示
    ProgressDialog progressDialog;
    //活动扩充至状态栏部分
    //实现状态栏图标和文字颜色为暗色
    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    private long mExitTime;
    /**
     * 以下全部都是隐藏界面的逻辑
     */
    private final Handler mHideHandler = new Handler();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @Override
        public void run() {
            mMapView.setSystemUiVisibility(option);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
            mToolBar.setVisibility(View.VISIBLE);
        }
    };

    //点击隐藏UI
    private void toggle() {
        if (mVisible) {
            hide();
            //mUiSettings.setMyLocationButtonEnabled(true);
        } else {
            show();
            //mUiSettings.setMyLocationButtonEnabled(false);
        }
    }
    //隐藏逻辑
    private void hide() {
        // Hide UI first
        mControlsView.setVisibility(View.GONE);
        mToolBar.setVisibility(View.GONE);
        mVisible = false;
        // 安排一个可运行的程序，以便在延迟后删除状态和导航栏
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
    //显示逻辑
    private void show() {
        // Show the system bar
        mMapView.setSystemUiVisibility(option);
        mVisible = true;

        // 安排一个可运行的延迟之后显示UI元素
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //用户
        user = BmobUser.getCurrentUser(User.class);
        mVisible = true;
        //沉浸式任务栏
        initImmersiveStatusBar();
        //地图设置
        initMapSetting();
        mMapView.onCreate(savedInstanceState);

        //初始化底部导航栏
        initBottomNavigationBar();
        //右上角弹出菜单
        initTopRightMenu();
        //初始化侧滑栏
        initDrawerLayout();
        //加载数据
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("加载中");
        progressDialog.show();

        //使用Bmob获取用来标记的信息
        BmobQuery<AD> adBmobQuery = new BmobQuery<>();
        //先判断是否有缓存
        boolean isCache = adBmobQuery.hasCachedResult(AD.class);
        if(isCache){
            adBmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);	// 先从缓存取数据，如果没有的话，再从网络取。
        }else{
            adBmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);	// 如果没有缓存的话，则先从网络中取
        }
        adBmobQuery.findObjectsObservable(AD.class)
                .subscribe(new Subscriber<List<AD>>() {
                    @Override
                    public void onCompleted() {
                        //处理逻辑(定位)
                        if (mAMapLocationClient != null) {
                            mAMapLocationClient.startLocation();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(MainActivity.this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<AD> ads) {
                        bmobData = ads;
                        addMarksToMap(ads);
                        //加载顶部导航栏的头像
                        Glide.with(MainActivity.this)
                                .load(user.getUserIcon())
                                .into(new GlideDrawableImageViewTarget(mCircleImageView){
                                    @Override
                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                        super.onResourceReady(resource, animation);
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                });
        //底部导航栏图片资源
        infoIcon.setImageResource(R.drawable.ic_visibility_off_unpress);
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        //弹窗弹出位置
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        navigationHeight = getResources().getDimensionPixelSize(resourceId);
    }

    //沉浸式工具栏
    private void initImmersiveStatusBar(){
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    //初始化并设置地图
    private void initMapSetting(){
        //初始化MapView
        if(mAMap == null){
            mAMap = mMapView.getMap();
            mAMap.showIndoorMap(true);
            mUiSettings = mAMap.getUiSettings();
            /*此处为系统自定义的定位
            myLocationStyle = new MyLocationStyle();
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_1));
            mAMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW));*/
            initLocation();
            mAMap.setOnMarkerClickListener(this);
        }
        // 设置用户交互以手动显示或隐藏系统UI。
        mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //隐藏infoWindows
                if(tempMarker.size() == 0) {
                    //没有可见的marker
                    toggle();
                }else {
                    deleteInfoWindows(tempMarker);
                    toggle();
                }
            }
        });
        mAMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                toggle();
            }
        });
        //地图基本的UI设置
        //标志设置在左下方
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
        //显示比例尺
        mUiSettings.setScaleControlsEnabled(true);
        //定位按钮显示
        //mUiSettings.setMyLocationButtonEnabled(true);
        //mAMap.setMyLocationEnabled(true);
        //缩放按钮不显示
        mUiSettings.setZoomControlsEnabled(false);
        //禁止旋转手势
        mUiSettings.setRotateGesturesEnabled(false);
    }

    //初始化定位相关
    private void initLocation() {
        //初始化定位
        mAMapLocationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位回调监听
        mAMapLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息

                        //取出经纬度
                        LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                        //添加Marker显示定位位置
                        if (locationMarker == null) {
                            //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
                            locationMarker = mAMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ad_location))));
                            //jumpPoint(locationMarker);
                        } else {
                            //已经添加过了，修改位置即可
                            locationMarker.remove();
                            //locationMarker.setPosition(latLng);
                            locationMarker = mAMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ad_location))));
                        }

                        //然后可以移动到定位点,使用animateCamera就有动画效果
                        //mAMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                latLng, 15, 45, 0)),500,null);
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Toast.makeText(MainActivity.this,"location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //初始化定位参数
        mAMapLocationClientOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mAMapLocationClientOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mAMapLocationClientOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mAMapLocationClientOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mAMapLocationClientOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mAMapLocationClientOption.setInterval(100);
        //给定位客户端对象设置定位参数
        mAMapLocationClient.setLocationOption(mAMapLocationClientOption);
    }

    //对地图添加mark
    private void addMarksToMap(List<AD> ads){
        mAMap.clear();
        tempLatLng.clear();
        tempMarker.clear();
        LatLng latLng;
        MarkerOptions markerOptions;
        for(AD ad : ads){
            latLng = new LatLng(ad.getLatitude(),ad.getLongitude());
            tempLatLng.add(latLng);
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(ad.getName());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ad_location)));
            Marker marker = mAMap.addMarker(markerOptions);
            tempMarker.add(marker);
        }
    }

    //清除广告消息
    private void deleteAllMarkers(List<Marker> markers){
        for(Marker marker : markers){
            marker.remove();
        }
    }

    //清除infowindows
    private void deleteInfoWindows(List<Marker> markers){
        for(Marker marker : markers){
            marker.hideInfoWindow();
        }
    }

    //Marker点击回调
    @Override
    public boolean onMarkerClick(final Marker marker) {
        jumpPoint(marker);
        if(!marker.equals(locationMarker)){
            tempMarkerId = SearchTempMarkerIdFromLatLng(marker.getPosition());
            loadingCorrespongdingMessage(marker);
        }
        return false;
    }

    //点击事件所进行的加载
    private void loadingCorrespongdingMessage(Marker marker){
        openPopupWindow();
        String title = marker.getTitle();
        tempImage = getImage(title);
        locationName.setText(title);
        //此处为预览图片所进行的处理
        data.clear();
        bdInfo = new RolloutBDInfo();
        imageInfo = new RolloutInfo();
        //图片的宽高可以自己去设定,也可以计算图片宽高
        imageInfo.width = 1400;
        imageInfo.height = 1120;
        imageInfo.url = tempImage;
        data.add(imageInfo);
        //图片加载
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("加载中");
        Glide.with(this).
                load(tempImage).
                into(new GlideDrawableImageViewTarget(locationImage){
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        progressDialog.show();
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        progressDialog.dismiss();
                    }
                });
    }

    //得到图片
    public String getImage(String title){
        String s = null;
        for (AD ad:bmobData){
            if(ad.getName().equals(title)){
                s = ad.getImageID();
                break;
            }
        }
        return s;
    }

    //marker点击时跳动一下
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final LatLng templatLng = marker.getPosition();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mAMap.getProjection();
        Point startPoint = proj.toScreenLocation(templatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * templatLng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * templatLng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    //初始化底部导航栏
    private void initBottomNavigationBar(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //导航栏UI处理
                backIcon.setImageResource(R.drawable.back_press);
                backText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.press));
                if(infoText.getText().toString().equals("隐藏所有")) {
                    infoIcon.setImageDrawable(null);
                    infoIcon.setImageResource(R.drawable.ic_visibility_off_unpress);
                }else{
                    infoIcon.setImageDrawable(null);
                    infoIcon.setImageResource(R.drawable.ic_visibility_unpress);
                }
                infoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                advanceIcon.setImageResource(R.drawable.advance_unpress);
                advanceText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                //处理逻辑
                if(tempMarker.size() != 0) {
                    if (tempMarkerId != 0) {
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                tempLatLng.get(tempMarkerId - 1), 17, 0, 0)),500,null);
                        //jumpPoint(tempMarker.get(tempMarkerId - 1));
                        tempMarkerId --;
                    } else {
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                tempLatLng.get(tempLatLng.size() - 1), 17, 0, 0)),500,null);
                        //jumpPoint(tempMarker.get(tempMarker.size() - 1));
                        tempMarkerId = tempLatLng.size() - 1;
                    }
                }else{
                    Toast.makeText(getBaseContext(),"找不到位置",Toast.LENGTH_SHORT).show();
                }
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //导航栏UI处理
                backIcon.setImageResource(R.drawable.back_unpress);
                backText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                infoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.press));
                advanceIcon.setImageResource(R.drawable.advance_unpress);
                advanceText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                //处理逻辑(显示与隐藏)
                if(infoText.getText().toString().equals("隐藏所有")){
                    if(tempMarker.size() != 0) {
                        deleteAllMarkers(tempMarker);
                        tempMarker.clear();
                        infoIcon.setImageDrawable(null);
                        infoIcon.setImageResource(R.drawable.ic_visibility_press);
                        infoText.setText("显示所有");
                    }else{
                        Toast.makeText(getBaseContext(),"广告位置已经隐藏",Toast.LENGTH_SHORT).show();
                    }
                }else if(infoText.getText().toString().equals("显示所有")){
                    if(tempMarker.size() == 0) {
                        addMarksToMap(bmobData);
                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(bmobData.get(0).getLatitude(), bmobData.get(0).getLongitude())).include(new LatLng(bmobData.get(1).getLatitude(), bmobData.get(1).getLongitude()))
                                .include(new LatLng(bmobData.get(2).getLatitude(), bmobData.get(2).getLongitude())).include(new LatLng(bmobData.get(3).getLatitude(), bmobData.get(3).getLongitude())).build();
                        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                        infoIcon.setImageDrawable(null);
                        infoIcon.setImageResource(R.drawable.ic_visibility_off_press);
                        infoText.setText("隐藏所有");
                    }else {
                        Toast.makeText(getBaseContext(),"广告位置已经显示",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        advance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //导航栏UI处理
                backIcon.setImageResource(R.drawable.back_unpress);
                backText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                if(infoText.getText().toString().equals("隐藏所有")) {
                    infoIcon.setImageDrawable(null);
                    infoIcon.setImageResource(R.drawable.ic_visibility_off_unpress);
                }else{
                    infoIcon.setImageDrawable(null);
                    infoIcon.setImageResource(R.drawable.ic_visibility_unpress);
                }
                infoText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.unPress));
                advanceIcon.setImageResource(R.drawable.advance_press);
                advanceText.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.press));
                //处理逻辑
                if(tempMarker.size() != 0) {
                    if (tempMarkerId != tempLatLng.size() - 1) {
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                tempLatLng.get(tempMarkerId + 1), 17, 0, 0)),500,null);
                        //jumpPoint(tempMarker.get(tempMarkerId + 1));
                        tempMarkerId ++;
                    } else {
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                tempLatLng.get(0), 17, 0, 0)),500,null);
                        //jumpPoint(tempMarker.get(0));
                        tempMarkerId = 0;
                    }
                }else{
                    Toast.makeText(getBaseContext(),"找不到位置",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //右上角菜单
    private void initTopRightMenu(){
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAMapLocationClient != null) {
                    mAMapLocationClient.startLocation();
                }
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                intent.putExtra("data",(Serializable) bmobData);
                startActivityForResult(intent,SEARCH);
            }
        });
    }

    //侧滑栏
    private void initDrawerLayout(){

        left.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(android.view.MenuItem item) {
                switch (item.getItemId()){
                    case R.id.All_ADs:
                        //Intent intent = new Intent(MainActivity.this,AllADsActivity.class);
                        //startActivity(intent);
                        break;
                    case R.id.setting:
                        Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                        startActivityForResult(intent,SETTING);
                        overridePendingTransition(R.anim.in,R.anim.out);
                        break;
                    case R.id.aboutUs_menu:
                        break;
                    case R.id.normal_map:
                        mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
                        //mImageView.setImageResource(R.drawable.ic_more_horiz_black);
                        break;
                    case R.id.satellite_map:
                        mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                        //mImageView.setImageResource(R.drawable.ic_more_horiz_white);
                        break;
                    case R.id.night_map:
                        mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
                        //mImageView.setImageResource(R.drawable.ic_more_horiz_white);
                        break;
                    case R.id.bus_map:
                        mAMap.setMapType(AMap.MAP_TYPE_BUS);
                        //mImageView.setImageResource(R.drawable.ic_more_horiz_black);
                        break;
                    default:
                }
                return true;
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                isDrawer = true;
                //获取屏幕的宽高
                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                //设置右面的布局位置  根据左面菜单的right作为右面布局的left   左面的right+屏幕的宽度（或者right的宽度这里是相等的）为右面布局的right
                right.layout(left.getRight(), 0, left.getRight() + display.getWidth(), display.getHeight());
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawer = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        View headView = left.getHeaderView(0);
        username = (TextView) headView.findViewById(R.id.username);
        username.setText(user.getUsername());
        //用户邮箱
        usermail = (TextView) headView.findViewById(R.id.mail);
        usermail.setText(user.getEmail());
        //加载用户头像
        icon = (CircleImageView) headView.findViewById(R.id.icon_image);
        Glide.with(this).load(user.getUserIcon()).into(icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,UserCentreActivity.class);
                ActivityCompat.startActivity(MainActivity.this,intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                MainActivity.this,
                                new Pair<>(view, "icon"))
                                .toBundle());
            }
        });
    }
    private void openPopupWindow() {
        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        //设置PopupWindow的View
        view = LayoutInflater.from(this).inflate(R.layout.ad_detail_popwindow, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置背景,这个没什么效果，不添加会报错
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //设置点击弹窗外隐藏自身
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //设置动画
        popupWindow.setAnimationStyle(R.style.PopupWindow);
        //设置位置
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, navigationHeight);
        //设置消失监听
        popupWindow.setOnDismissListener(this);
        //设置PopupWindow的View点击事件
        setOnPopupViewClick(view);
        //设置背景色(半透明)
        setBackgroundAlpha(0.8f);
    }

    private void setOnPopupViewClick(View view) {

        locationImage = (ImageView) view.findViewById(R.id.locationImage);
        locationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ADsDetailActivity.class);
                intent.putExtra("data",tempImage);
                ActivityCompat.startActivity(MainActivity.this,
                        intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                MainActivity.this,
                                new Pair<>(view, "detail_image"))
                                .toBundle());
            }
        });
        locationImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //获取相对位置，左边和顶部
                int location[] = new int[2];
                locationImage.getLocationOnScreen(location);
                bdInfo.x = location[0];
                bdInfo.y = location[1];
                //视图布局的宽高
                bdInfo.width = locationImage.getWidth();
                bdInfo.height = locationImage.getHeight();
                //跳转和传数据都必须要
                Intent intent = new Intent(MainActivity.this, RolloutPreviewActivity.class);
                intent.putExtra("data", (Serializable) data);
                intent.putExtra("bdinfo",bdInfo);
                intent.putExtra("type", 0);//单图传0
                intent.putExtra("index",0);
                startActivity(intent);
                overridePendingTransition(0,0);
                return false;
            }
        });
        popDetail = (ImageView) view.findViewById(R.id.popDetail);
        popDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popMenu = new TopRightMenu(MainActivity.this);
                List<MenuItem> menuItemList = new ArrayList<>();
                menuItemList.add(new MenuItem(R.drawable.ads_message,"文字内容"));
                menuItemList.add(new MenuItem(R.drawable.ads_physical,"物理信息"));
                menuItemList.add(new MenuItem(R.drawable.ads_company,"公司信息"));
                menuItemList.add(new MenuItem(R.drawable.ads_maintain,"维护信息"));
                popMenu.addMenuList(menuItemList)
                        .setOnMenuItemClickListener(new TopRightMenu.OnMenuItemClickListener() {
                            @Override
                            public void onMenuItemClick(int position) {
                                Toast.makeText(MainActivity.this, "待完善", Toast.LENGTH_SHORT).show();
                                switch (position){
                                    case 0:

                                        break;
                                    case 1:

                                        break;
                                    case 2:

                                        break;
                                    case 3:

                                        break;
                                    default:
                                }
                            }
                        })
                        .showAsDropDown(popDetail,-335,-25)
                .dimBackground(false);
            }
        });
        locationName = (TextView) view.findViewById(R.id.locationName);

    }

    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

   @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        tempMarkerId = 0;
        //退出界面的时候停止定位
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();
        }
    }

    //当侧滑栏没有隐藏时，先隐藏侧滑栏，故重写onBackPress方法
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }else if ((System.currentTimeMillis() - mExitTime) > 2000){
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Gson gson = new Gson();
                    User temp = gson.fromJson(s, User.class);
                    Glide.with(MainActivity.this).load(temp.getUserIcon()).into(icon);
                    Glide.with(MainActivity.this)
                            .load(temp.getUserIcon())
                            .into(new GlideDrawableImageViewTarget(mCircleImageView) {
                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                    progressDialog.show();
                                }

                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                    super.onResourceReady(resource, animation);
                                    progressDialog.dismiss();
                                }
                            });
                    //用户邮箱
                    usermail.setText(temp.getEmail());
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case SEARCH :
                if(resultCode == RESULT_OK){
                    LatLng latLng = data.getParcelableExtra("search_return");
                    if(tempMarker.size() != 0) {
                        tempMarkerId = SearchTempMarkerIdFromLatLng(latLng);
                        mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                latLng, 17, 0, 0)), 500, null);
                        Marker marker = SearchCorrespondingMarker(latLng);
                        loadingCorrespongdingMessage(marker);
                    }else{
                        Toast.makeText(this, "找不到位置！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case SETTING :
                if(resultCode == RESULT_OK){
                    String type = data.getStringExtra("setting");
                    if(type.equals("logout")) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.in, R.anim.out);
                        finish();
                    }else{
                        drawer.closeDrawer(Gravity.START);
                    }
                }
        }
    }

    //寻找经纬度对应序号
    private int SearchTempMarkerIdFromLatLng(LatLng latLng){
        int temp = 0;
        for(int i = 0; i < tempLatLng.size(); i ++){
            if(tempLatLng.get(i).equals(latLng)){
                temp = i;
                break;
            }
        }
        return temp;
    }
    //
    private Marker SearchCorrespondingMarker(LatLng latLng){
        Marker temp = null;
        for(int i = 0; i < tempMarker.size(); i++){
            if(tempMarker.get(i).getPosition().equals(latLng)){
                temp = tempMarker.get(i);
                break;
            }
        }
        return temp;
    }
    //这是最底层activity,不需要背景透明
    /*@Override
    public boolean isTransparent() {
        return false;
    }*/
}
