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
import com.umeng.analytics.MobclickAgent;

public class CoverCardActivity extends Activity {

    NavigationBar nb;
    ListView lv;
    ListView catLv;
    Cursor datasource4card;
    Cursor datasource4cato;
    //	预被替换的 位置的 相关属性   在其页面中的相对位置position  其parent
    int replacePosition;
    String parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        Intent intent = getIntent();
        replacePosition = intent.getIntExtra("position", 0);
        Log.i("lxl", "替换界面  得到的replacePosition:" + replacePosition);
        parent = intent.getStringExtra("parent");
        Log.i("lxl", "替换界面  得到的parent id is:" + parent);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void init() {
        setupViews();
        initNavigationBar();
    }

    public void initNavigationBar() {
        nb.setTvTitle("替换");
        nb.setBtnRightVisble(false);
        nb.setBtnLeftBacground(R.drawable.ic_back);
        nb.setBtnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    DataBaseHelper myDbHelper;

    public void setupViews() {
        nb = (NavigationBar) findViewById(R.id.nb_edit);
        lv = (ListView) findViewById(R.id.listView);
        catLv = (ListView) findViewById(R.id.listView2);
        myDbHelper = DataBaseHelper.getDataBaseHelper(CoverCardActivity.this);
        datasource4card = myDbHelper.getDataSource(Constants.TYPE_CARD);
        SimpleCursorAdapter adapter4card = new SimpleCursorAdapter(CoverCardActivity.this, R.layout.listitem, datasource4card, new String[]{"name"}, new int[]{R.id.listitem});
        lv.setAdapter(adapter4card);

        datasource4cato = myDbHelper.getDataSource(Constants.TYPE_CATEGORY);
        SimpleCursorAdapter adapter4cato = new SimpleCursorAdapter(CoverCardActivity.this, R.layout.listitem, datasource4cato, new String[]{"name"}, new int[]{R.id.listitem});
        catLv.setAdapter(adapter4cato);

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int location, long id) {
                replayBack(datasource4card, location, 3);
            }
        });

        catLv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int location, long id) {
                replayBack(datasource4cato, location, 3);
            }
        });
    }

    public void replayBack(Cursor datasource, int location, int resultCode) {
        Cursor c = datasource;
        if (c.moveToPosition(location)) {
            String _id = c.getString(c.getColumnIndex("_id"));
            String cardname = c.getString(c.getColumnIndex("name"));
            String type = c.getString(c.getColumnIndex("type"));
            String image = c.getString(c.getColumnIndex("image"));
            String audio = c.getString(c.getColumnIndex("audio"));
            Intent data = new Intent();
//			需要在这将image的id转换为路径信息
            data.putExtra("image", image);
            data.putExtra("type", type);
            data.putExtra("name", cardname);
            data.putExtra("audio", audio);
            data.putExtra("_id", _id);
            data.putExtra("position", replacePosition);
            myDbHelper.insertIntoCard_tree(_id, parent, replacePosition);
//			根据前一个页面传递过来的   position parent 创建纪录
            setResult(resultCode, data);
            Log.i("lxl", "正在更新数据库 返回2*8页面");
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit, menu);
        return true;
    }

}
