package com.xia.adgis.Main.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.swipeback.ISwipeBackActivity;
import com.example.swipeback.SwipeBackActivityImpl;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xia.adgis.DragPhotoActivity;
import com.xia.adgis.Main.Tool.StatusBarUtil;
import com.xia.adgis.R;
import com.xia.adgis.Register.Bean.User;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserCentreActivity extends SwipeBackActivityImpl {

    private int mOffset = 0;
    private int mScrollY = 0;
    User user;
    //刷新框架
    public static RefreshLayout refreshLayout;
    @BindView(R.id.toolbar_centre)
    Toolbar toolbar;
    //背景图片
    @BindView(R.id.parallax)
    View parallax;
    //toolbar上的标题布局
    @BindView(R.id.buttonBarLayout)
    View buttonBar;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    //头像
    @BindView(R.id.avatar)
    CircleImageView avatar;
    //用户相关
    @BindView(R.id.centre_nickname)
    TextView userNickname;
    @BindView(R.id.centre_moto)
    TextView userMoto;
    @BindView(R.id.centre_username)
    TextView userName;
    @BindView(R.id.centre_mail)
    TextView userMail;
    @BindView(R.id.centre_phone)
    TextView userPhone;
    @BindView(R.id.centre_sex)
    TextView userSex;
    @BindView(R.id.centre_birthday)
    TextView userBrithday;
    @BindView(R.id.centre_area)
    TextView userArea;
    @BindView(R.id.title)
    TextView Title;
    //图片查看相关
    ArrayList<String> path = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_centre);
        ButterKnife.bind(this);
        //获取本地账户
        user = BmobUser.getCurrentUser(User.class);
        //状态栏透明和间距处理
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.autoRefresh();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshLayout) {
                BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            Gson gson = new Gson();
                            user = gson.fromJson(s,User.class);
                            userNickname.setText(user.getNickName());
                            userMoto.setText(user.getMotto());
                            userName.setText(user.getUsername());
                            userMail.setText(user.getEmail());
                            userPhone.setText(user.getMobilePhoneNumber());
                            userSex.setText(user.getSex());
                            userBrithday.setText(user.getBirthday());
                            userArea.setText(user.getAddress());
                            Title.setText(user.getNickName());
                            Glide.with(UserCentreActivity.this).
                                    load(user.getUserIcon()).
                                    into(new GlideDrawableImageViewTarget(avatar){

                                        @Override
                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                            super.onResourceReady(resource, animation);
                                            refreshLayout.finishRefresh();
                                        }
                                    });
                        }
                    }
                });

                    }
        });
        //上下滑动效果
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                parallax.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }

            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                parallax.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private int lastScrollY = 0;
            private int h = DensityUtil.dp2px(200);
            private int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)&0x00ffffff;
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBar.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                    parallax.setTranslationY(mOffset - mScrollY);
                }
                lastScrollY = scrollY;
            }
        });
        buttonBar.setAlpha(0);
        toolbar.setBackgroundColor(0);
        initUserData();
        //查看图片
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                path.clear();
                Intent intent = new Intent(UserCentreActivity.this, DragPhotoActivity.class);
                int location[] = new int[2];
                avatar.getLocationOnScreen(location);
                path.add(user.getUserIcon());
                intent.putExtra("index",0);
                intent.putExtra("path",(Serializable)path);
                intent.putExtra("left", location[0]);
                intent.putExtra("top", location[1]);
                intent.putExtra("height", avatar.getHeight());
                intent.putExtra("width", avatar.getWidth());
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }

    private void initUserData(){
        //头像加载
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("加载中");
        Glide.with(this).
                load(user.getUserIcon()).
                into(avatar);
        userNickname.setText(user.getNickName());
        userMoto.setText(user.getMotto());
        userName.setText(user.getUsername());
        userMail.setText(user.getEmail());
        userPhone.setText(user.getMobilePhoneNumber());
        userSex.setText(user.getSex());
        userBrithday.setText(user.getBirthday());
        userArea.setText(user.getAddress());
        Title.setText(user.getNickName());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_centre,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.user_edit:
                Intent intent = new Intent(UserCentreActivity.this,EditUserActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setSwipeBackEnable(false);
    }

}
