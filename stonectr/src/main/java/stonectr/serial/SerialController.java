package stonectr.serial;

import android.util.Log;

import stonectr.serial.callBackEvent.UartCodebarEvent;
import stonectr.serial.callBackEvent.UartEventNormal;
import stonectr.serial.callBackEvent.UartEventOther;
import stonectr.serial.callBackEvent.UartRobotInfoEvent;
import stonectr.serial.callBackEvent.UartRobotPoseEvent;
import stonectr.serial.callBackEvent.UartWakeUpEvent;


/**
 * Created by Sunshine on 2017/3/28.
 */

public class SerialController {


    private static ControlCallBack callback;

    private static String TAG = "UARTReceive";
    private static SerialController instance;


    public void setControlCallBack(ControlCallBack callback) {
        this.callback = callback;
    }

    private SerialController() {
    }

    public static synchronized SerialController getInstance() {
        if (instance == null) {
            instance = new SerialController();
        }
        return instance;
    }
    //串口唤醒事件1
    public static void wakeUp(int angle)
    {
        callback.onReback(new UartWakeUpEvent(angle));
    }

    public static void robotInfo(int pm25, int pm10, int temp, byte humi, int smoke, byte level, byte charging)
    {
        UartRobotInfoEvent infoEvent = new UartRobotInfoEvent();
        infoEvent.setPm25(pm25);
        infoEvent.setPm10(pm10);
        infoEvent.setTemperature(temp);
        infoEvent.setHumidity(humi);
        infoEvent.setSmoke(smoke);
        infoEvent.setLevel(level);
        infoEvent.setCharging(charging);
        callback.onReback(infoEvent);
    }

    public static void robotPose(double x, double y, double theta)
    {
        UartRobotPoseEvent event = new UartRobotPoseEvent();
        event.setPosition_x(x);
        event.setPosition_y(y);
        event.setRotation(theta);
        callback.onReback(event);
    }
    public static void commandReceived1() {
        callback.onReback(new UartEventNormal(1));
    }

    public static void commandReceived2() {
        callback.onReback(new UartEventNormal(2));
    }

    public static void getBarcode(String code)
    {
        //获得条形码
        UartCodebarEvent event = new UartCodebarEvent();
        event.setIs2DBar(false);
        event.setBar(code);
        Log.d(TAG, "getBarcode: " + code);
        if (callback != null)
        callback.onReback(event);
    }
    public static void get2Barcode(String code)
    {
        //获得二维码
        UartCodebarEvent event = new UartCodebarEvent();
        event.setIs2DBar(true);
        event.setBar(code);
        Log.d(TAG, "get2Barcode: " + code);
        callback.onReback(event);
    }

    public static native void init();
    public static native void release();
    //开关串口
    public static native void openScanPort();
    public static native void closeScanPort();
    public static native void openWakePort();
    public static native void closeWakePort();
    public static native void openROSPort();
    public static native void closeROSPort();
    //相关功能的开启和关闭
    public static native void startWakeRcv();
    public static native void stopWakeRcv();
    public static native void startScanRcv();
    public static native void stopScanRcv();
    public static native void startRosRcv();
    public static native void stopRosRcv();


    public native void sendForward();
    public native void sendBackward();
    public native void sendLeft();
    public native void sendRight();
    public native void sendStop();
    public native void sendForward(byte distance);   //前进**米
    public native void sendBackward(byte distance);
    public native void sendLeft(byte distance);
    public native void sendRight(byte distance);

    public native void platformUp();
    public native void platformDown();
    public native void platformStop();
    public native void platformBottom();

    public native void patrol(byte line);//巡逻路线*

    public native void navigation(byte[] x, byte[] y, byte[] theta);  //导航到指定点，x、y、theta double 各8字节，


    public native void sendMoveToLiving();     //移动到客厅
    public native void sendMoveToBedroom();
    public native void sendMoveToYard();
    public native void sendPatrol();


    public native void voiceprintCheck();
    public native void voiceCheckSuccess();
    public native void voiceCheckFail();

    static {
        System.loadLibrary("serialTool");
        init();
    }

}
