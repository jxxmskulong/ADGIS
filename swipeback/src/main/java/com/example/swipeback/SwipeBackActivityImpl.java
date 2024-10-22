package com.example.swipeback;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class SwipeBackActivityImpl extends AppCompatActivity implements ISwipeBackActivity {

    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isTransparent()) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout = new SwipeBackLayout(this);
        mSwipeBackLayout.setLayoutParams(params);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackLayout.attachToActivity(this);
    }

    @Override
    public View findViewById(int id) {
        View view = super.findViewById(id);
        if (view == null && mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return view;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    @Override
    public ISwipeBackActivity getPreActivity() {
        return (ISwipeBackActivity) (ActivityStack.getInstance().getBackActivity());
    }

    @Override
    public boolean swipeBackPriority() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 1){
            return false;
        }
        return true;
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setEnableGesture(enable);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }
}
