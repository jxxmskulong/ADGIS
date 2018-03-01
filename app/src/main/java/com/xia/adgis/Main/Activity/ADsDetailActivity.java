package com.xia.adgis.Main.Activity;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.myphotoview.Info;
import com.example.myphotoview.MyPhotoView;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.xia.adgis.Main.Fragment.ADsCompanyFragment;
import com.xia.adgis.Main.Fragment.ADsMaintainFragment;
import com.xia.adgis.Main.Fragment.ADsMessageFragment;
import com.xia.adgis.Main.Fragment.ADsPhysicalFragment;
import com.xia.adgis.Main.Tool.StatusBarUtil;
import com.xia.adgis.Utils.DragScrollDetailsLayout;
import com.xia.adgis.R;
import com.xia.imagewatch.GlideProgress.ProgressInterceptor;
import com.xia.imagewatch.GlideProgress.ProgressListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

public class ADsDetailActivity extends AppCompatActivity {

    @BindView(R.id.detailDrag)
    DragScrollDetailsLayout mDragScrollDetailsLayout;
    @BindView(R.id.imageDetail)
    ImageView imageDetail;
    @BindView(R.id.detailViewPager)
    ViewPager viewPager;
    @BindView(R.id.flag_tips)
    TextView mTextView;
    @BindView(R.id.adsDetail)
    LinearLayout adsDetail;
    @BindView(R.id.detailTabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.detailToolBar)
    Toolbar toolbar;
    @BindView(R.id.detailButtonBarLayout)
    View buttonBar;
    @BindView(R.id.detailTitle)
    TextView title;
    private int mScrollY = 0;

    //碎片
    ADsPhysicalFragment aDsPhysicalFragment;
    ADsMessageFragment aDsMessageFragment;
    ADsCompanyFragment aDsCompanyFragment;
    ADsMaintainFragment aDsMaintainFragment;
    //定义要装fragment的列表
    private ArrayList<Fragment> list_fragment=new ArrayList<Fragment>();
    //tablayout的标题
    private ArrayList<String> list_title=new ArrayList<>();
    //viewpager与tablayout共用适配器
    TabAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_detail);
        ButterKnife.bind(this);
        title.setText("广告牌详细信息");
        //toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //沉浸模式
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this,toolbar);
        StatusBarUtil.setPaddingSmart(this,adsDetail);
        //开始不透明
        buttonBar.setAlpha(0);
        toolbar.setBackgroundColor(0);
        //碎片初始化
        aDsPhysicalFragment = new ADsPhysicalFragment();
        aDsMessageFragment = new ADsMessageFragment();
        aDsCompanyFragment = new ADsCompanyFragment();
        aDsMaintainFragment = new ADsMaintainFragment();
        //添加碎片
        list_fragment.add(aDsMessageFragment);
        list_fragment.add(aDsPhysicalFragment);
        list_fragment.add(aDsCompanyFragment);
        list_fragment.add(aDsMaintainFragment);
        //添加标题
        list_title.add("文字内容");
        list_title.add("物理信息");
        list_title.add("公司信息");
        list_title.add("维护信息");
        //tablayout与viewpager逻辑
        adapter = new TabAdapter(getSupportFragmentManager());
        //TabLayout与ViewPager关联
        //ViewPager滑动关联TabLayout
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //mTabLayout.setScrollPosition(position, 0, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);
        mDragScrollDetailsLayout.setOnSlideDetailsListener(new DragScrollDetailsLayout.OnSlideFinishListener() {
            @Override
            public void onStatueChanged(DragScrollDetailsLayout.CurrentTargetIndex status) {
                if(status == DragScrollDetailsLayout.CurrentTargetIndex.UPSTAIRS){
                    mTextView.setText("上拉展现更多");
                }else{
                    mTextView.setText("回到顶部");
                }
            }
        });

        mDragScrollDetailsLayout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            private int lastScrollY = 0;
            private int h = DensityUtil.dp2px(300);
            private int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)&0x00ffffff;
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBar.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                }
                lastScrollY = scrollY;
            }
        });
        //加载
        final String imageID = getIntent().getStringExtra("data");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("加载中");
        ProgressInterceptor.addListener(imageID, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
            }
        });
        Glide.with(this).
                load(imageID)
                .into(new GlideDrawableImageViewTarget(imageDetail){
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        progressDialog.show();
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        progressDialog.dismiss();
                        ProgressInterceptor.removeListener(imageID);
                    }
                });

        imageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private class TabAdapter extends FragmentPagerAdapter {
        private TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }

        @Override
        public int getCount() {
            return list_fragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list_title.get(position);
        }
    }
}
