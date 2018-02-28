package com.xia.adgis.Main.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.swipeback.ISwipeBackActivity;
import com.example.swipeback.SwipeBackActivityImpl;
import com.kevin.crop.UCrop;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.xia.adgis.App;
import com.xia.adgis.CropActivity;
import com.xia.adgis.Main.Tool.BindPhoneDialog;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscriber;

public class EditUserActivity extends SwipeBackActivityImpl implements View.OnClickListener,PopupWindow.OnDismissListener {

    @BindView(R.id.toolbar_edit)
    Toolbar toolbar;
    @BindView(R.id.edit_photo_relative)
    RelativeLayout editPhoto;
    @BindView(R.id.edit_nickname)
    EditText editNickname;
    @BindView(R.id.edit_area)
    TextView editArea;
    @BindView(R.id.edit_sex)
    TextView editSex;
    @BindView(R.id.edit_birthday)
    TextView editBirthday;
    @BindView(R.id.edit_phone)
    TextView editPhone;
    @BindView(R.id.edit_mail)
    EditText editMail;
    @BindView(R.id.edit_motto)
    EditText editMotte;
    @BindView(R.id.edit_Textcount_text)
    TextView editCount;
    @BindView(R.id.edit_photo_image)
    CircleImageView editPhotoImage;
    //加载对话框
    ProgressDialog loading;
    //本地用户
    User user;
    //相册选图标记
    private static final int GALLERY_REQUEST = 0;
    //相机拍照标记
    private static final int CAMERA_REQUEST = 1;
    // 拍照临时图片
    private String mTempPhotoPath;
    // 剪切后图像文件
    private Uri mDestinationUri;
    //选择头像弹出窗口
    private PopupWindow popupWindow;
    //弹窗弹出的位置
    private int navigationHeight;
    View view;
    //图片路径
    String imagePath;
    //是否编辑了头像，默认为不编辑
    boolean isIcon = false;
    //选择城市界面
    CityPickerView mCityPickerView = new CityPickerView();
    //自定义定位相关
    public AMapLocationClient mAMapLocationClient;
    public AMapLocationClientOption mAMapLocationClientOption;
    String Province = "北京市";
    String City = "北京市";
    String District = "东城区";
    //性别选择
    ArrayList<String> gender = new ArrayList<>();
    OptionsPickerView mOptionsPickerView;
    //选择生日
    TimePickerView mTimePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        user = BmobUser.getCurrentUser(User.class);
        ButterKnife.bind(this);
        editPhoto.setOnClickListener(this);
        editArea.setOnClickListener(this);
        editSex.setOnClickListener(this);
        editBirthday.setOnClickListener(this);
        editPhone.setOnClickListener(this);
        //弹窗弹出位置
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        navigationHeight = getResources().getDimensionPixelSize(resourceId);
        //得到文件存储路径
        String filename = "cropImage.jpeg";
        //目标路径
        mDestinationUri = Uri.fromFile(new File(getCacheDir(),filename));
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mDestinationUri));
        //拍照临时照片
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
        //用户照片(即使不更改头像仍然能够进行编辑)
        imagePath = user.getUserIcon();
        //初始化toolbar
        initToolBar();
        //初始化加载对话框
        initProgressDialog();
        //进入后加载信息
        initLoadingIcon();
        loadingMessage();
        //先初始化定位配置
        initLocation();
        //预先加载仿iOS滚轮实现的全部数据
        mCityPickerView.init(this);
        //初始化性别选择
        initSexPicker();
        //预先加载时间信息
        initBirthdayPicker();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_photo_relative:
                SelectIcon();
                break;
            case R.id.edit_area:
                //进入即开始定位
                if (mAMapLocationClient != null) {
                    mAMapLocationClient.startLocation();
                    loading.setMessage("定位中...");
                    loading.show();
                }
                break;
            case R.id.edit_sex:
                mOptionsPickerView.show();
                break;
            case R.id.edit_birthday:
                mTimePickerView.show();
                break;
            case R.id.edit_phone:
                ChangePhone();
                break;
        }
    }

    //初始化toolBar
    private void initToolBar(){
        toolbar.setTitle("编辑个人资料");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //初始化加载对话框
    private void initProgressDialog(){
        loading = new ProgressDialog(this);
        loading.setMessage("加载中...");
        loading.setCancelable(true);
    }
    //进入后加载信息
    private void initLoadingIcon(){
        Glide.with(this)
                .load(user.getUserIcon())
                .into(new GlideDrawableImageViewTarget(editPhotoImage){
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        loading.show();
                        loadingMessage();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        loading.dismiss();
                        Toast.makeText(EditUserActivity.this, "头像加载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        loading.dismiss();
                    }
                });

        //进入后个人介绍剩余字数
        String temp = 20 - user.getMotto().length() + "";
        editCount.setText(temp);
        //个人介绍的文字限制
        editMotte.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 20) {
                    Editable mo = editMotte.getText();
                    CharSequence ss = mo.subSequence(0, 20);//获取从0位置末（1）开始到第16个字[字母，数字，汉子 都是为1的长度]
                    editMotte.setText(ss);
                }
                //光标靠后
                editMotte.setSelection(editMotte.getText().length());
                editNickname.setSelection(editNickname.getText().length());
                //剩余可输入字符个数
                int len = 20 - editable.length();
                editCount.setText(len <= 0 ? "0" : len + "");
            }
        });
    }

    private void loadingMessage(){
        editNickname.setText(user.getNickName());
        editArea.setText(user.getAddress());
        editSex.setText(user.getSex());
        editBirthday.setText(user.getBirthday());
        editPhone.setText(user.getMobilePhoneNumber());
        editMail.setText(user.getEmail());
        editMotte.setText(user.getMotto());
    }

    //头像选择

    private void SelectIcon(){

        //防止重复按按钮
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        view = LayoutInflater.from(this).inflate(R.layout.edit_icon_popwindow,null);
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
        setBackgroundAlpha(0.5f);
    }

    private void setOnPopupViewClick(View view) {
        TextView takePhoto = (TextView) view.findViewById(R.id.take_photo);
        TextView pickPicture = (TextView) view.findViewById(R.id.pick_picture);
        TextView cancel = (TextView) view.findViewById(R.id.picture_selector_cancel);
        //拍照
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePhoto();
            }
        });
        //选择图片
        pickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });
        //取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }
    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }
    //透明度
    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }

    //拍照
    private void TakePhoto() {
        popupWindow.dismiss();
        Intent takeIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        //考虑兼容性
        Uri imageUri; //照片文件临时存储路径
        File temp = new File(mTempPhotoPath);
        if(Build.VERSION.SDK_INT < 24){
            imageUri = Uri.fromFile(temp);
        }else {
            imageUri = FileProvider.getUriForFile(this, "com.example.cameraalbumtest.fileprovider", temp);
        }
        //下面这句指定调用相机拍照后的照片存储的路径
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takeIntent, CAMERA_REQUEST);
    }
    //从系统相册选择
    private void pickFromGallery() {
        popupWindow.dismiss();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:   // 调用相机拍照
                    //考虑兼容性
                    Uri imageUri; //照片文件临时存储路径
                    File temp = new File(mTempPhotoPath);
                    if(Build.VERSION.SDK_INT < 24){
                        imageUri = Uri.fromFile(temp);
                    }else {
                        imageUri = FileProvider.getUriForFile(this, "com.example.cameraalbumtest.fileprovider", temp);
                    }
                    startCropActivity(imageUri);
                    break;
                case GALLERY_REQUEST:  // 直接从相册获取
                    startCropActivity(data.getData());
                    break;
                case UCrop.REQUEST_CROP:    // 裁剪图片结果
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:    // 裁剪图片错误
                    handleCropError(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //裁剪图片方法实现
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1024, 1024)
                .withTargetActivity(CropActivity.class)
                .start(this);
    }

    //处理剪切成功的返回值
    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri) {
            Bitmap bitmap = null;
            try {
                //只能使用这种方法得出
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                editPhotoImage.setImageBitmap(bitmap);
                String filePath = resultUri.getEncodedPath();
                //获得了图片路径
                imagePath = Uri.decode (filePath);
                //此时头像已经被编辑
                isIcon = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }
    //处理剪切失败返回值
    private void handleCropError(Intent result) {
        deleteTempPhotoFile();
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }
    //删除拍照临时文件
    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }
    //地址选择
    private void CityPick(){
        CityConfig cityConfig = new CityConfig.Builder().title("选择地区")//标题
                .titleTextSize(18)//标题文字大小
                .titleTextColor("#585858")//标题文字颜色
                .titleBackgroundColor("#E9E9E9")//标题栏背景色
                .confirmText("确定")//确认按钮文字
                .confirmTextSize(16)//确认按钮文字大小
                .cancelText("取消")//取消按钮文字
                .cancelTextSize(16)//取消按钮文字大小
                .setCityWheelType(CityConfig.WheelType.PRO_CITY_DIS)//显示省市区三类联动
                .showBackground(true)//是否显示半透明背景
                .visibleItemsCount(5)//显示item的数量
                .province(Province)//默认显示的省份
                .city(City)//默认显示省份下面的城市
                .district(District)//默认显示省市下面的区县数据
                .provinceCyclic(true)//省份滚轮是否可以循环滚动
                .cityCyclic(true)//城市滚轮是否可以循环滚动
                .districtCyclic(true)//区县滚轮是否循环滚动
                .setCustomItemLayout(R.layout.item_city)
                .setCustomItemTextViewId(R.id.item_city_name)
                .drawShadows(true)//显示模糊效果
                .setShowGAT(true)//显示港澳台数据
                .setLineColor("#303F9F")//分割线颜色
                .setLineHeigh(5)//横线高度
                .build();
        mCityPickerView.setConfig(cityConfig);
        mCityPickerView.setOnCityItemClickListener(new OnCityItemClickListener() {

            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                StringBuffer area = new StringBuffer();
                if (province != null){
                    area.append(province.getName());
                    area.append("-");
                }
                if (city != null){
                    area.append(city.getName());
                    area.append("-");
                }
                if (district != null){
                    area.append(district.getName());
                }
                editArea.setText(area);
            }
        });
        mCityPickerView.showCityPicker();
    }

    //初始化定位相关
    private void initLocation() {
        //初始化定位
        mAMapLocationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位回调监听
        mAMapLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation != null){
                    if(aMapLocation.getErrorCode() == 0){
                        //定位成功回调信息，设置相关信息
                        Province = aMapLocation.getProvince();
                        City = aMapLocation.getCity();
                        District = aMapLocation.getDistrict();
                        loading.dismiss();
                        CityPick();
                    }
                }else{
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Toast.makeText(EditUserActivity.this,"location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo(),Toast.LENGTH_SHORT).show();
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
        //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        //给定位客户端对象设置定位参数
        mAMapLocationClient.setLocationOption(mAMapLocationClientOption);
    }

    //性别选择
    private void initSexPicker(){
        //初始化选择数据源
        gender.clear();
        gender.add("男");
        gender.add("女");
        gender.add("神秘人");
        mOptionsPickerView = new OptionsPickerView.Builder(EditUserActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                editSex.setText(gender.get(options1));
            }
        })
                .setTitleText("选择性别")
                .setContentTextSize(20)
                .setDividerColor(Color.BLACK)
                .setTypeface(Typeface.DEFAULT_BOLD)
                .setTextColorCenter(Color.BLACK)
                .setLineSpacingMultiplier(2.0f)
                .build();
        mOptionsPickerView.setPicker(gender);

    }
    //生日选择
    private void initBirthdayPicker(){
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(1990, 0, 1);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1930, 0, 23);
        Calendar endDate = Calendar.getInstance();
        //时间选择器
        mTimePickerView = new TimePickerView.Builder(EditUserActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                editBirthday.setText(getTime(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                //设置显示标签
                .setLabel("年","月","日",null,null,null)
                .setTitleText("选择生日")
                .setDividerColor(Color.BLACK)
                //滚轮字体大小
                .setContentSize(20)
                //两横线之间间隔倍数1.2 ~ 2
                .setLineSpacingMultiplier(2.0f)
                .isCyclic(false)
                .setDate(selectedDate)
                .setRangDate(startDate,endDate)
                //设置为true则只有中间部分显示标签
                .isCenterLabel(true)
                .build();
    }

    //获取时间
    private String getTime(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    //更换手机
    private void ChangePhone(){
        BindPhoneDialog bindPhoneDialog = new BindPhoneDialog();
        bindPhoneDialog.show(getSupportFragmentManager());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.save:
                saveUserModify();
                break;
        }
        return true;
    }

    //
    private void saveUserModify(){

        String nickName = editNickname.getText().toString();
        String area = editArea.getText().toString();
        String sex = editSex.getText().toString();
        String birthday = editBirthday.getText().toString();
        String phone = editPhone.getText().toString();
        String email = editMail.getText().toString();
        String motto = editMotte.getText().toString();

        if(TextUtils.isEmpty(nickName)){
            editNickname.requestFocus();
            editNickname.setError("昵称不能为空");
            return;
        }
        if(TextUtils.isEmpty(email)){
            editMail.requestFocus();
            editMail.setError("邮箱不能为空");
            return;
        }else if(!email.contains("@")){
            editMail.requestFocus();
            editMail.setError("邮箱名不合法");
            return;
        }else if(!email.contains(".com")){
            editMail.requestFocus();
            editMail.setError("邮箱名不合法");
            return;
        }
        if(TextUtils.isEmpty(motto)){
            editMotte.requestFocus();
            editMotte.setError("个性说明不能空");
            return;
        }
        loading.setMessage("修改信息中...");
        loading.show();

        //修改信息(判断头像是否已经修改)
        if(isIcon){
            saveUserAndIcon(nickName,area,sex,birthday,phone,email,motto);
        }else{
            saveUser(nickName,area,sex,birthday,phone,email,motto);
        }
    }
    //在最终保存用户时，需要注意的是在上传头像前，需要将前一张头像从服务器中删除，为了节约服务器存储空间
    private void saveUserAndIcon(final String nickName, final String area, final String sex, final String birthday, final String phone, final String email, final String motto){
        //删除前一次头像文件
        BmobFile lastTimeIcon = new BmobFile();
        lastTimeIcon.setUrl(user.getUserIconUri());
        lastTimeIcon.deleteObservable().subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {
                loading.dismiss();
                Toast.makeText(EditUserActivity.this, "删除失败"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Void aVoid) {
                final BmobFile thisTimeIcon = new BmobFile(new File(imagePath));
                thisTimeIcon.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null){
                            //更新用户
                            User newUser = new User();
                            newUser.setUserIcon(thisTimeIcon.getFileUrl());
                            newUser.setNickName(nickName);
                            newUser.setAddress(area);
                            newUser.setSex(sex);
                            newUser.setBirthday(birthday);
                            newUser.setMobilePhoneNumber(phone);
                            if(!email.equals(user.getEmail())){
                                newUser.setEmail(email);
                            }
                            newUser.setMotto(motto);
                            newUser.setUserIconUri(thisTimeIcon.getUrl());
                            newUser.update(user.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e == null){
                                        Toast.makeText(EditUserActivity.this, "信息修改成功", Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                        finish();
                                        //继续刷新
                                        UserCentreActivity.refreshLayout.autoRefresh();
                                    }else{
                                        Toast.makeText(EditUserActivity.this, "更新失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }
                                }
                            });
                        }else {
                            loading.dismiss();
                            Toast.makeText(EditUserActivity.this, "上传失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void saveUser(final String nickName, final String area, final String sex, final String birthday, final String phone, final String email, final String motto){
        User newUser = new User();
        newUser.setNickName(nickName);
        newUser.setAddress(area);
        newUser.setSex(sex);
        newUser.setBirthday(birthday);
        newUser.setMobilePhoneNumber(phone);
        if(!email.equals(user.getEmail())){
            newUser.setEmail(email);
        }
        newUser.setMotto(motto);
        newUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(EditUserActivity.this, "信息修改成功", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    finish();
                    //继续刷新
                    UserCentreActivity.refreshLayout.autoRefresh();
                }else {
                    Toast.makeText(EditUserActivity.this, "更新失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        });
    }
    @Override
    public ISwipeBackActivity getPreActivity() {
        return (ISwipeBackActivity) App.getInstance().getStack().getBackActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出界面的时候停止定位
        if (mAMapLocationClient != null) {
            mAMapLocationClient.stopLocation();
        }
    }

}
