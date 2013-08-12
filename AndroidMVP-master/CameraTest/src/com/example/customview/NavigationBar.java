package com.example.customview;


import com.example.cameratest.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class NavigationBar extends RelativeLayout {


    private Button btn_left;
    private Button btn_right;
    private TextView tv_title;

    public NavigationBar(Context context) {
        super(context);
        init(context);

    }

    public NavigationBar(Context context, AttributeSet arg1) {
        super(context, arg1);
        init(context);
    }

    public NavigationBar(Context context, AttributeSet arg1, int defStyle) {
        super(context, arg1, defStyle);
        init(context);
    }


    public void init(Context context) {

        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.navigationbar, this, true);

        btn_left = (Button) findViewById(R.id.leftBtn);
        btn_right = (Button) findViewById(R.id.rightBtn);
        tv_title = (TextView) findViewById(R.id.titleTv);
    }

    public void setBtnLeftClickListener(OnClickListener listener) {
        btn_left.setOnClickListener(listener);
    }

    public void setBtnRightClickListener(OnClickListener listener) {
        btn_right.setOnClickListener(listener);
    }

    public void setBtnLeftText(String text) {
        if (btn_left != null) {
            btn_left.setText(text);
        }
    }

    public void setBtnLeftVisble(boolean isVisble) {
        if (isVisble) {
            btn_left.setVisibility(VISIBLE);
        } else {
            btn_left.setVisibility(INVISIBLE);
        }
    }

    public void setBtnRightVisble(boolean isVisble) {
        if (btn_right != null) {
            if (isVisble) {
                btn_right.setVisibility(VISIBLE);
            } else {
                btn_right.setVisibility(INVISIBLE);
            }
        }
    }


    public void setBtnRightText(String text) {
        if (btn_right != null) {
            btn_right.setText(text);
        }
    }

    public void setBtnLeftBacground(int resId) {

        if (btn_left != null) {
            btn_left.setBackgroundResource(resId);
        }
    }

    public void setBtnRightBacground(int resId) {
        if (btn_right != null) {
            btn_right.setBackgroundResource(resId);
        }

    }

    public void setTvTitle(String text) {
        if (tv_title != null) {
            tv_title.setText(text);
        }
    }

    public Button getBtn_left() {
        return btn_left;
    }

    public Button getBtn_right() {
        return btn_right;
    }

    public TextView getTv_title() {
        return tv_title;
    }


}
