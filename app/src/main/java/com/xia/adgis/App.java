package com.xia.adgis;

import android.app.Application;
import android.content.Context;

import com.example.swipeback.ActivityStack;

import cn.bmob.v3.Bmob;

/**
 *
 * Created by xiati on 2018/2/10.
 */

public class App extends Application {

    public static App instance;

    //这是库中提供的activity栈,放到Application中,不会被GC
    public ActivityStack stack;

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,"b50f56da99641a8ffab38ca5c15188f2");
        instance = this;
        stack = new ActivityStack();
        this.registerActivityLifecycleCallbacks(stack);
        context = getApplicationContext();
    }

    public static App getInstance() {
        return instance;
    }

    public ActivityStack getStack() {
        return stack;
    }

    public static Context getContext() {
        return context;
    }
}
