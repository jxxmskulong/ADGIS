package com.xia.adgis.Main.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.xia.adgis.Main.Activity.ADsDetailActivity;
import com.xia.adgis.Main.Activity.SearchActivity;
import com.xia.adgis.R;
import com.xia.adgis.Main.Bean.SearchItem;
import com.xia.adgis.Utils.ViewHolder;
import com.xia.imagewatch.RCommonUtil;
import com.xia.imagewatch.RolloutBDInfo;
import com.xia.imagewatch.RolloutPreviewActivity;

import java.io.Serializable;
import java.util.List;

public class SearchAdapter extends BaseAdapter{

    private AppCompatActivity mContext;
    private List<SearchItem> mData;
    private int mLayoutId;

    private RolloutBDInfo bdInfo;

    public SearchAdapter(AppCompatActivity context, List<SearchItem> data, int layoutId) {
        mContext = context;
        mData = data;
        mLayoutId = layoutId;
        bdInfo = new RolloutBDInfo();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public SearchItem getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.getHolder(mContext,convertView,mLayoutId,parent,position);
        convert(holder,position);
        return holder.getConvertView();
    }

    /**
     * get holder convert
     */
    public void convert(ViewHolder holder, final int position) {
        holder.setImageResource(R.id.item_search_icon,mData.get(position).url)
                .setText(R.id.item_search_title,mData.get(position).title)
                .setText(R.id.item_search_content,"待完善");
        final ImageView image = holder.getIcon(R.id.item_search_icon);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = SearchActivity.lvResults.getChildAt(0);
                //相对于其父视图的顶部位置
                int top = v.getTop();
                //返回适配器的数据集中显示在屏幕上的第一个项目的位置。
                int firstVisiblePosition = SearchActivity.lvResults.getFirstVisiblePosition();
                bdInfo.x = image.getLeft();
                bdInfo.y = SearchActivity.searchView.getHeight() + RCommonUtil.dip2px(mContext, 8) + (position - firstVisiblePosition) * RCommonUtil.dip2px(mContext, 82f) + top + SearchActivity.lvResults.getTop();

                //关于imageView想要有多宽
                bdInfo.width = image.getLayoutParams().width;
                bdInfo.height = image.getLayoutParams().height;

                Intent intent = new Intent(mContext, RolloutPreviewActivity.class);
                intent.putExtra("data", (Serializable) mData);
                intent.putExtra("bdinfo", bdInfo);
                intent.putExtra("index", position);
                intent.putExtra("type", 1);//list传1
                mContext.startActivity(intent);
            }
        });

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(mContext, ADsDetailActivity.class);
                intent.putExtra("data",mData.get(position).url);
                ActivityCompat.startActivity(mContext,intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                mContext,
                                new Pair<>(view, "detail_image"))
                                .toBundle());
                return false;
            }
        });
    }
}
