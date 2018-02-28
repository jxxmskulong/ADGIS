package com.xia.adgis.Register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

import com.xia.adgis.Login.LoginActivity;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;
import com.xia.adgis.Register.check.PhoneCheck;
import com.xia.adgis.Register.Fragment.MainFragment;
import com.xia.adgis.Register.Fragment.Basic.BaseFragment;


public class RegisterActivity extends AppCompatActivity {

    //存储的客户属性值，便于后续注册插值处理
    public String registerphone = "";
    //总体的进度条
    public static ProgressBar total;
    //注册第一个界面
    public static LinearLayout first;
    private ImageView firstBack;
    private EditText userPhone;
    private Button firstNextMove;
    //注册第二个界面
    public static LinearLayout second;
    //注册的第三个界面
    public static LinearLayout third;
    private ImageView thirdBack;
    public static CircleImageView userIcon;
    private EditText userName;
    private EditText userPassWord;
    private EditText userPassWord1;
    private Button userRegister;
    //注册相关
    private UserLoginTask mAuthTask = null;
    private View mProgressView;
    private View mLogUpFormView;
    //Bmob相关
    private String objectId;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //初始化Bmob
        initView();
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        initFirst();
        initSecond();
        initThird();

        total.setProgress(100);

    }
    //视图初始化
    private void initView(){
        //总体进度条
        total = (ProgressBar)findViewById(R.id.progressBar);
        //注册第一个界面
        first = (LinearLayout)findViewById(R.id.first);
        firstBack = (ImageView)findViewById(R.id.back1);
        userPhone = (EditText)findViewById(R.id.register_phone);
        firstNextMove = (Button)findViewById(R.id.next_move1);
        //注册第二个界面
        second = (LinearLayout)findViewById(R.id.second);
        //注册第三个界面
        third = (LinearLayout)findViewById(R.id.third);
        thirdBack = (ImageView)findViewById(R.id.back3);
        userIcon = (CircleImageView) findViewById(R.id.user_icon);
        userName = (EditText) findViewById(R.id.register_username);
        userPassWord = (EditText) findViewById(R.id.register_password);
        userPassWord1 = (EditText)findViewById(R.id.register_password1);
        userRegister = (Button)findViewById(R.id.register);
        //注册相关
        mLogUpFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    //第一个界面处理的逻辑
    private void initFirst(){
        //退出按钮
        firstBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //userPhone.setText("15559721960");
        //文本框点击右侧图案清空
        userPhone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Drawable drawable = userPhone.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (motionEvent.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (motionEvent.getX() > userPhone.getWidth()
                        - userPhone.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    userPhone.setText("");
                }
                return false;
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
        initMainFragment();
    }

    public void initMainFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BaseFragment mFragment = MainFragment.newInstance();
        transaction.replace(R.id.second, mFragment);
        transaction.commit();
    }

    //第三个界面处理的逻辑
    private void initThird(){

        thirdBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = userName.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (motionEvent.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (motionEvent.getX() > userName.getWidth()
                        - userName.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    userName.setText("");
                }
                return false;
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
            showProgress(true);
            mAuthTask = new UserLoginTask(name,registerphone,password,MainFragment.s);
            mAuthTask.execute((Void) null);
        }
    }

    //密码是否太短
    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    /**
     * 显示进度UI并隐藏登录表单。
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLogUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLogUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLogUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLogUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * *表示用于认证用户的异步登录/注册任务。
     */
    public class UserLoginTask extends AsyncTask<Void, Integer, Boolean> {

        private final String Name;
        private final String Tel;
        private final String mPassword;
        private final String mIcon;

        UserLoginTask(String name, String tel, String password, String icon) {
            Name = name;
            Tel = tel;
            mPassword = password;
            mIcon = icon;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Simulate network access.
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
            user.setUserIconUri(MainFragment.delete);
            user.signUp(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getBaseContext(), "注册成功", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        BmobFile tempBmobFile = new BmobFile();
                        tempBmobFile.setUrl(MainFragment.delete);
                        tempBmobFile.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    mAuthTask = null;
                                    showProgress(false);
                                    //Toast.makeText(getBaseContext(),MainFragment.delete, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });
            return true;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!TextUtils.isEmpty(userName.getText())&&!TextUtils.isEmpty(userPassWord.getText())){
            LoginActivity.mUsernameView.setText(userName.getText());
            LoginActivity.mPasswordView.setText(userPassWord.getText());
        }
    }
}
