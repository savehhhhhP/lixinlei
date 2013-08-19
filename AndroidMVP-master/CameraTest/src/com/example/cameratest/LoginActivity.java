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
 * Time: ����5:52
 * To change this template use File | Settings | File Templates.
 */
public class LoginActivity extends Activity {

    NavigationBar nbar;                                            //������
    Button btnLogin;
    ExpandableListView exlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         //btnLogin
        initUI();
        initNavigationBar();
    }
    List<String> group;           //���б�
    List<List<String>> child;     //���б�

    /**
     * ��ʼ���顢���б�����
     */
    private void initializeData(){
        group = new ArrayList<String>();
        child = new ArrayList<List<String>>();

        addInfo("Andy",new String[]{"male","138123***","GuangZhou"});

    }
    /**
     * ģ����顢���б��������
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


    Intent intent;
    RadioButton newUser;
    RadioButton oldUser;
    //newUserName
    EditText textNewUser;
    EditText textOldUser;
    Spinner spOldUser;
    RadioGroup rdGroup;
    LinearLayout ll;
    private void initUI() {
        btnLogin = (Button)findViewById(R.id.btnLogin);
        nbar = (NavigationBar)findViewById(R.id.navigationBar_Login);
        newUser =(RadioButton)findViewById(R.id.rbNewUser);
        oldUser =(RadioButton)findViewById(R.id.rbOldUser);
        rdGroup = (RadioGroup)findViewById(R.id.radioGroup);
        spOldUser =(Spinner)findViewById(R.id.spOldUserName);
        textNewUser = (EditText)findViewById(R.id.newUserName);
        textOldUser = (EditText)findViewById(R.id.oldUserName);
        setGroupListener(rdGroup);
        ll = (LinearLayout)findViewById(R.id.lLayout);

        intent = new Intent();
        intent.setClass(LoginActivity.this, FirstActivity.class);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent,0);
            }
        });
    }
    private void setGroupListener(RadioGroup rd){
         rd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(RadioGroup group, int checkedId) {
                 if(checkedId == R.id.rbNewUser){                  //��������û�
                     spOldUser.setEnabled(false);
                     textOldUser.setEnabled(false);
                     textNewUser.setEnabled(true);
                 }else{                                            //��������û�
                     spOldUser.setEnabled(true);
                     textOldUser.setEnabled(true);
                     textNewUser.setEnabled(false);
                 }
             }
         });

    }
    public void initNavigationBar() {
        nbar.setTvTitle("С���");
        nbar.setBtnLeftVisble(false);
        nbar.setBtnRightVisble(false);
    }
}
