package stonectr.serial;
// http://www.cnblogs.com/huangenai/p/6839477.html  编译时候出现 DELETE_FAILED_INTERNAL_ERROR 错误 樊嘉欣 2017-12-16 22:53:14
import android.util.Log;

import stonectr.serial.callBackEvent.UartCodebarEvent;
import stonectr.serial.callBackEvent.UartEventNormal;
import stonectr.serial.callBackEvent.UartGetFaceEvent;
import stonectr.serial.callBackEvent.UartRfidEvent;
import stonectr.serial.callBackEvent.UartRobotInfoEvent;
import stonectr.serial.callBackEvent.UartRobotPoseEvent;
import stonectr.serial.callBackEvent.UartShelvesInfoEvent;
import stonectr.serial.callBackEvent.UartWakeUpEvent;
import stonectr.serial.utils.BytesUtil;

import stonectr.serial.serialapply.SerialPortUtil;
import stonectr.serial.utils.CRCVerify;


/**
 * Created by Sunshine on 2017/3/28.
 */

public class SerialController {

    private static ControlCallBack callback;

    private final static String TAG = "UARTReceive";
    private static SerialController instance=null;
    private volatile boolean stopMotionFlag=false;

    public void setControlCallBack(ControlCallBack callback) {
        this.callback = callback;
    }

    private SerialController() {
    }

    //单例模式
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

    public static void getBarcode(String code) {
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

    public static void getFace(int faceID)
    {
        UartGetFaceEvent event = new UartGetFaceEvent();
        event.setFaceID(faceID);
        Log.d(TAG, "getFace: " + faceID);
        callback.onReback(event);
    }

    public static void getShelves(int top, int middle)
    {
        UartShelvesInfoEvent event = new UartShelvesInfoEvent();
        event.setTop(top);
        event.setMiddle(middle);
        Log.d(TAG, "getShelvesInfo: " + top + " " + middle);
        callback.onReback(event);
    }


    public static void getRfid(int r) {
        UartRfidEvent event = new UartRfidEvent();
//        Log.d(TAG, "getRfid: " + ((r + 256*256*256*256 ) % 256*256*256*256));

        Log.d(TAG, "getRfid: " + r);
        event.setId(r & 0xffffffff);
        callback.onReback(event);
    }

    public static void getRfid(long r) {
        UartRfidEvent event = new UartRfidEvent();
//        Log.d(TAG, "getRfid: " + ((r + 256*256*256*256 ) % 256*256*256*256));

        Log.d(TAG, "getRfid: " + r);
        event.setId(r & 0xffffffff);
        callback.onReback(event);
    }

    /***发送串口命令函数*add by wei 2018/3/10**/
    //待测试.....，2018/3/10
    //导航到指定点
    public  void navigation( double android_x, double android_y, double android_theta){
        navigation(  BytesUtil.parseDoubleToInts(android_x) ,
                    BytesUtil.parseDoubleToInts(android_y) ,
                  BytesUtil.parseDoubleToInts( android_theta));
    }

    //导航到指定点
    public  void navigation(int[] x, int[] y, int[] theta){ //导航到指定点，x、y、theta double 各8字节，
            int [] command =new int[28] ; //
           command[0]=0x55;command[1]=0x01;
           command[2]=0x40;
           System.arraycopy(x,0,command,3,8);
           System.arraycopy(y,0,command,11,8);
           System.arraycopy(theta,0,command,19,8);
           command[27]= CRCVerify.getCRCNum(command,27);
           SerialPortUtil.getInstance().sendBuffer(command) ; //发送命令
    }

    //编写功能函数，代替原native函数,功能实现函数
    //向前走3s后停下
    public  void sendForward(){
        new Thread(new Runnable() {
//            1000/50=20

            @Override
            public void run() {
                stopMotionFlag=false;
                int n=20*3; //每间隔50ms发送一次指令， 3s后停下
                while(!stopMotionFlag && n>0){
                   if(n>2){
                       sendMsgForward();
                   }else{
                       sendMsgStop(); //stop,发两次停止指令
                   }
                    n--;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendMsgStop(); //stop
            }
        }).start();
    }

    //向后走，3s后停下
    public  void sendBackward(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                stopMotionFlag=false;
                int n=20*3; //每间隔50ms发送一次指令， 3s后停下
                while(!stopMotionFlag && n>0){
                    if(n>2){
                        sendMsgBackward();
                    }else{
                        sendMsgStop(); //stop
                    }
                    n--;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendMsgStop(); //stop
            }
        }).start();
    }

