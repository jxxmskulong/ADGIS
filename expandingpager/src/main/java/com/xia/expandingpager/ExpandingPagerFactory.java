package com.xia.expandingpager;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import com.xia.expandingpager.fragment.ExpandingFragment;
/**
 * Created by xiati on 2018/2/4.
 */

public class ExpandingPagerFactory {

    public static ExpandingFragment getCurrentFragment(ViewPager viewPager){
        if (viewPager.getAdapter() instanceof ExpandingViewPagerAdapter) {
            ExpandingViewPagerAdapter adapter = (ExpandingViewPagerAdapter) viewPager.getAdapter();
            Fragment fragment = adapter.getCurrentFragment();
            if (fragment instanceof ExpandingFragment) {
                return (ExpandingFragment)fragment;
            }
        }
        return null;
    }

    public static void setupViewPager(final ViewPager viewPager) {
        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
        layoutParams.width = ((Activity) viewPager.getContext()).getWindowManager().getDefaultDisplay().getWidth() / 9 * 7;
        layoutParams.height = ((Activity) viewPager.getContext()).getWindowManager().getDefaultDisplay().getHeight() / 7 * 5;

        viewPager.setOffscreenPageLimit(2);

        if (viewPager.getParent() instanceof ViewGroup) {
            ViewGroup viewParent = ((ViewGroup) viewPager.getParent());
            viewParent.setClipChildren(false);
            viewPager.setClipChildren(false);
        }

        viewPager.setPageTransformer(true, new ExpandingViewPagerTransformer());

    }

    public static boolean onBackPressed(ViewPager viewPager) {
        ExpandingFragment expandingFragment = getCurrentFragment(viewPager);
        if(expandingFragment != null && expandingFragment.isOpenend()){
            expandingFragment.close();
            return true;
        }
        return false;
    }
}