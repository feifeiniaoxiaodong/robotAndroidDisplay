package com.example.qman.rockpad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qman.rockpad.utils.ActivityUtil;

public class MemberActivity extends AppCompatActivity implements View.OnClickListener  {
    private Button wakeup_button = null;
    private TextView name_value = null;
    private TextView jilu_value = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        //初始化控件
        init();
        //设置控件的值
        setValue();
        wakeup_button.setOnClickListener(this);
    }
    private void init(){
        //初始化
        name_value = (TextView) findViewById(R.id.name_value);
        jilu_value = (TextView) findViewById(R.id.jilu_value);
        wakeup_button = (Button) findViewById(R.id.wakeup_button);
    }

    private void setValue(){
        name_value.setText("秦曼");
        jilu_value.setText("酸奶，矿泉水");
    }
    @Override
    public void onClick(View v) {
        ActivityUtil.toastShow(this,"点我干嘛？");
    }
}
