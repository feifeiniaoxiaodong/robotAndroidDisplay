package stonectr.serial.serialapply;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import stonectr.serial.serialport.SerialPort;

/**
 * 管理
 * 接收唤醒命令的串口
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
        initSerialPort(portpath,buadrate,0);
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

        }
    }

}