    //向左转，3s后停下
    public  void sendLeft(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                stopMotionFlag=false;
                int n=20*3; //每间隔50ms发送一次指令， 3s后停下
                while(!stopMotionFlag && n>0){
                    if(n>2){
                        sendMsgLeft();
                    }else{
                        sendMsgStop(); //stop
                    }
                    n--;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendMsgStop(); //stop
            }
        }).start();
    }

    //向右转，3s后停下
    public  void sendRight(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                stopMotionFlag=false;
                int n=20*3; //每间隔50ms发送一次指令， 3s后停下
                while(!stopMotionFlag && n>0){
                    if(n>2){
                        sendMsgRight();
                    }else{
                        sendMsgStop(); //stop
                    }
                    n--;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendMsgStop(); //stop
            }
        }).start();
    }

    public  void stopRobotMoving(){
        stopMotionFlag=true;
        try {
            Thread.sleep(70);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendMsgStop(); //停止
    }
    
/*********************************************/
    //发送串口指令函数
    //向前走
    private void sendMsgForward(){
        simpleMove(0x00); //前进
    }
    //向后走
    private void sendMsgBackward(){
        simpleMove(0x01); //后退
    }
    private void sendMsgLeft(){
        simpleMove(0x03); //反转
    }
    private void sendMsgRight(){
        simpleMove(0x02); //正转
    }
    private void sendMsgStop(){
        simpleMove(0x04); //停止
    }
    private void simpleMove(int action){
        int []  command=new int[6];
        command[0]=0x55; command[1]=0x01;
        command[2]=0x10; //simple move
        command[3]=action; //动作
        command[4]=0;
        command[5]=CRCVerify.getCRCNum(command,5);
        SerialPortUtil.getInstance().sendBuffer(command) ; //发送命令
    }
    /****************/

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

    /*public native void sendForward();
    public native void sendBackward();
    public native void sendLeft();
    public native void sendRight();
    public native void sendStop();*/

//    public native void sendForward(byte distance);   //前进**米
    public native void sendBackward(byte distance);
    public native void sendLeft(byte distance);
    public native void sendRight(byte distance);

    public native void platformUp();
    public native void platformDown();
    public native void platformStop();
    public native void platformBottom();

    public native void moveTo(byte type);
    public native void gotoScan();

    public native void patrol(byte line);//巡逻路线*

//    public native void navigation(byte[] x, byte[] y, byte[] theta);  //导航到指定点，x、y、theta double 各8字节，

    public native void sendMoveToLiving();     //移动到客厅
    public native void sendMoveToBedroom();
    public native void sendMoveToYard();
    public native void sendPatrol();

    public native void voiceprintCheck();
    public native void voiceCheckSuccess();
    public native void voiceCheckFail();
    public native void voiceChec2kFail();

    // add by fjx ---2018-02-03 14:22:25----start
   /* public native void init(String port,int baudRate,int databits,int stopbits,int parity);
    public native int open();
    public native void close();
    public native int read(byte[] rcv_buf,int length);
    public native int write(byte[] tx_buf,int length);
    // 0:false  1:true
    public native int isOpen();
    public native String getString(String str);
    public native int add(int x ,int y);*/
    // add by fjx ---2018-02-03 14:22:25----end

/*   static {
        System.loadLibrary("serialTool");
        init();
    }*/

}
