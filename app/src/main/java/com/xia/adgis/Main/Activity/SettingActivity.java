package com.xia.adgis.Main.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xia.adgis.App;
import com.xia.adgis.Main.Tool.StatusBarUtil;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.collapsing_setting)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.setting_imageView)
    ImageView setting_ImageView;
    @BindView(R.id.setting_toolbar)
    Toolbar setting_toolbar;
    @BindView(R.id.clear_CahChe)
    RelativeLayout clearCaChe;
    @BindView(R.id.show_CaChe)
    TextView showCaChe;
    @BindView(R.id.appMessage)
    RelativeLayout appMessage;
    @BindView(R.id.log_out)
    Button logOut;
    //本地用户
    User user;
    //cache文件夹下所有文件的大小(单位：字节)
    private long allCacheFileSize;
    //缓存文件路径
    private File cahcheDir = App.getContext().getCacheDir();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        StatusBarUtil.immersive(this, false);
        StatusBarUtil.setPaddingSmart(this, setting_toolbar);
        user = BmobUser.getCurrentUser(User.class);
        //初始化toolbar
        initToolbar();
        //显示缓存大小
        initCaCheSize();
        //应用信息
        showAppMessage();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobUser.logOut();
                Intent intent = new Intent();
                intent.putExtra("setting","logout");
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("setting","normal");
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(R.anim.in_1,R.anim.out_1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar(){
        setSupportActionBar(setting_toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //以后用来设置用户名
        collapsingToolbarLayout.setTitle("设置");
    }
    private void initCaCheSize(){

        allCacheFileSize(cahcheDir);

        float size = (allCacheFileSize / 1024.0f)/1024.0f;

        String result = String.format("%.2f", size);//java 四舍五入保留小数点后两位

        showCaChe.setText(result + "MB");

        clearCaChe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setMessage("确定要清除所有缓存吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAllCache(cahcheDir);
                        showCaChe.setText("0.00MB");
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

            }
        });
    }

    //获取指定文件夹下的所有文件总和大小【此处计算的是cache文件夹】
    private void allCacheFileSize(File dir){
        if(!dir.isDirectory()){
            Toast.makeText(this, "删除错误", Toast.LENGTH_SHORT).show();;
            return;
        }
        File[] listFiles = dir.listFiles();
        for (int i = 0; i < listFiles.length; i ++){
            File file = listFiles[i];
            //是否是一个文件夹(路径)
            if(file.isDirectory()){
                allCacheFileSize(file);//递归调用
            }else {
                allCacheFileSize = allCacheFileSize + file.length();
            }
        }
    }

    //使用遍历删除所有缓存文件
    private void deleteAllCache(File dir){
        if(!dir.isDirectory()){
            Log.v("TAG", "请传入一个文件夹对象");
            return;
        }
        File[] listFiles = dir.listFiles();
        for (int i=0;i<listFiles.length;i++){
            File file=listFiles[i];
            //是否是一个文件夹(路径)
            if(file.isDirectory()){
                //文件夹
                Log.v("TAG", "文件夹："+file.toString());
                deleteAllCache(file);//递归调用
            }else {
                //文件
                Log.v("TAG", "文件："+file.toString());
                file.delete();
            }
        }
    }

    private void showAppMessage(){
        appMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this,ApplicationMessageActivity.class));
                overridePendingTransition(R.anim.in,R.anim.out);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("setting","normal");
        setResult(RESULT_OK,intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.in_1,R.anim.out_1);
    }
}
