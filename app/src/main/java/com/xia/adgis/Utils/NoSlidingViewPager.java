package com.xia.adgis.Utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 *
 * Created by xiati on 2018/1/25.
 */

public class NoSlidingViewPager extends ViewPager {

    public NoSlidingViewPager(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //去掉ViewPager默认的滑动效果， 不消费事件
        return false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //不让拦截事件
        return false;
    }
}
