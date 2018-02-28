package com.xia.adgis.Utils;

import android.content.Context;
import android.graphics.Bitmap;
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

public class ViewHolder {

    private SparseArray<View> mViews;
    private Context mContext;
    private View mConvertView;
    private int mPosition;
    /**
     * init holder
     */
    public ViewHolder(Context context, int layoutId, ViewGroup parent, int position) {
        mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mViews = new SparseArray<>();
        mPosition = position;
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
            ViewHolder holder = (ViewHolder)convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }

    public View getConvertView(){
        return mConvertView;
    }

    /**
     * get view
     */
    public <T extends View> T getView(int viewId){
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
        ImageView imageView = getView(viewId);

        return imageView;
    }
}
