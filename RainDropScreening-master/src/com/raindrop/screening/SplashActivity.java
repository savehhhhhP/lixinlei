package com.raindrop.screening;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.raindrop.application.MyApplication;
import com.raindrop.customview.NavigationBar;
import com.raindrop.util.Contants;

public class SplashActivity extends Activity {

    NavigationBar myNavigationbar;
    ListView questionTypeLV;
    MyApplication myapp;
//	SystemExitBroadcastReceiver exitReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        init();
    }

    public void init() {
//		exitReceiver=new SystemExitBroadcastReceiver();
        myapp = (MyApplication) getApplication();
        myapp.addActivity(SplashActivity.this);
        initNavigationbar();
        initQuestionTypeListview();
    }

    //设置Navigationbar使其为隐藏按钮模式
    public void initNavigationbar() {
        myNavigationbar = (NavigationBar) findViewById(R.id.helpNb);
        myNavigationbar.setBtnRightVisble(false);
        myNavigationbar.setBtnLeftVisble(false);
        myNavigationbar.setTvTitle("请选择题库");
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Contants.EXIT);
//		this.registerReceiver(exitReceiver, filter);
    }

    String age = "未填";
    String relation = "未填";
    String gender = "未填";
    int questionType = 1;      //传值到mainActivity以获取对应的题库

    public void initQuestionTypeListview() {
//		题库A(适合0到16个月儿童)
//		题库B(适合16到30个月儿童)
//		题库C(适合2岁以上儿童)
        final String[] adapterSource = new String[]{"孩子的年龄(必填)", "您和孩子的关系", "孩子的性别", "开始答题"};
        final int[] dialogItemsID = {R.array.age, R.array.relation, R.array.gender};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, adapterSource);   //android自带的android.R.layout.simple_list_item_1
        questionTypeLV = (ListView) findViewById(R.id.questionTypeLV);
        questionTypeLV.setAdapter(adapter);
        questionTypeLV.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                    long arg3) {
                //此时用户点击开始答题，如果没有选择年龄，则需要对用户进行提示      --点击了开始答题
                if (arg2 == 3) {

                    if ("未填".equals(age)) {
                        AlertDialog.Builder builder = new Builder(SplashActivity.this);
                        builder.setTitle("提示").setMessage("您需要选择孩子的年龄才能开始答题").setPositiveButton("确定", null).show();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("questionType", questionType);
                        String[] ages = getResources().getStringArray(R.array.age);
                        myapp.setAge(age);
                        myapp.setGender(gender);
                        myapp.setRelation(relation);
                        startActivity(intent);
//						SplashActivity.this.finish();
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);//activity的切换效果             淡出     淡入
                    }
                } else {
                    AlertDialog.Builder builder = new Builder(SplashActivity.this);
                    builder.setTitle(adapterSource[arg2]).setSingleChoiceItems(dialogItemsID[arg2], 0, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String answer = getResources().getStringArray(dialogItemsID[arg2])[which];
                            Log.i("sjl", "answer:" + answer);
                            switch (arg2) {
                                case 0:                                //年龄
                                    questionType = which;
                                    age = answer;
                                case 1:                                //关系
                                    relation = answer;
                                case 2:                                //性别
                                    gender = answer;
                                default:
                                    break;
                            }
                            dialog.dismiss();
                        }
                    }).show();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_help, menu);
        return true;
    }
}
