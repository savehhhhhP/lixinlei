package com.raindrop.screening;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raindrop.application.MyApplication;
import com.raindrop.customview.NavigationBar;
import com.raindrop.util.Contants;
import com.raindrop.util.HttpUtil;
import com.umeng.fb.FeedbackAgent;

public class MainActivity extends Activity implements OnClickListener {

    public final static String TAG = "MainActivity";
    MyApplication myapp;
    ImageButton imageBtnYes;
    ImageButton imageBtnNo;
    LinearLayout displayLL;
    LinearLayout displayPartLL;
    NavigationBar navigationBar;
    ProgressBar progressbar;
    ImageView questionIv;
    TextView questionTv;
    Map<Integer, String> questions;
    Map<String, Integer> answers;
    String[] questionsArray;
    JSONObject json;
    int questionLength;
    int questionType;
    int currentNum;
    FeedbackAgent agent;

    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String jsonstring = msg.obj.toString();
//			      questionTv.setText(jsonstring);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //
    public void initMyApplication() {
        myapp = (MyApplication) getApplication();
        myapp.addActivity(MainActivity.this);
        myapp.setTerminal("android");
        myapp.setUserid(getUuid());
    }

    private void init() {
        initMyApplication();
        initUI();
        initQuestions();
    }

    private void initUI() {

        imageBtnYes = (ImageButton) findViewById(R.id.imageBtnYes);
        imageBtnNo = (ImageButton) findViewById(R.id.imageBtnNo);
        displayLL = (LinearLayout) findViewById(R.id.displayLL);
        questionTv = (TextView) findViewById(R.id.questionTv);
        progressbar = (ProgressBar) findViewById(R.id.progressBar1);
        displayPartLL = (LinearLayout) findViewById(R.id.displayPartLL);
        questionIv = (ImageView) findViewById(R.id.questionIv);
        answers = new HashMap<String, Integer>();
        agent = new FeedbackAgent(MainActivity.this);
        agent.sync();

        setNavigationBar();
        setImageButtonListener();
    }

    /**
     * 生成用户ID
     *
     * @return
     */
    public String getUuid() {
//   	 判断是否是第一次使用  
        SharedPreferences userInfo = MainActivity.this.getSharedPreferences("userInfo", 0);
        String userid = userInfo.getString("userid", "");
        if ("".equals(userid)) {
//	   		第一次使用
            TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            String tmDevice = "" + tm.getDeviceId();
            String tmSerial = "" + tm.getSimSerialNumber();
            String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            userid = deviceUuid.toString();
            Editor editor = userInfo.edit();
            editor.putString("userid", userid).commit();
        }
        return userid;
    }

    private void setImageButtonListener() {
        imageBtnYes.setOnClickListener(this);
        imageBtnNo.setOnClickListener(this);
    }

