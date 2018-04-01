package com.example.qman.rockpad.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.example.qman.rockpad.MemberActivity;
import com.example.qman.rockpad.ProductsActivity;
import com.example.qman.rockpad.RFIDMemberActivity;
import com.example.qman.rockpad.WakeUpActivity;
import com.example.qman.rockpad.application.StoneRbtApp;
import com.example.qman.rockpad.constant.BroadcastType;

import stonectr.serial.ControlCallBack;
import stonectr.serial.SerialController;
import stonectr.serial.callBackEvent.UartBaseEvent;
import stonectr.serial.callBackEvent.UartCodebarEvent;
import stonectr.serial.callBackEvent.UartEventNormal;
import stonectr.serial.callBackEvent.UartEventOther;
import stonectr.serial.callBackEvent.UartGetFaceEvent;
import stonectr.serial.callBackEvent.UartRfidEvent;
import stonectr.serial.callBackEvent.UartRobotInfoEvent;
import stonectr.serial.callBackEvent.UartRobotPoseEvent;
import stonectr.serial.callBackEvent.UartShelvesInfoEvent;
import stonectr.serial.callBackEvent.UartWakeUpEvent;
import stonectr.serial.serialapply.SerialPortUtil;

/**
 * Created by Sunshine on 2017/3/28.
 */

public class SerialService extends Service implements ControlCallBack {

    private final String TAG = "Serial Service";
    public static final String[] BODY_FORWARD = {"往前", "向前", "前进"};
    public static final String[] BODY_BACKWARD = {"后退", "向后", "往后"};
    public static final String[] BODY_LEFT = {"左转", "向左"};
    public static final String[] BODY_RIGHT = {"右转", "向右", "看右边"};
    private final IBinder serialServiceBinder = new SerialServiceBinder();
    private MobileMsgHandler mobileMsgHandler;
    SerialController serialController;
    private MusicBroadCastReceiver musicReceiver;

