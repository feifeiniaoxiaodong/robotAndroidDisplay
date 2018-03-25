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

  /*  public void test()
    {
        SerialController.getInstance().init("/dev/ttyS3",9600,8,1,'N');
        SerialController.getInstance().open();
        System.out.println("isOpen:"+SerialController.getInstance().isOpen()+"");
        byte[] buf =new byte[100];
        SerialController.getInstance().read(buf,100);
        int x = 'A';
        System.out.println("buf:"+buf+x);
    }*/

    /**
     * 串口测试
     * 串口初始化
     * add:wei
     */
 /*   public void initSerial(){
        String portpath="/dev/ttyS3";
        int buadrate=115200 ;

        SerialPortUtil serialPortUtil=SerialPortUtil.getInstance();
        //初始化串口
        serialPortUtil.initSerialPort(portpath,buadrate,0);
    }*/

    /**
     * 串口测试，发送数据
     * add :wei
     */
 /*   public void testSerial(){
        int[] testBuffer=new int [] {0xe2,0x23,0x78,0x12,0xad};
        if(SerialPortUtil.getInstance().isSerialOpen()){
            SerialPortUtil.getInstance().sendBuffer(testBuffer);
        }
    }*/

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

//        initSerial();

        /*System.out.println("6666666666666666666666666");
        System.out.println("zrvalue is:"+ SerialController.getInstance().add(190, 7));
        System.out.println("zrStr is:"+ SerialController.getInstance().getString("/dev/ttyS777"));

        SerialController.getInstance().init("/dev/ttyS3",9600,8,1,'N');*/

//        try {
//				/* Missing read/write permission, trying to chmod the file */
//            Process su;
//            su = Runtime.getRuntime().exec("/system/bin/su");
//            String cmd = "chmod 666 " + "/dev/ttyS3" + "\n"
//                    + "exit\n";
//            su.getOutputStream().write(cmd.getBytes());
//            if ((su.waitFor() != 0)) {
//                throw new SecurityException();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new SecurityException();
//        }
        //System.out.println("open:"+SerialController.getInstance().open());
        //System.out.println("isOpen:"+SerialController.getInstance().isOpen()+"");
//        byte[]buf = {1,1,2};
        //SerialController.getInstance().read(buf,buf.length);
        //SerialController.getInstance().write(buf,buf.length);
//        int x = 'A';
//        System.out.println("buf:"+buf.toString()+x);
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

    private void setValue() {
        wendu_value.setText("50℃");
        shidu_value.setText("40");
        yanwu_value.setText("5");
        PM_value.setText("63");
        dianliang_value.setText("90");
    }

    @Override
    public void onClick(View v) {
      switch (v.getId())
        {
            case R.id.test_btn:

                new Thread(new MusicTestThread(getApplicationContext())).start(); //测试MediaPlayer

//               new Thread( new SerialTestThread()).start(); //开启一个测试线程，可删

//                new Thread(new MysqlTestThread()).start(); //测试数据库

                /*testSerial();*/
             /*   ActivityUtil.toastShow(this, "点我干嘛？");
                File device = new File("/dev/ttyS3");
                //检查访问权限，如果没有读写权限，进行文件操作，修改文件访问权限
                if (!device.canRead() || !device.canWrite())
                {
                    try {
                        //通过挂载到linux的方式，修改文件的操作权限
                        Process su = Runtime.getRuntime().exec("/system/bin/su");
                        String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
                        su.getOutputStream().write(cmd.getBytes());

                        if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                            throw new SecurityException();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new SecurityException();
                    }
                }
                SerialController.getInstance().init("/dev/ttyS3",9600,8,1,'N');
                boolean flag = false;
                if(!flag)
                {
                    flag = true;
                    System.out.println("open(MainActivity):"+SerialController.getInstance().open());
                    System.out.println("isOpen(MainActivity):"+SerialController.getInstance().isOpen()+"");
                }
                byte[]data = new byte[10];
                // trans to cpp
                for (byte i = 0;i<10;i++)
                {
                    data[i] = (byte)(i+1);
                }
                System.out.println("read(MainActivity) "+SerialController.getInstance().read(data,10)+" bytes");
                for (int i = 0;i< data.length;i++)
                {
                    System.out.println("data[" +i + "] = "+data[i]);
                }*/
                break;
            case R.id.wakeup_button:
                //        ActivityUtil.toastShow(this, "点我干嘛？");
                Intent intent = new Intent(this, WakeUpActivity.class);
                startActivity(intent);
            break;
        }
    }


    class SerialMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String type = bundle.getString("info");
            if (type.equals(BroadcastType.ROBOTINFO)) {
                wendu_value.setText(bundle.getInt(BroadcastType.ROBOTINFO_TEMPERATURE, -1) + "℃");
                shidu_value.setText(bundle.getByte(BroadcastType.ROBOTINFO_HUMIDITY, (byte)0) + "");
                yanwu_value.setText(bundle.getInt(BroadcastType.ROBOTINFO_SMOKE, -1) + "");
                PM_value.setText(bundle.getInt(BroadcastType.ROBOTINFO_PM25, -1) + "");
               dianliang_value.setText(bundle.getByte(BroadcastType.ROBOTINFO_LEVEL, (byte) 0) + "%");
            }
        }
    }
}
