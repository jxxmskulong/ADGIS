package com.xia.adgis.Utils.MySearchView.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xia.adgis.R;
import com.xia.adgis.Utils.MySearchView.SearchView;

import java.util.List;

/**
 *
 * Created by xiati on 2018/1/17.
 */

public class AutoCompleteAdapter extends ArrayAdapter<String> {

    private int resourceId;
    private List<String> list;

    public AutoCompleteAdapter(Context context,int textViewResourdeId,List<String> objects){
        super(context,textViewResourdeId,objects);
        resourceId = textViewResourdeId;
        list = objects;
    }

    @Override
    @NonNull
    public View getView(int position,  View convertView,  @NonNull ViewGroup parent) {
        final String temp = getItem(position);

        final View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.userName = (TextView)view.findViewById(R.id.user_common_context);
            viewHolder.userDelete = (ImageView)view.findViewById(R.id.user_common_delete);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.userName.setText(temp);
        viewHolder.userDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(temp);
                notifyDataSetChanged();
                if(list.isEmpty()){
                    SearchView.llSearchEmpty.setVisibility(View.GONE);
                    SearchView.showOrhide.setImageResource(R.drawable.ic_expand_more);
                }
            }
        });
        return view;
    }

    private class ViewHolder{
        private TextView userName;
        private ImageView userDelete;

    }
}
