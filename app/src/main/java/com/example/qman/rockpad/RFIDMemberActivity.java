package com.example.qman.rockpad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qman.rockpad.constant.BroadcastType;

public class RFIDMemberActivity extends AppCompatActivity implements View.OnClickListener  {
    private ImageButton wakeup_button;
    private ImageView image;
    private TextView username_t;
    private TextView record_t;
    private TextView welcome_t;
    private IntentFilter intentFilter;
    private SerialMsgReceiver serialReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid_member);
        //初始化控件
        initView();
        //设置控件的值

        //开启广播接收
        serialReceiver = new SerialMsgReceiver();
        intentFilter=new IntentFilter("com.stone.uartBroadcast");
        registerReceiver(serialReceiver, intentFilter);
    }
    private void initView(){
        //初始化
        username_t = (TextView) findViewById(R.id.rfid_name);
      //  record_t = (TextView) findViewById(R.id.rfid_record);
        welcome_t = (TextView)findViewById(R.id.rfid_user);
        image = (ImageView)findViewById(R.id.rfid_img);
        wakeup_button = (ImageButton) findViewById(R.id.rfid_wakeup_button);
        wakeup_button.setOnClickListener(this);
    }
@Override
    public void onResume()
    {

        super.onResume();
     //   record_t.setText("我的名字");
        Intent intent = getIntent();
       int id = intent.getIntExtra("rfid",-3);
        setValue(id);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(serialReceiver);
    }
    private void setValue(int faceID){

        String[] name = {"丁宁","董登峰","董洪义","冯瑾","梁帆", "李慧君","刘鹏","秦蔓","王宝明","王绮涵",
                          "王现永","王鑫磊","肖胜杰","邢瑞林","徐宽","徐越","杨璐","于旸","张翰天","张新华"} ;
        int[] id = {0x3a75fb9e, 0xe417bb95, 0x7a152b74,0x1AB9FA4E,0xDA262474,
                0x06ECDAAB,0x96986F3B,0x6BD8DAAB,0xFACEDAAB,0xC67FD5AB,
                0x1635DBAB,0x9C69D5AB,0xB9696F3B,0x73666F3B,0x9AA26E8B,
                0x4DAF6E3B,0x6D76703B};
        String[] id_name = {"董洪义","王现永","余浩","徐越","肖胜杰",
                "王鑫磊","蒋瞰阳","冯瑾","董登峰","陈浩",
                "安淳榆","张新华","余暘","时同华","刘鹏",
                "邢瑞林","备用"};
        for (int i = 0; i < id.length; i++)
        {
            if (faceID == id[i])
            {
                username_t.setText(id_name[i]);
                Log.d("aisdi a", "第--------------" + i);
            }
        }
  //       String[] record = {"苹果" ,"矿泉水", "周黑鸭,面膜"};
   //     welcome_t.setText("尊敬的" + name[faceID] + "，您好");
   //     username_t.setText(name[faceID]);
//        if (faceID >= 0)
//        switch (faceID)
//        {
//            case 0:
//                break;
//            case 1:
//                image.setImageResource(R.drawable.men_01);
//                break;
//            case 2:
//                image.setImageResource(R.drawable.men_02);
//                break;
//            case 3:
//                image.setImageResource(R.drawable.men_03);
//                break;
//            case 6:
//                image.setImageResource(R.drawable.men_06);
//                break;
//            case 8:
//                image.setImageResource(R.drawable.men_08);
//                break;
//            case 10:
//                image.setImageResource(R.drawable.men_10);
//                break;
//            case 11:
//                image.setImageResource(R.drawable.men_11);
//                break;
//            case 13:
//                image.setImageResource(R.drawable.men_13);
//                break;
//            case 19:
//                image.setImageResource(R.drawable.men_19);
//                break;
//            default:
//
//        }
//

    }
    @Override
    public void onClick(View v) {
       // ActivityUtil.toastShow(this,"点我干嘛？");
        Intent intent = new Intent(this, WakeUpActivity.class );
        startActivity(intent);
    }
    class SerialMsgReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String type = bundle.getString("info");
            if (type.equals("face"))
            {
                int id = Integer.parseInt(bundle.getString("value"));
                setValue(id);
                //query( bundle.getString("value"));
            }
            else if (type.equals(BroadcastType.SCAN2BAR))
            {

            }

        }
    }
}