    @Override
    public void onCreate()
    {
        Log.d(TAG, "service onCreate");
        super.onCreate();
        mobileMsgHandler = MobileMsgHandler.getInstance();
        mobileMsgHandler.setContext(StoneRbtApp.getApplication());
        mobileMsgHandler.getSystemIP();
        mobileMsgHandler.startMobileRcv();
        serialController =  SerialController.getInstance();
        serialController.setControlCallBack(this);
        SerialPortUtil.getInstance().initSerialPort(); //初试化串口，开启串口接受线程

    /*   try{
            serialController.startWakeRcv(); //造成闪退
//        serialController.startScanRcv();
            serialController.startRosRcv();
        }catch(Exception e) {
            e.printStackTrace();
        }*/
        musicReceiver=new MusicBroadCastReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.music.complete");
        registerReceiver(musicReceiver,filter);
    }
    @Override
    public int onStartCommand(Intent intent, int flag, int startID){
        Log.d(TAG, "service onStartCommand");
        return super.onStartCommand(intent, flag, startID);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "service onBind");
        return serialServiceBinder;
    }

    public class SerialServiceBinder extends Binder {
        public SerialServiceBinder() {
        }
        public SerialService getSerialService() {
            return SerialService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.d(TAG, "service onUnbind ");
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy()
    {
        Log.d(TAG, "service onDestroy");
        super.onDestroy();
       serialController.stopRosRcv();
        serialController.stopWakeRcv();
    }

    public void setRemoteIP(String ip) {
        mobileMsgHandler.setRemoteIP(ip);
    }

    @Override
    public void onReback(UartBaseEvent event) {
        if(event instanceof UartEventNormal)
        {
            Log.d("SerialUser", "onReback: commmandNormal command " + ((UartEventNormal) event).getCommandNum());
        }
        else if (event instanceof UartWakeUpEvent)
        {
//            sendBroadcast(BroadcastType.WAKEUP,"" + ((UartWakeUpEvent) event).getAngle());
            Intent wakeIntent = new Intent(this, WakeUpActivity.class);
            wakeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(wakeIntent);

            Intent intent=new Intent(this,PlayMusicService.class);
            intent.putExtra("type",PlayMusicService.STOP_MUSIC);
            startService(intent);
        }
        else if(event instanceof UartCodebarEvent)
        {
            if (((UartCodebarEvent) event).getIs2DBar())
            {
                sendBroadcast(BroadcastType.SCAN2BAR,((UartCodebarEvent) event).getBar());
                Log.d("get message","get 2D bar" + ((UartCodebarEvent) event).getBar());
            }
            else
            {
                sendBroadcast(BroadcastType.SCANBAR,((UartCodebarEvent) event).getBar());
//                Log.d("get message", "get bar " + ((UartCodebarEvent) event).getBar());
                Intent scanIntent = new Intent(this, ProductsActivity.class);
                scanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                scanIntent.putExtra("barID",((UartCodebarEvent) event).getBar());
                startActivity(scanIntent);
            }
        }
        else if (event instanceof UartRobotInfoEvent)
        {
            sendBroadcast(BroadcastType.ROBOTINFO,(UartRobotInfoEvent) event);
        }
        else if (event instanceof UartRobotPoseEvent)
        {
            sendBroadcast(BroadcastType.ROBOTPOSE,(UartRobotPoseEvent) event);
        }
        else if (event instanceof UartGetFaceEvent)
        {
                    sendBroadcast(BroadcastType.FACE,"" + ((UartGetFaceEvent) event).getFaceID());
                    Intent faceIntent = new Intent(this, MemberActivity.class);
                    faceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    faceIntent.putExtra("faceID",((UartGetFaceEvent) event).getFaceID());
                    startActivity(faceIntent);
        }
        else if (event instanceof UartRfidEvent)
        {
            sendBroadcast(BroadcastType.RFID,"" + ((UartRfidEvent) event).getId());
            Intent rfidIntent = new Intent(this, RFIDMemberActivity.class);
            rfidIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            rfidIntent.putExtra("rfid",((UartRfidEvent) event).getId());
            startActivity(rfidIntent);


        }
        else if (event instanceof UartShelvesInfoEvent)
        {
               mobileMsgHandler.sendMsg("shelves " + ((UartShelvesInfoEvent) event).getTop() + " " + ((UartShelvesInfoEvent) event).getMiddle() );
        }
        else if (event instanceof UartEventOther)
        {
            Log.d("SerialUser", "onReback: commmandOther");
        }
    }

    private void sendBroadcast(String str,String value)
    {
        Intent intent = new  Intent();
        intent.setAction("com.stone.uartBroadcast");
        intent.putExtra("info",str);
        intent.putExtra("value",value);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String str,UartRobotPoseEvent event)
    {
        Intent intent = new  Intent();
        intent.setAction("com.stone.uartBroadcast");
        intent.putExtra("info",str);
        intent.putExtra("x", event.getPosition_x());
        intent.putExtra("y", event.getPosition_y());
        intent.putExtra("theta", event.getRotation());
        sendBroadcast(intent);
    }

    private void sendBroadcast(String str,UartRobotInfoEvent event)
    {
        Intent intent = new  Intent();
        intent.setAction("com.stone.uartBroadcast");
        intent.putExtra("info",str);
        intent.putExtra(BroadcastType.ROBOTINFO_PM25, event.getPm25());
    //    mobileMsgHandler.sendRbtInfoMsg(BroadcastType.ROBOTINFO_PM25 + " " + event.getPm25());
        intent.putExtra(BroadcastType.ROBOTINFO_PM10, event.getPm10());
    //    mobileMsgHandler.sendRbtInfoMsg(BroadcastType.ROBOTINFO_PM10 + " " + event.getPm10());
        intent.putExtra(BroadcastType.ROBOTINFO_TEMPERATURE, event.getTemperature());
     //   mobileMsgHandler.sendRbtInfoMsg(BroadcastType.ROBOTINFO_TEMPERATURE + " " + event.getTemperature());
        intent.putExtra(BroadcastType.ROBOTINFO_HUMIDITY,event.getHumidity());
    //    mobileMsgHandler.sendRbtInfoMsg(BroadcastType.ROBOTINFO_HUMIDITY + " " + event.getHumidity());
        intent.putExtra(BroadcastType.ROBOTINFO_SMOKE,event.getSmoke());
     //   mobileMsgHandler.sendRbtInfoMsg(BroadcastType.ROBOTINFO_SMOKE + " " + event.getSmoke());
        intent.putExtra(BroadcastType.ROBOTINFO_LEVEL,event.getLevel());
    //    mobileMsgHandler.sendRbtInfoMsg(BroadcastType.ROBOTINFO_LEVEL + " " + event.getLevel());
        intent.putExtra(BroadcastType.ROBOTINFO_CHARGING,event.isCharging());
    //    mobileMsgHandler.sendRbtInfoMsg(BroadcastType.ROBOTINFO_CHARGING + " " + event.isCharging());
        sendBroadcast(intent);
    }

    public void resolve(String str)
    {
        if (str == null || str.length() == 0 || str.equals("。") || str.equals("！") || str.equals("？")) {
            return;
        }
        if (str.contains(BODY_FORWARD[0])
                || str.contains(BODY_FORWARD[1])
                || str.contains(BODY_FORWARD[2])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止")){
                return;
            }
        //    if (str.contains("米"))
            SerialController.getInstance().sendForward();

        } else if (str.contains(BODY_BACKWARD[0])
                || str.contains(BODY_BACKWARD[1])
                ) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止"))
                return;
            SerialController.getInstance().sendBackward();
        } else if (str.contains(BODY_LEFT[0])
                || str.contains(BODY_LEFT[1])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止"))
                return;
            SerialController.getInstance().sendLeft();
        } else if (str.contains(BODY_RIGHT[0])
                || str.contains(BODY_RIGHT[1])) {
            if (str.contains("不") || str.contains("别") || str.contains("甭") || str.contains("禁止"))
                return;
            SerialController.getInstance().sendLeft();
        }
    }

    public class MusicBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
         //   Toast.makeText(context,"音乐播放结束",Toast.LENGTH_SHORT).show();
        }
    }

}
