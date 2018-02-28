package com.xia.adgis.Login.Adapter;

import java.util.List;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xia.adgis.Login.DataBase.UserSqliteHelper;
import com.xia.adgis.R;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * Created by xiati on 2018/1/13.
 */

public class UserNameHistoryAdapter extends ArrayAdapter<String> {

    private int resourceId;
    private Context mContext;
    private List<String> list;
    public UserNameHistoryAdapter(Context context,int textViewResourdeId,List<String> objects) {
        super(context,textViewResourdeId,objects);
        resourceId = textViewResourdeId;
        mContext = context;
        list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String temp = getItem(position);

        View view;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("删除用户");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserSqliteHelper helper = new UserSqliteHelper(mContext);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        try {
                            String sql = "delete from history where name = ?";
                            db.execSQL(sql,new String[]{temp});
                            Toast.makeText(mContext,"成功删除",Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(mContext,"删除失败",Toast.LENGTH_SHORT).show();
                        }
                        remove(temp);
                    }
                });
               builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                   }
               });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            });
        return view;
    }

    static class ViewHolder{
        public TextView userName;
        public ImageView userDelete;

    }
}
