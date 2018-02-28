package com.xia.adgis.Main.Tool;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomdialog.BaseBottomDialog;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by xiati on 2018/2/23.
 */

public class BindPhoneDialog extends BaseBottomDialog {

    //各类控件
    TextView Cancel;
    TextView Confirm;
    EditText Phone;
    EditText Code;
    TextView Send;
    ProgressDialog progress;
    @Override
    public int getLayoutRes() {
        return R.layout.bind_phone_bottom_dialod;
    }

    @Override
    public void bindView(View v) {
        Cancel = (TextView) v.findViewById(R.id.cancel);
        Confirm = (TextView) v.findViewById(R.id.confirm);
        Phone = (EditText) v.findViewById(R.id.change_number);
        Code = (EditText) v.findViewById(R.id.change_code);
        Send = (TextView) v.findViewById(R.id.change_send);
        progress = new ProgressDialog(getActivity());
        progress.setMessage("正在验证短信验证码");
        progress.setCanceledOnTouchOutside(false);
        //取消
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSMSCode();
            }
        });

        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOrBind();
            }
        });
    }

    //发送短信验证码
    private void requestSMSCode() {
        String phone = Phone.getText().toString();
        if(!TextUtils.isEmpty(phone)){
            final MyCountTimer timer = new MyCountTimer(30000,1000);
            timer.start();
            BmobSMS.requestSMSCode(phone, "默认模板", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null){
                        //Toast.makeText(getActivity(), "验证码发送成功", Toast.LENGTH_SHORT).show();
                        Send.setText("发送成功");
                        Send.setEnabled(false);
                    }else{
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        timer.cancel();
                        Send.setEnabled(true);
                    }
                }
            });
        }else{
            Toast.makeText(getActivity(), "电话输入为空", Toast.LENGTH_SHORT).show();
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
            Send.setText(temp);
            Send.setEnabled(false);
        }
        @Override
        public void onFinish() {
            Send.setText("重新发送验证码");
            Send.setEnabled(true);
        }
    }
    //验证验证码

    private void verifyOrBind(){
        final String phone = Phone.getText().toString();
        String code = Code.getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(getActivity(), "手机号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(code)){
            Toast.makeText(getActivity(), "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.show();
        BmobSMS.verifySmsCode(phone, code, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Toast.makeText(getActivity(), "号码验证成功", Toast.LENGTH_SHORT).show();
                    bindMobilePhone(phone);
                }else {
                    Toast.makeText(getActivity(), "验证失败", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void bindMobilePhone(final String phone){
        User user = new User();
        user.setMobilePhoneNumber(phone);
        user.setMobilePhoneNumberVerified(true);
        User cur = BmobUser.getCurrentUser(User.class);
        user.update(cur.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Toast.makeText(getActivity(), "手机号更换成功", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    dismiss();
                }else{
                    Toast.makeText(getActivity(), "手机号更换失败", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
            }
        });
    }

    @Override
    public float getDimAmount() {
        return 0.5f;
    }

    @Override
    public boolean getCancelOutside() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
