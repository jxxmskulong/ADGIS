package com.xia.adgis.Utils.MySearchView.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xia.adgis.R;
import com.xia.adgis.Main.DataBase.HistorySqliteHelpter;
import com.xia.adgis.Utils.MySearchView.SearchView;

import java.util.List;

/**
 *
 * Created by xiati on 2018/1/13.
 */

public class UserNameHistoryAdapter extends ArrayAdapter<String> {

    private int resourceId;
    private Context mContext;
    private List<String> list;
    public UserNameHistoryAdapter(Context context, int textViewResourdeId, List<String> objects) {
        super(context,textViewResourdeId,objects);
        resourceId = textViewResourdeId;
        mContext = context;
        list = objects;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final String temp = getItem(position);

        final View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.userName = (TextView)view.findViewById(R.id.user_history_context);
            viewHolder.userDelete = (ImageView)view.findViewById(R.id.user_history_delete);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.userName.setText(temp);
        viewHolder.userDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(temp);
                notifyDataSetChanged();
                HistorySqliteHelpter helpter = new HistorySqliteHelpter(mContext);
                SQLiteDatabase database = helpter.getWritableDatabase();
                try {
                    String sql = "delete from history where name = ?";
                    database.execSQL(sql,new String[]{temp});
                    Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(mContext,"删除失败",Toast.LENGTH_SHORT).show();
                }
                if(list.isEmpty()){
                    SearchView.llSearchEmpty.setVisibility(View.GONE);
                    SearchView.showOrhide.setImageResource(R.drawable.ic_expand_more);
                    SearchView.isShow = false;
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