    private void setNavigationBar() {

        navigationBar = (NavigationBar) findViewById(R.id.navigationBar1);
        navigationBar.setTvTitle(getString(R.string.main_tittle));
        navigationBar.setBtnLeftVisble(false);
        navigationBar.setBtnRightText(getString(R.string.right_btn_text));
        navigationBar.setBtnRightClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new Builder(MainActivity.this);
                builder.setTitle(getString(R.string.right_btn_msg_tittle)).setItems(R.array.operations, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
//								重新开始答题
                                currentNum = 0;
                                setQuestionContent();
                                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                Toast.makeText(MainActivity.this, getString(R.string.main_msg), Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                agent.startFeedbackActivity();
                                break;
                            case 2:
//								退出应用  此处已经改为由myapp中维护的一个activity链表 统一管理  by sjl 2013 07 18 
//								广播通知所有Activity 关闭所有活动Activity
//								Intent exitIntent=new  Intent();
//								exitIntent.setAction(Contants.EXIT);
//								MainActivity.this.sendBroadcast(exitIntent);
                                myapp.closeActivitys();
//								finish();   
//								overridePendingTransition(R.anim.fadein, R.anim.fadeout); 

//								不能用下面这句 销毁资源  否则 后台线程关闭  导致不能广播到监听者 by sjl 20130627
//								System.exit(0);
                                break;
                        }
                    }
                }).setPositiveButton(getString(R.string.right_btn_msg_cancel), null).show();

            }
        });
    }

    /**
     * 初始化问题，根据条件选择的情况可以载入对应的题库
     */
    private void initQuestions() {
        Intent intent = this.getIntent();
        questionType = intent.getIntExtra("questionType", 1);    //从上一个activity 取得题库的类型
        Resources res = this.getResources();

        switch (questionType) {
            case 0:
                questionsArray = res.getStringArray(R.array.questionType1);
                break;
            case 1:
                questionsArray = res.getStringArray(R.array.questionType2);
                break;
            case 2:
                questionsArray = res.getStringArray(R.array.questionType3);
                break;
            default:
                break;
        }
        ;
        questionLength = questionsArray.length;
        currentNum = 0;
        progressbar.setMax(questionLength);
        progressbar.setProgress(1);
        setQuestionContent();
    }

    private void setQuestionContent() {
        questionTv.setText(Html.fromHtml("<b>" + questionsArray[currentNum] + "</b>"));
//		String filed="question"+currentNum;
//		Class<?> c;
//		Field f;
//		try {
//			c=Class.forName("com.raindrop.screening.R"+"$"+"drawable");
//			f = c.getField(filed);
//			questionIv.setImageResource(Integer.valueOf(f.get(c.newInstance()).toString()));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        int key = currentNum + 1;
        if (v.getId() == R.id.imageBtnYes) {
            answers.put(key + "", 1);
        } else if (v.getId() == R.id.imageBtnNo) {
            answers.put(key + "", 0);
        }
        currentNum++;
        //使用     currentNum  的改变可以控制载入不同的题目，同时控制progressBar的改变，当长度到达最大题目数时给出结果
        if (currentNum < questionLength) {
            setQuestionContent();
            progressbar.setProgress(currentNum + 1);
        } else {
            commitAnswers();
        }
        startAlphaAnimation(displayPartLL);
    }

    public void commitAnswers() {
        json = formatRequestParams();
        //开新线程用于计算结果
        Thread mythread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> requestParams = new HashMap<String, String>();
                    requestParams.put("answer", json.toString());
                    String jsonstring = HttpUtil.postRequest("datacommit", requestParams);
                    Message me = new Message();
                    me.obj = jsonstring;
                    myhandler.sendMessage(me);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//		向服务器提交数据
        mythread.start();
        handleResult();
    }

    public JSONObject formatRequestParams() {
        JSONObject jsonparams = new JSONObject();
        try {
            for (int i = 0; i < answers.size(); i++) {
                jsonparams.put("question" + (i + 1), answers.get((i + 1) + ""));
            }
            jsonparams.put("userid", myapp.getUserid());
            jsonparams.put("terminal", myapp.getTerminal());
            jsonparams.put("age", myapp.getAge());
            jsonparams.put("gender", myapp.getGender());
            jsonparams.put("relation", myapp.getRelation());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonparams;
    }

    public void handleResult() {
//		根据不同的题库 进行不同的逻辑判断
        Resources rs = this.getResources();
        String[] answerStandard = rs.getStringArray(R.array.answers);
        int dangerIndexNum = 0;
        int register = 0;
        boolean result = true;
        switch (questionType) {
            case 0:
//				由于相关的题库尚未搜集好，该类型处理逻辑暂且搁置
                break;
            case 1:
//				测试结果：阳性。说明 ：该量表用于筛查 16~30月龄 儿 童 ,共 23 项 由 家 长 填 写 。 单 项 阳 性 判 别 标 准 :答 案选项为“是”或“否”,第 1、18、20、2项选“是”, 其余各项选“否”时即判断该项为阳性;总阳性判别 标准:总共 23项中≥3项阳性或 6项核心项目中 (核 心 项 目 为 第 2、7、9、13、14、15 项 )≥ 2 项 阳 性 为 孤独症高风险,需进一步电话随访,仍未通过者则需 进一步评估。
//				16到30个月情况
                for (int i = 0; i < questionsArray.length; i++) {
                    if (answerStandard.equals(answers.get(i))) ;    //相同说明，用户选择是倾向于阳性，相关计数器增加
                    {
                        register++;
                        if (i == 2 || i == 7 || i == 9 || i == 13 || i == 14 || i == 15) {
                            dangerIndexNum++;
                        }
                        if (dangerIndexNum >= 2) {
                            result = false;
                            break;
                        }
                    }
                    if (register >= 3) {
                        result = false;
                        break;
                    }
                }
                break;
            case 2:
//				同0，由于相关的题库尚未搜集好，该类型处理逻辑暂且搁置
                break;
            default:
                break;
        }
        if (!result) {
            questionTv.setText(Html.fromHtml(getString(R.string.main_result_yang)));
        } else {
            questionTv.setText(getString(R.string.main_result_nyang));
        }
    }

    /**
     * 实现渐变效果
     *
     * @param view
     */
    public void startAlphaAnimation(View view) {
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        aa.setDuration(1000);
        view.startAnimation(aa);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
