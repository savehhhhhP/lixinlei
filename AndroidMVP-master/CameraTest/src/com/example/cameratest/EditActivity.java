package com.example.cameratest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.customview.NavigationBar;
import com.example.util.Constants;
import com.example.util.DataBaseHelper;
import com.example.util.ListenerUtil.ListViewLongClickListener;
import com.umeng.analytics.MobclickAgent;

public class EditActivity extends Activity {

    NavigationBar nb;
    ListView cardsListView;
    ListView categoryListView;
    Cursor datasource4card;
    Cursor datasource4cato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        init();
    }

    public void init() {
        setupViews();
        initNavigationBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListData();
        MobclickAgent.onResume(this);
    }

    public void initNavigationBar() {
        nb.setTvTitle("编辑");
        nb.setBtnLeftBacground(R.drawable.ic_back);
        nb.setBtnRightVisble(false);
        nb.setBtnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    DataBaseHelper myDbHelper;

    public void initListData() {
        datasource4card = myDbHelper.getDataSource(Constants.TYPE_CARD);
        SimpleCursorAdapter adapter4card = new SimpleCursorAdapter(EditActivity.this, R.layout.listitem, datasource4card, new String[]{"name"}, new int[]{R.id.listitem});
        cardsListView.setAdapter(adapter4card);

        datasource4cato = myDbHelper.getDataSource(Constants.TYPE_CATEGORY);
        SimpleCursorAdapter adapter4cato = new SimpleCursorAdapter(EditActivity.this, R.layout.listitem, datasource4cato, new String[]{"name"}, new int[]{R.id.listitem});
        categoryListView.setAdapter(adapter4cato);
    }


    public void setupViews() {
        nb = (NavigationBar) findViewById(R.id.nb_edit);
        cardsListView = (ListView) findViewById(R.id.listView);
        categoryListView = (ListView) findViewById(R.id.listView2);

        myDbHelper = DataBaseHelper.getDataBaseHelper(EditActivity.this);
        initListData();

        cardsListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                startCardDetail(datasource4card, position);
            }
        });

        categoryListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                startCardDetail(datasource4cato, position);
            }
        });

//		begin 增加 删除卡片或目录的功能 2013 07 31
        cardsListView.setOnItemLongClickListener(new ListViewLongClickListener(datasource4card, EditActivity.this, myDbHelper));
        categoryListView.setOnItemLongClickListener(new ListViewLongClickListener(datasource4cato, EditActivity.this, myDbHelper));
//		end
    }

    public void startCardDetail(Cursor datasource, int position) {
        Intent intent = new Intent();
        intent.setClass(EditActivity.this, EditCardActivity.class);
        Cursor c = datasource;
        if (datasource.moveToPosition(position)) {
            String _id = c.getString(c.getColumnIndex("_id"));
            String name = c.getString(c.getColumnIndex("name"));
            String type = c.getString(c.getColumnIndex("type"));
            String image = c.getString(c.getColumnIndex("image"));
            String audio = c.getString(c.getColumnIndex("audio"));
            Log.i("sjl", "即将跳转到卡片编辑页面: name:" + name + "image:" + image + "audio:" + audio + "_id:" + _id);
            intent.putExtra("name", name);
            intent.putExtra("image", image);
            intent.putExtra("audio", audio);
            intent.putExtra("type", type);
            intent.putExtra("_id", _id);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit, menu);
        return true;
    }

}
