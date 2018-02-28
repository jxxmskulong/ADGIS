package com.xia.adgis.Register.Fragment.Basic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment {

	/** 上下文 */
	protected Context mContext 	= null;
	/** 依附的Activity */
	protected Activity mActivity= null;

	Unbinder unbind;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext  = activity;
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		if (getContentViewId() != 0) {
			return inflater.inflate(getContentViewId(), null);
		} else {
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		unbind = ButterKnife.bind(this, view);
		init();
		initData();
		initEvents();
	}




	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbind.unbind();
	}

	/** 初始化方法 */
	public void init() {}
	/** 设置布局 */
	protected abstract int getContentViewId();

	/** 初始化数据 */
	protected void initData() {}
	/** 初始化事件的抽象方法 */
	public abstract void initEvents();

}