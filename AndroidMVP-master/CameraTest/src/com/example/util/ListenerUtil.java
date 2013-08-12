package com.example.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.example.cameratest.EditActivity;

public class ListenerUtil {

    public static class SpinnerSelectedListener implements OnItemSelectedListener {
        Cursor datasource;

        public SpinnerSelectedListener(Cursor datasource) {
            this.datasource = datasource;
        }

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Log.i("sjl", "选中的事件..");
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            Log.i("sjl", "未选的事件..");
        }

    }

    public static class ListViewLongClickListener implements OnItemLongClickListener {

        Cursor datasource;
        Context context;
        DataBaseHelper databasehelper;

        public ListViewLongClickListener(Cursor datasource, Context context, DataBaseHelper databasehelper) {
            this.datasource = datasource;
            this.context = context;
            this.databasehelper = databasehelper;
            Log.i("sjl", "datasource的大小：" + datasource.getCount());
        }

        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       final int position, long id) {
            Log.i("sjl", "position is " + position);
            String name = "";
            if (datasource.moveToPosition(position)) {
                name = datasource.getString(datasource.getColumnIndex("name"));
            }
            AlertDialog.Builder builder = new Builder(context);
            builder.setTitle("提示").setMessage("确定删除 " + name + " 吗?").setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (datasource.moveToPosition(position)) {
                        String _id = datasource.getString(datasource.getColumnIndex("_id"));
                        Log.i("sjl", "即将删除是_id:" + _id);
                        databasehelper.deleteCardById(_id);
//						刷新UI数据
                        ((EditActivity) context).initListData();
                        Toast.makeText(context, "已经删除", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            }).show();
            return false;
        }
    }


}
