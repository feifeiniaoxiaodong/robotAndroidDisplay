package com.example.qman.rockpad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.qman.rockpad.constant.BroadcastType;
import com.example.qman.rockpad.service.SerialService;
import com.example.qman.rockpad.test.MusicTestThread;
import com.example.qman.rockpad.test.MysqlTestThread;
import com.example.qman.rockpad.test.SerialTestThread;
import com.example.qman.rockpad.utils.ActivityUtil;

import java.io.File;

import stonectr.serial.SerialController;
import stonectr.serial.serialport.SerialPort;
import stonectr.serial.serialport.SerialPortUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView wendu_value = null;
    private TextView shidu_value = null;
    private TextView yanwu_value = null;
    private TextView PM_value = null;
    private TextView dianliang_value = null;
    private ImageButton wakeup_button = null;
    private Button test_open = null;
    private SerialMsgReceiver serialReceiver;
    private IntentFilter intentFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        //开启广播接收
        serialReceiver = new SerialMsgReceiver();
        intentFilter = new IntentFilter("com.stone.uartBroadcast");
        registerReceiver(serialReceiver, intentFilter);

       Intent serviceIntent = new Intent(this, SerialService.class);
       startService(serviceIntent);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(serialReceiver);
        super.onDestroy();
    }

    private void initView() {
        //初始化
        initTextView(R.id.main_temp_t, "温度", 1);
        initTextView(R.id.main_humidity_t, "湿度", 1);
        initTextView(R.id.main_somg_t, "烟雾", 1);
        initTextView(R.id.main_pm25_t, "PM2.5", 2);
        initTextView(R.id.main_level_t, "电量", 1);

        wendu_value = (TextView) findViewById(R.id.main_wendu_value);
        shidu_value = (TextView) findViewById(R.id.main_shidu_value);
        yanwu_value = (TextView) findViewById(R.id.main_yanwu_value);
        PM_value = (TextView) findViewById(R.id.main_PM_value);
        dianliang_value = (TextView) findViewById(R.id.main_dianliang_value);
        wakeup_button = (ImageButton) findViewById(R.id.wakeup_button);
        wakeup_button.setOnClickListener(this);
        test_open=(Button)findViewById(R.id.test_btn);
        test_open.setOnClickListener(this);
        setValue();
    }
    private void initTextView(int id, String str, int index)
    {
        TextView t = (TextView)findViewById(id);
        SpannableString text = new SpannableString(str);
        text.setSpan(new TextAppearanceSpan(this, R.style.style_main_bigFont), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new TextAppearanceSpan(this, R.style.style_main_smallFont), index, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        t.setText(text, TextView.BufferType.SPANNABLE);
    }

    @Override
    public void onClick(View v) {
      switch (v.getId())
        {
            case R.id.test_btn:

                new Thread(new MusicTestThread(getApplicationContext())).start(); //测试MediaPlayer，可删

//               new Thread( new SerialTestThread()).start(); //开启一个测试线程，可删

//                new Thread(new MysqlTestThread()).start(); //测试数据库，可删

                break;
            case R.id.wakeup_button:
                //        ActivityUtil.toastShow(this, "点我干嘛？");
                Intent intent = new Intent(this, WakeUpActivity.class);
                startActivity(intent);
            break;
        }
    }

    private void setValue() {
        wendu_value.setText("50℃");
        shidu_value.setText("40");
        yanwu_value.setText("5");
        PM_value.setText("63");
        dianliang_value.setText("90");
    }

    class SerialMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String type = bundle.getString("info");
            if (type.equals(BroadcastType.ROBOTINFO)) { //界面温湿度显示
                wendu_value.setText(bundle.getInt(BroadcastType.ROBOTINFO_TEMPERATURE, -1) + "℃");
                shidu_value.setText(bundle.getByte(BroadcastType.ROBOTINFO_HUMIDITY, (byte)0) + "");
                yanwu_value.setText(bundle.getInt(BroadcastType.ROBOTINFO_SMOKE, -1) + "");
                PM_value.setText(bundle.getInt(BroadcastType.ROBOTINFO_PM25, -1) + "");
               dianliang_value.setText(bundle.getByte(BroadcastType.ROBOTINFO_LEVEL, (byte) 0) + "%");
            }else{
                //找不到音乐
                //音乐播放结束
            }
        }
    }
}
