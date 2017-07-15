package com.example.qman.rockpad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.qman.rockpad.tools.VoiceSpeaker;
import com.example.qman.rockpad.utils.ActivityUtil;
import com.example.qman.rockpad.utils.TimerUtil;

import stonectr.serial.SerialController;

public class MapOneActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btn_water;
    private Button btn_sadas;
    private Button btn_tea;
    private ImageButton button_yes;
    private ImageButton button_no;
    private ImageView map_image;

    private int product_id;
    private String[] products = {"健力宝", "可口可乐", "名仁", "百岁山", "维他柠檬茶",
            "绿茶", "小茗", "怡宝", "农夫山泉", "碳酸饮料",
            "茶", "纯净水", "饮料", "曲奇饼干", "垃圾袋",
            "纸杯", "卫生间", "收银台"};
    //地图的长度、宽度
    private float map_width = 0;
    private float map_height = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map1);
        //初始化控件
        initView();
        //更新实时位置
  //      updatePosition();

        //10秒后自动退回
        TimerUtil.startTime(MapOneActivity.this,  10000);
        }
        @Override
        public void onResume()
        {
            super.onResume();
            Intent intent = getIntent();
            product_id = intent.getIntExtra("product_id",-1);
            if (product_id > 0)
            {
                map_image.setImageResource(R.drawable.map_01 + product_id - 1);
                VoiceSpeaker.getInstance().speak(products[product_id - 1
                        ] + "的位置给您显示出来了");
            }
        }
    private void initView() {
        //初始化
        btn_water = (Button)findViewById(R.id.map_class_water);
        btn_water.setOnClickListener(this);
        btn_sadas = (Button)findViewById(R.id.map_class_sodas);
        btn_sadas.setOnClickListener(this);
        btn_tea = (Button)findViewById(R.id.map_class_tea);
        btn_tea.setOnClickListener(this);
        button_yes = (ImageButton) findViewById(R.id.map_choose_yes);
        button_yes.setOnClickListener(this);
        button_no = (ImageButton) findViewById(R.id.map_choose_no);
        button_no.setOnClickListener(this);

        map_image = (ImageView) findViewById(R.id.map_img);

        map_width = getResources().getDimension(R.dimen.map_width);
        map_height = getResources().getDimension(R.dimen.map_height);
    }
    /*更新实时位置*/
    private void updatePosition(){

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) map_image.getLayoutParams();
        params.setMargins(300, 300, 300, 300);// 通过自定义坐标来放置你的控件
        map_image.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.map_class_water:
                product_id = 12;
                map_image.setImageResource(R.drawable.map_12);
                break;
            case R.id.map_class_sodas:
                product_id = 10;
                map_image.setImageResource(R.drawable.map_10);
                break;
            case R.id.map_class_tea:
                product_id = 11;
                map_image.setImageResource(R.drawable.map_11);
                break;
            case R.id.map_choose_yes:
                SerialController.getInstance().moveTo((byte)product_id);
                finish();
                break;
            case R.id.map_choose_no:
                finish();
                break;
        }
    }
}
