package com.xia.adgis.Register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.kevin.crop.UCrop;
import com.xia.adgis.CropActivity;
import com.xia.adgis.Login.LoginActivity;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;
import com.xia.adgis.Register.check.PhoneCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class RegisterActivity extends AppCompatActivity implements PopupWindow.OnDismissListener {

    //存储的客户属性值，便于后续注册插值处理
    private String registerphone = "";
    //图片真实路径
    String image;
    //图片网络路径
    String imagePath = "1";
    //图片用来删除的路径(Bmob要求)
    String delete = "1";
    //总体的进度条
    @BindView(R.id.progressBar)
    ProgressBar total;
    //注册第一个界面
    @BindView(R.id.first)
    LinearLayout first;
    @BindView(R.id.back1)
    ImageView firstBack;
    @BindView(R.id.register_phone)
    EditText userPhone;
    @BindView(R.id.next_move1)
    Button firstNextMove;
    //注册第二个界面
    @BindView(R.id.second)
    LinearLayout second;
    //注册上传的头像
    @BindView(R.id.main_frag_picture_iv)
    CircleImageView mPictureIv;
    @BindView(R.id.back2)
    ImageView secondBack;
    @BindView(R.id.next_move2)
    Button nextmove;
    //注册的第三个界面
    @BindView(R.id.third)
    LinearLayout third;
    @BindView(R.id.back3)
    ImageView thirdBack;
    @BindView(R.id.user_icon)
    CircleImageView userIcon;
    @BindView(R.id.register_username)
    EditText userName;
    @BindView(R.id.register_password)
    EditText userPassWord;
    @BindView(R.id.register_password1)
    EditText userPassWord1;
    @BindView(R.id.register)
    Button userRegister;
    //注册相关
    private UserLoginTask mAuthTask = null;
    @BindView(R.id.register_form)
    View mProgressView;
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
    //加载对话框
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        //初始化加载对话框
        initProgressDialog();
        //初始化头像选择弹窗及图片文件路径
        initIconPicker();
        //初始化第一个界面
        initFirst();
        //初始化第二个界面
        initSecond();
        //初始化第三个界面
        initThird();
        //注册开始便是进行了1/3了
        total.setProgress(100);
    }

    //初始化加载对话框
    private void initProgressDialog(){
        loading = new ProgressDialog(this);
        loading.setMessage("加载中...");
        loading.setCancelable(false);
    }

    //初始化头像选择事项(包括临时文件路径,目标文件路径)
    private void initIconPicker(){
        //头像弹窗弹出位置
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        navigationHeight = getResources().getDimensionPixelSize(resourceId);
        //得到文件存储路径
        String filename = "cropImage.jpeg";
        //目标路径
        mDestinationUri = Uri.fromFile(new File(getCacheDir(),filename));
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mDestinationUri));
        //拍照临时照片
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
    }

    //第一个界面处理的逻辑
    private void initFirst(){
        //退出按钮
        firstBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
            }
        });
        //核验电话号码
        firstNextMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cancel = false;

                userPhone.setError(null);

                //电话号码正则表达式
                String regexPhone = "[\\d]{11}";
                String phone = userPhone.getText().toString();

                if(TextUtils.isEmpty(phone)){
                    userPhone.setError("输入为空！");
                    cancel = true;
                } else if(!phone.matches(regexPhone)){
                    userPhone.setError("手机号码必须为11位");
                    cancel = true;
                }else if(!PhoneCheck.checkNumber(phone.substring(0,3))) {
                    userPhone.setError("号码非法");
                    cancel = true;
                }
                if(cancel){
                    userPhone.requestFocus();
                }else {
                    registerphone = phone;
                    total.incrementProgressBy(100);
                    first.setVisibility(View.GONE);
                    second.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    //第二个界面处理的逻辑
    private void initSecond(){
        //退出按钮
        secondBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
            }
        });
        //选择头像进行上传
        mPictureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectIcon();
            }
        });
        //下一步
        nextmove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(image.equals("1")){
                    Toast.makeText(RegisterActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                }else{
                    //头像上传显示加载框(并且无法触摸退出)
                    loading.setTitle("上传头像");
                    loading.setMessage("上传中");
                    loading.setCanceledOnTouchOutside(false);
                    loading.show();
                    //需考虑多次选择会导致多张上传
                    //上传头像文件
                    final BmobFile bmobFile = new BmobFile(new File(image));
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                imagePath = bmobFile.getFileUrl();
                                delete = bmobFile.getUrl();
                                loading.setTitle("加载头像");
                                loading.setMessage("加载中...");
                                //加载第三步的头像
                                Glide.with(RegisterActivity.this)
                                        .load(imagePath)
                                        .override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                                        .into(new GlideDrawableImageViewTarget(userIcon){
                                            @Override
                                            public void onLoadStarted(Drawable placeholder) {
                                                super.onLoadStarted(placeholder);
                                                loading.show();
                                            }

                                            @Override
                                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                                super.onLoadFailed(e, errorDrawable);
                                                loading.dismiss();
                                                Toast.makeText(RegisterActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                                super.onResourceReady(resource, animation);
                                                loading.dismiss();
                                            }
                                        });
                                total.incrementProgressBy(100);
                                second.setVisibility(View.GONE);
                                third.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onProgress(Integer value) {
                            if(value == 100){
                                loading.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    //选择头像
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

    //弹窗内点击事件
    private void setOnPopupViewClick(View view){
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

    //点击弹窗后屏幕能变暗
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

    //回调事件的处理
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
    //处理剪切成功的返回值
    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri) {
            Bitmap bitmap = null;
            try {
                //只能使用这种方法得出
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                mPictureIv.setImageBitmap(bitmap);
                String filePath = resultUri.getEncodedPath();
                //获得了图片路径
                image = Uri.decode (filePath);
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
    //裁剪图片方法实现
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1024, 1024)
                .withTargetActivity(CropActivity.class)
                .start(this);
    }


    //第三个界面处理的逻辑
    private void initThird(){

        //退出按钮
        thirdBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
            }
        });

        userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                total.incrementProgressBy(100);
                attemptRegister();
            }
        });

    }

    private void attemptRegister(){
        if(mAuthTask != null){
            return;
        }

        //重置错误
        userName.setError(null);
        userPassWord.setError(null);
        userPassWord1.setError(null);

        //在注册时尝试存储值
        String name = userName.getText().toString();
        String password = userPassWord.getText().toString();
        String password1 = userPassWord1.getText().toString();

        boolean cancel = false;
        View focusView = null;
        //密码正则表达式
        String regexPwd = "[\\d]+";

        //检查用户名合法性
        if(TextUtils.isEmpty(name)){
            userName.setError("帐户名为空");
            focusView = userName;
            cancel = true;
        }

        //检查密码合法性
        if(TextUtils.isEmpty(password)){
            userPassWord.setError("输入密码为空!");
            focusView = userPassWord;
            cancel = true;
        }else if(!isPasswordValid(password)){
            userPassWord.setError("密码太短!");
            focusView = userPassWord;
            cancel = true;
        }else if(!password.equals(password1)){
            userPassWord.setError("两次输入密码不一样！");
            focusView = userPassWord;
            cancel = true;
        }else if(password.matches(regexPwd)){
            userPassWord.setError("密码必须包含一个字符！");
            focusView = userPassWord;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            loading.setMessage("注册中...");
            loading.show();
            mAuthTask = new UserLoginTask(name,registerphone,password,imagePath,delete);
            mAuthTask.execute((Void) null);
        }
    }

    //密码是否太短
    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    /**
     * *表示用于认证用户的异步登录/注册任务。
     */
    public class UserLoginTask extends AsyncTask<Void, Integer, Void> {

        private final String Name;
        private final String Tel;
        private final String mPassword;
        private final String mIcon;
        private final String mDelete;

        UserLoginTask(String name, String tel, String password, String icon, String delete) {
            Name = name;
            Tel = tel;
            mPassword = password;
            mIcon = icon;
            mDelete = delete;
        }

        @Override
        protected Void doInBackground(Void... params) {
            User user = new User();
            user.setUsername(Name);
            user.setPassword(mPassword);
            user.setMobilePhoneNumber(Tel);
            user.setUserIcon(mIcon);
            user.setNickName("还没有设置昵称哦");
            user.setSex("神秘人");
            user.setMotto("这个人很懒，什么也没留下...");
            user.setBirthday("未知");
            user.setAddress("未编辑");
            user.setUserIconUri(mDelete);
            user.signUp(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getBaseContext(), "注册成功", Toast.LENGTH_LONG).show();
                        finish();
                        overridePendingTransition(R.anim.in_1,R.anim.out_1);
                    } else {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        BmobFile tempBmobFile = new BmobFile();
                        tempBmobFile.setUrl(mDelete);
                        tempBmobFile.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e != null) {
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }else{
                                    mAuthTask = null;
                                    loading.dismiss();
                                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                    mPictureIv.setImageResource(R.drawable.camera);
                                    userIcon.setImageResource(R.drawable.ic_account_);
                                    userName.setText("");
                                    userPassWord.setText("");
                                    userPassWord1.setText("");
                                    userPhone.setText("");
                                    first.setVisibility(View.VISIBLE);
                                    second.setVisibility(View.GONE);
                                    third.setVisibility(View.GONE);
                                    image = "1";
                                }
                            }
                        });
                    }
                }
            });
            return null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            loading.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1,R.anim.out_1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imagePath = "1";
        if(!TextUtils.isEmpty(userName.getText())&&!TextUtils.isEmpty(userPassWord.getText())){
            LoginActivity.mUsernameView.setText(userName.getText());
            LoginActivity.mPasswordView.setText(userPassWord.getText());
        }
    }
}
