package com.xia.adgis.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xia.adgis.Login.Adapter.UserNameHistoryAdapter;
import com.xia.adgis.Login.DataBase.UserSqliteHelper;
import com.xia.adgis.Main.Activity.ForgetPassWordActivity;
import com.xia.adgis.Main.Activity.MainActivity;
import com.xia.adgis.R;
import com.xia.adgis.Register.RegisterActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import rx.Subscriber;

/**
 * 登录屏幕，通过电子邮件/密码提供登录。
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * 跟踪登录任务，确保我们可以取消它，如果请求。
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    public static AutoCompleteTextView mUsernameView;
    public static EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox checkbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 设置登录表单。
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //下拉列表的历史登陆用户
        UserNameHistort();
        //记住密码
        checkbox = (CheckBox) findViewById(R.id.checkBox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences sp = getSharedPreferences("user", 0);
                SharedPreferences.Editor edit = sp.edit();
                if(isChecked){
                    edit.putBoolean("isCheck",true);
                    edit.commit();
                }else{
                    edit.putBoolean("isCheck",false);
                    edit.commit();
                }
            }
        });
        checkIsRemenber();

        //开启注册界面
        findViewById(R.id.register).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in,R.anim.out);
            }
        });

        //忘记密码
        findViewById(R.id.forget_pass).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPassWordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in,R.anim.out);
            }
        });
    }

    //判断记住密码是否选中
    private void checkIsRemenber() {
        SharedPreferences sp = getSharedPreferences("user", 0);
        boolean isRemenber = sp.getBoolean("isCheck", true);
        if(isRemenber){
            checkbox.setChecked(true);
            mUsernameView.setText(sp.getString("name",""));
            mPasswordView.setText(sp.getString("pwd",""));
        }
    }

    /**
     尝试登录或注册登录表单指定的帐户。
     如果存在表单错误（无效的电子邮件，缺少字段等），
     则会显示错误，并且不会进行实际的登录尝试。
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // 在登录尝试时存储值。
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 如果用户输入一个密码，请检查一个有效的密码。
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("密码为空！");
            focusView = mPasswordView;
            cancel = true;
        }else if(!isPasswordValid(password)){
            mPasswordView.setError("密码太短");
            focusView = mPasswordView;
            cancel = true;
        }

        // 检查一个有效的电子邮件地址。
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError("用户名为空！");
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // 有一个错误; 不要尝试登录并将第一个表单字段与一个错误集中。
            focusView.requestFocus();
        } else {
            //显示一个进度微调，并启动一个后台任务来执行用户登录尝试。
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    /**
     * 显示进度UI并隐藏登录表单。
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        //在Honeycomb MR2上，我们有ViewPropertyAnimator API，可以非常简单的动画。 如果可用，请使用这些API淡入进度微调器。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            // ViewPropertyAnimator API不可用，所以只需显示和隐藏相关的UI组件。
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //下拉列表显示历史登陆用户
    private void UserNameHistort(){
        List<String> usernames = new ArrayList<>();
        UserSqliteHelper helper = new UserSqliteHelper(LoginActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from history";
        Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()){
                usernames.add(cursor.getString(cursor.getColumnIndex("name")));
            }

        addEmailsToAutoComplete(usernames);
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //创建适配器来告诉AutoCompleteTextView在下拉列表中显示什么。
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        UserNameHistoryAdapter adapter = new UserNameHistoryAdapter(LoginActivity.this,R.layout.user_history_item,emailAddressCollection);
        //UserNameHistoryAdapter adapter = new UserNameHistoryAdapter(emailAddressCollection,LoginActivity.this);
        mUsernameView.setAdapter(adapter);

    }

    /**
     表示用于验证用户的异步登录/注册任务。
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Thread.sleep(300);
                BmobUser.loginByAccountObservable(BmobUser.class,mEmail,mPassword).subscribe(new Subscriber<BmobUser>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                        mAuthTask = null;
                        showProgress(false);
                    }

                    @Override
                    public void onNext(BmobUser bmobUser) {
                        UserSqliteHelper helper = new UserSqliteHelper(LoginActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        try {
                            String sql = "insert into history (name) values (?)";
                            db.execSQL(sql, new String[]{mEmail});

                        }catch (Exception e){
                            //Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        SharedPreferences sp = getSharedPreferences("user", 0);
                        SharedPreferences.Editor edit = sp.edit();
                        if (checkbox.isChecked()) {//勾选记住密码
                            edit.putBoolean("isCheck", true);
                            edit.putString("name", mEmail);
                            edit.putString("pwd", mPassword);
                            edit.commit();
                        } else {
                            edit.clear();
                            edit.commit();
                        }
                        Toast.makeText(getBaseContext(), bmobUser.getUsername()+"用户登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.in,R.anim.out);
                        finish();
                    }
                });

            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private long mExitTime;
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000){
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
        else {
            super.onBackPressed();
        }
    }
}

