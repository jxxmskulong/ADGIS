package com.xia.adgis.Utils.MySearchView;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by yetwish on 2015-05-11
 */
@SuppressWarnings("unchecked")
public class ViewHolder {

    private SparseArray<View> mViews;
    private Context mContext;
    private View mConvertView;
    /**
     * init holder
     */
    private ViewHolder(Context context, int layoutId, ViewGroup parent, int position) {
        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mViews = new SparseArray<>();
        mConvertView.setTag(this);
        mContext = context;
    }

    /**
     *  获取viewHolder
     */
    public static ViewHolder getHolder(Context context, View convertView,
                                       int layoutId, ViewGroup parent, int position) {
        if(convertView == null){
            return new ViewHolder(context,layoutId,parent,position);
        }else{
            return (ViewHolder)convertView.getTag();
        }
    }

    public View getConvertView(){
        return mConvertView;
    }

    /**
     * get view
     */
    private  <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T)view;
    }

    /**
     * set text
     */
    public ViewHolder setText(int viewId, String text){
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     *  set image res
     */
    public ViewHolder setImageResource(int viewId,String resId){
        ImageView iv = getView(viewId);
        Glide.with(mContext).load(resId).thumbnail(0.1f).into(iv);
        return this;
    }

    public ImageView getIcon(int viewId){

        return (ImageView)getView(viewId);
    }
}
