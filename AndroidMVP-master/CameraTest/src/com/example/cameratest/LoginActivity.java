package com.example.cameratest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.customview.NavigationBar;
import com.example.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Appchina
 * Date: 13-8-12
 * Time: 下午5:52
 * To change this template use File | Settings | File Templates.
 */
public class LoginActivity extends Activity {

    NavigationBar nbar;                                            //导航条
    Button btnLogin;
    ExpandableListView exlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         //btnLogin
        initUI();
        initNavigationBar();
        initExpandableListView();
    }
    List<String> group;           //组列表
    List<List<String>> child;     //子列表

    /**
     * 初始化组、子列表数据
     */
    private void initializeData(){
        group = new ArrayList<String>();
        child = new ArrayList<List<String>>();

        addInfo("Andy",new String[]{"male","138123***","GuangZhou"});

    }
    /**
     * 模拟给组、子列表添加数据
     * @param g-group
     * @param c-child
     */
    private void addInfo(String g,String[] c){
        group.add(g);
        List<String> childitem = new ArrayList<String>();
        for(int i=0;i<c.length;i++){
            childitem.add(c[i]);
        }
        child.add(childitem);
    }


    private void initExpandableListView() {
        initializeData();
        final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            //-----------------Child----------------//
            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return child.get(groupPosition).get(childPosition);
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return child.get(groupPosition).size();
            }

            @Override
            public View getChildView(int groupPosition, int childPosition,
                                     boolean isLastChild, View convertView, ViewGroup parent) {
                String string = child.get(groupPosition).get(childPosition);
                return getGenericView(string);
            }

            //----------------Group----------------//
            @Override
            public Object getGroup(int groupPosition) {
                return group.get(groupPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public int getGroupCount() {
                return group.size();
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded,
                                     View convertView, ViewGroup parent) {
                String string = group.get(groupPosition);
                return getGenericView(string);
            }

            //创建组/子视图
            public TextView getGenericView(String s) {
                // Layout parameters for the ExpandableListView
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, 40);

                TextView text = new TextView(LoginActivity.this);
                text.setLayoutParams(lp);
                // Center the text vertically
                text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                // Set the text starting position
                text.setPadding(36, 0, 0, 0);

                text.setText(s);
                return text;
            }
            @Override
            public boolean hasStableIds() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return true;
            }
        };

        exlist.setAdapter(adapter);
        exlist.setCacheColorHint(0);  //设置拖动列表的时候防止出现黑色背景
    }


    Intent intent;
    RadioButton newUser;
    RadioButton oldUser;
    LinearLayout ll;
    private void initUI() {
        btnLogin = (Button)findViewById(R.id.btnLogin);
        nbar = (NavigationBar)findViewById(R.id.navigationBar_Login);
        exlist =(ExpandableListView)findViewById(R.id.expandableListView);
        ll = (LinearLayout)findViewById(R.id.lLayout);
        /*尝试添加到Group  失败
        Log.i("group","group before");
        RadioGroup rGroup;
        rGroup = new RadioGroup(this);
        newUser =(RadioButton)findViewById(R.id.rbNewUser);
        oldUser =(RadioButton)findViewById(R.id.rbOldUser);
        rGroup.addView(newUser);
        rGroup.addView(oldUser);
        ll.addView(rGroup);
        Log.i("group","group after");
        */
        intent = new Intent();
        intent.setClass(LoginActivity.this, FirstActivity.class);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent,0);
            }
        });
    }
    public void initNavigationBar() {
        nbar.setTvTitle("小雨滴");
        nbar.setBtnLeftVisble(false);
        nbar.setBtnRightVisble(false);
    }
}
