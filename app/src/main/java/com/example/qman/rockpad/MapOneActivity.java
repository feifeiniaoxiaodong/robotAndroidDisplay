package com.example.qman.rockpad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.qman.rockpad.utils.ActivityUtil;

import java.util.Map;

public class MapOneActivity extends AppCompatActivity {
    private ImageView button1 = null;
    private ImageView button2 = null;
    private ImageView button3 = null;

    private ImageView button_yes = null;
    private ImageView button_no = null;

    private ImageView robot = null;
    //地图的长度、宽度
    private float map_width = 0;
    private float map_height = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map1);

        //初始化控件
        init();

        //更新实时位置
        updatePosition();

//
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityUtil.toastShow(MapOneActivity.this,"按钮1");
//            }
//        });
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityUtil.toastShow(MapOneActivity.this,"按钮2");
//            }
//        });
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ActivityUtil.toastShow(MapOneActivity.this,"按钮3");
//            }
//        });
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.toastShow(MapOneActivity.this,"需要");
            }
        });
        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.toastShow(MapOneActivity.this,"不需要");
            }
        });
    }
    private void init() {
        //初始化
//        button1 = (ImageView) findViewById(R.id.kuang1);
//        button2 = (ImageView) findViewById(R.id.kuang2);
//        button3 = (ImageView) findViewById(R.id.kuang3);

        button_yes = (ImageView) findViewById(R.id.yes);
        button_no = (ImageView) findViewById(R.id.no);

        robot = (ImageView) findViewById(R.id.robot);

        map_width = getResources().getDimension(R.dimen.map_width);
        map_height = getResources().getDimension(R.dimen.map_height);
    }
    /*更新实时位置*/
    private void updatePosition(){

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)robot.getLayoutParams();
        params.setMargins(300, 300, 300, 300);// 通过自定义坐标来放置你的控件
        robot .setLayoutParams(params);
    }
}
