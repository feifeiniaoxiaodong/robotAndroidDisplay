package stonectr.serial.serialapply;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.qman.rockpad.application.StoneRbtApp;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

import stonectr.serial.SerialController;
import stonectr.serial.serialport.SerialPort;

/**
 * 管理
 * 接收唤醒命令的串口
 * 串口接收语音唤醒命令，串口地址:/dev/ttyS3
 */
public class SerialPortWakeUpUtil {
    private final String TAG=SerialPortWakeUpUtil.class.getSimpleName();
    private volatile boolean isStop=false;
    private SerialPort  serialPort=null;
    private OutputStream mOutputStream=null;
    private InputStream mInputStream=null;

    private SerialReceiveThread serialReceiveThread=null;
    private ParseWakeUpSerialData parseWakeUpSerialData=null;

    private String  portpath="/dev/ttyS3"; //语音唤醒串口地址
    private int    buadrate= 115200 ;
    private int    flags =0;

    private static SerialPortWakeUpUtil  serialPortWakeUpUtil=null;       //单例模式
    public  static SerialPortWakeUpUtil  getInstance(){
        if(serialPortWakeUpUtil==null){
            synchronized (SerialPortWakeUpUtil.class){
                if(serialPortWakeUpUtil==null){
                    serialPortWakeUpUtil=new SerialPortWakeUpUtil();
                }
            }
        }
        return serialPortWakeUpUtil;
    }

    /**
     * 串口初始化
     */
    public void initSerialPort(){
        initSerialPort(portpath,buadrate,flags);
    }

    /**
     * 有参串口初始化
     * @param path
     * @param baudrate
     * @param flags
     */
    public void initSerialPort(String path,int baudrate,int flags){
        try{
            if(serialPort==null){
                if(path==null || "".equals(path.trim()) || baudrate <=0 ){
                    throw new InvalidParameterException();
                }
                serialPort=new SerialPort(new File(path),baudrate,flags);
                mOutputStream=serialPort.getOutputStream();
                mInputStream=serialPort.getInputStream();

                parseWakeUpSerialData =new ParseWakeUpSerialData();
                serialReceiveThread=new SerialReceiveThread();
                serialReceiveThread.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class SerialReceiveThread  extends Thread{
        @Override
        public void run() {
            while(!isStop && !isInterrupted()){
                int size=0 , nRead=0;
                try{
                    if(mInputStream==null) return ;
                    byte[] buffer =new byte[1024];

                    size= mInputStream.read(buffer);//堵塞式IO
                    try{
                        Thread.sleep(5);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    while((nRead=mInputStream.available())>0 && size<900 ){
                        nRead =mInputStream.read(buffer,size,100);
                        size+=nRead ;
                    }

                    if(size>0){
                        if(parseWakeUpSerialData!=null){
                            parseWakeUpSerialData.onDataReceive(buffer,size);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }//end while

        }
    }

    class ParseWakeUpSerialData  implements OnDataReceiveListener {

      /*  @Override
        public void run() {

        }*/

        //解析唤醒报文，执行唤醒操作
        @Override
        public void onDataReceive(byte[] buffer, int size) {
            String msg=null;
            try {
                 msg=new String(buffer,"ascii");
//                displayToastMain("收到的唤醒报文::"+msg);
                 msg=msg.toLowerCase();
                 msg=msg.substring(msg.indexOf('w'));//剔除wakeup前面的无用字符
                 msg= msg.substring(0, msg.indexOf("\n"));
//                displayToastMain("收到的唤醒报文::"+msg);

                if(msg.contains("wake")&& msg.contains("up") ){
                    int angle=0;
                    if(msg.contains("angle")){
                        String strmsg=msg.substring( msg.indexOf("!")+1);
                        String [] paramstrs= strmsg.split(" ");
                        String  anglestr= paramstrs[0].substring( paramstrs[0].indexOf(":")+1);
                        angle=Integer.parseInt(anglestr);
//                        displayToastMain("angle::"+angle);
                    }

                    SerialController.wakeUp(angle); //唤醒语音
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // 读取一行，即 "WAKE UP!angle:88 score:2049  key_word: shi2"
        }
    }

    //在主屏幕显示消息
    private void displayToastMain(String msg){
        Intent intent=new Intent("com.stone.toast");
        intent.putExtra("toast",msg);

        Context context= StoneRbtApp.getContext();
        context.sendBroadcast(intent);
    }

}
