package stonectr.serial.serialapply;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

import stonectr.serial.serialport.SerialPort;
import stonectr.serial.utils.BytesUtil;

/**
 * Created by wei on 2018/3/9.
 * 串口工具类
 * 温湿度和底盘控制串口号：/dev/ttyS0
 */

public class SerialPortUtil {
    private final String TAG=SerialPortUtil.class.getSimpleName();

    private SerialPort serialPort=null;
    private OutputStream mOutputStream=null;
    private InputStream  mInputStream=null;
    private SerialReadThread  mSerialReadThread= null;        //数据接收线程
    private OnDataReceiveListener onDataReceiveListener=null; //串口数据读取接口
    private ParseSerialData parseSerialData =null ;           //报文解析线程
    private volatile boolean isStop=false;

    private static SerialPortUtil  serialPortUtil=null;       //单例模式
    public  static SerialPortUtil  getInstance(){
        if(serialPortUtil==null){
            synchronized (SerialPortUtil.class){
                if(serialPortUtil==null){
                    serialPortUtil=new SerialPortUtil();
                }
            }
        }
        return serialPortUtil;
    }


    //设置数据接收接口
    public void setOnDataReceiveListener(
            OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    //初始化串口对象，并获取读写流
    public  void initSerialPort(){
        //从prefer获取路径，波特率，标志位
        //初始化串口
        String portpath="/dev/ttyS0";
        int buadrate=115200 ;
        initSerialPort(portpath,buadrate,0);
    }

    /**
     *  初始化串口对象，并获取读写流
     *  path:串口路径
     *  baudrate:波特率
     *  flags: 标志
     */
    public  void initSerialPort(String path,int baudrate,int flags){
        try{
            if(serialPort==null){
                if ((path.length() == 0) || (baudrate == -1)) {
                    throw new InvalidParameterException();
                }
                serialPort=new SerialPort(new File(path),baudrate,flags);
                mOutputStream=serialPort.getOutputStream();
                mInputStream=serialPort.getInputStream();

                //开启数据接收线程
//              isStop=false;
                mSerialReadThread= new SerialReadThread();//开启数据接收线程
                mSerialReadThread.start();

                 parseSerialData=new ParseSerialData();
                 parseSerialData.start();
                 onDataReceiveListener=parseSerialData;//设置数据接收接口
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //定义数据接收线程
    private class SerialReadThread extends  Thread{

        @Override
        public void run() {

            super.run();
            while(!isStop && !isInterrupted()){//条件：串口未关闭 ，没有中断产生
                int index=0 , nRead=0;
               int size=0;
               try{
                   if(mInputStream==null)  return ;

                   byte[] buffer=new byte[1024];

                  size=mInputStream.read(buffer);
                  try{
                      Thread.sleep(3);
                  }catch (Exception e){
                      e.printStackTrace();
                  }

                  while((nRead=mInputStream.available())>0 && size<900 ){
                      nRead =mInputStream.read(buffer,size,100);
                      size+=nRead ;
                   }

                   if(size>=0){ //接收的最短报文长15

                       if(null!=onDataReceiveListener){
                           onDataReceiveListener.onDataReceive(buffer,size); //接收数据 ，java数组传递引用，在函数中更改buffer数据，也会造成原buffer中数据的修改
                       }
                   }
               }catch(IOException e){
                   e.printStackTrace();
               }
            }
        }
    }

    /**
     * 发送指令到串口
     *
     * @param cmd
     * @return
     */
    public boolean sendCmds(String cmd) throws UnsupportedEncodingException {
        boolean result=true;

        try{
            byte[] mBuffer=cmd.getBytes("UTF-8");
            if(mOutputStream!=null){
                mOutputStream.write(mBuffer);
            }else{
                result=false;
            }
        }catch(IOException e){
            e.printStackTrace();
            result=false;
        }
        return result;
    }

    /**
     * 发送字节
     * @param mBuffer
     * @return
     */
    public boolean sendBuffer(byte[] mBuffer){
        boolean result=true;
        try {
            if(mOutputStream!=null){
                mOutputStream.write(mBuffer);
            }else{
                result =false;
            }
        }catch (IOException e){
            e.printStackTrace();
            result=false;
        }
        return result;
    }

    public boolean sendBuffer(int[] mBuf){
        byte[]  mBuffer = BytesUtil.intToByte(mBuf);
        return sendBuffer(mBuffer);
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort(){
        isStop=true ;   //关闭报文接收线程
        parseSerialData.stopParseSerialData(); //关闭报文处理线程
        if(serialPort!=null){
            serialPort.close();
            serialPort=null;
        }
    }

    /**
     * 串口是否打开
     */
    public  boolean isSerialOpen(){
        if(serialPort!=null){
            return  serialPort.isSerialOpen();
        }
        return false;
    }


}






//数据接收接口对象
   /* private OnDataReceiveListener  tmpOnDataReceiveListener=new OnDataReceiveListener(){

        @Override
        public void onDataReceive(byte[] buffer, int size) {

            int [] receivedata= BytesUtil.byteToInt(buffer,size);
            //处理收到的串口数据，解析串口协议
            String str=new String(receivedata,0,size);

            Log.d(TAG, new String(buffer,0,size)); //打印
        }
    };
*/