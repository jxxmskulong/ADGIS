package com.xia.adgis.Main.Activity;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;
import com.xia.adgis.Register.check.PhoneCheck;
import com.xia.adgis.Utils.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Subscriber;

public class ForgetPassWordActivity extends AppCompatActivity {

    @BindView(R.id.forget_pass_progress)
    ProgressBar progressBar;
    @BindView(R.id.forget_pass_main)
    View main_interface;
    @BindView(R.id.forget_pass_toolbar)
    Toolbar toolbar;
    @BindView(R.id.forget_pass_check_phone)
    LinearLayout check_phone;
    @BindView(R.id.forget_pass_phone)
    ClearEditText forget_pass_phone;
    @BindView(R.id.forget_pass_code)
    ClearEditText forget_pass_code;
    @BindView(R.id.forget_pass_send_code)
    TextView send_code;
    @BindView(R.id.confirm_code)
    TextView confirm_code;
    @BindView(R.id.forget_pass_change_pass)
    LinearLayout change_pass;
    @BindView(R.id.new_password)
    EditText new_pass;
    @BindView(R.id.new_password1)
    EditText new_pass1;
    @BindView(R.id.confirm_password)
    TextView confirm_password;
    //验证码
    String code;
    //当前用户
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word);
        ButterKnife.bind(this);
        user = BmobUser.getCurrentUser(User.class);
        //初始化toolbar
        initToolbar();
        //验证码发送并验证阶段
        initCheckPhone();
        //初始化重置密码阶段
        initResetPassWord();
    }

    //初始化toolbar
    private void initToolbar(){
        toolbar.setTitle("忘记密码");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
            }
        });
    }

    //初始化验证码并确认
    private void initCheckPhone(){
        //发送验证码
        send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setTitle("进行手机验证");
                requestSMSCode();
            }
        });
        //验证验证码
        confirm_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOrBind();
            }
        });
    }

    //发送短信验证码
    private void requestSMSCode() {
        String phone = forget_pass_phone.getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "电话输入为空", Toast.LENGTH_SHORT).show();
        } else if (!phone.matches("[\\d]{11}")) {
            Toast.makeText(this, "手机号码必须为11位", Toast.LENGTH_SHORT).show();
        } else if(!PhoneCheck.checkNumber(phone.substring(0,3))){
            Toast.makeText(this, "号码非法!", Toast.LENGTH_SHORT).show();
        }else{
            final MyCountTimer timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(phone, "ADGIS", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {
                        Toast.makeText(ForgetPassWordActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                        send_code.setEnabled(false);
                    } else {
                        Toast.makeText(ForgetPassWordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        timer.cancel();
                        send_code.setEnabled(true);
                    }
                }
            });
        }
    }

    //自定义倒计时
    private class MyCountTimer extends CountDownTimer {

        private MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            String temp = (millisUntilFinished / 1000) +"秒后重发";
            send_code.setText(temp);
            send_code.setEnabled(false);
        }
        @Override
        public void onFinish() {
            send_code.setText("重新发送验证码");
            send_code.setEnabled(true);
        }
    }

    //验证验证码
    private void verifyOrBind() {
        final String phone = forget_pass_phone.getText().toString();
        code = forget_pass_code.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.matches("[\\d]{11}")) {
            Toast.makeText(this, "手机号码必须为11位", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!PhoneCheck.checkNumber(phone.substring(0,3))){
            Toast.makeText(this, "号码非法!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!code.matches("[\\d]{6}")){
            Toast.makeText(this, "验证码必须为6位", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress(true);
        BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null) {
                    showProgress(false);
                    Toast.makeText(ForgetPassWordActivity.this, "号码验证成功", Toast.LENGTH_SHORT).show();
                    check_phone.setVisibility(View.GONE);
                    change_pass.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(ForgetPassWordActivity.this, "号码验证失败", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            }

        });
    }

    //初始化重置密码
    private void initResetPassWord(){
        confirm_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPassWord();
            }
        });
    }

    private void ResetPassWord(){
        String pass = new_pass.getText().toString();
        String pass1 = new_pass1.getText().toString();

        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "密码输入为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isPasswordValid(pass)){
            Toast.makeText(this, "密码不能低于5位", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!pass.equals(pass1)){
            Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pass.matches("[\\d]+")){
            Toast.makeText(this, "密码中必须包含一个字符", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress(true);
        BmobUser.resetPasswordBySMSCodeObservable(code,pass)
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        new_pass.setText(throwable.getMessage());
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        Toast.makeText(ForgetPassWordActivity.this, "密码重置成功", Toast.LENGTH_SHORT).show();
                    }
                });
        /*BmobUser.resetPasswordBySMSCode(code, pass, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null) {
                    Toast.makeText(ForgetPassWordActivity.this, "密码重置成功", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.in_1, R.anim.out_1);
                }else{
                    showProgress(false);
                    new_pass.setText(e.getMessage());
                    Toast.makeText(ForgetPassWordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    //密码是否太短
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /**
     * 显示进度UI并隐藏登录表单。
     */

    private void showProgress(final boolean show) {

        main_interface.setVisibility(show ? View.GONE : View.VISIBLE);

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1,R.anim.out_1);
    }
}
