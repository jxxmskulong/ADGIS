package com.xia.adgis.Main.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xia.adgis.R;

public class ForgetPassWordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1,R.anim.out_1);
    }
}
