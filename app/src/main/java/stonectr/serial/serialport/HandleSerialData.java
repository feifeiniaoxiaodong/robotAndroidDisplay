package stonectr.serial.serialport;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import stonectr.serial.SerialController;

/**
 * Created by wei on 2018/3/9.
 * 处理串口接收数据
 * 解析串口报文
 */
public class HandleSerialData implements OnDataReceiveListener {
    private final String TAG="HandleSerialData";

    private IntBuffer  intBuffer =IntBuffer.allocate(1024);

    int offset=0;
    @Override
    public void onDataReceive(byte[] buffer, int size) {
        //处理收到的串口数据，解析串口协议
        //默认为一条完整报文
        int [] revData= BytesUtil.byteToInt(buffer,size); //byte转化为int，暂时保存数据
        BytesUtil.printData(revData); //打印数据
//        intBuffer.put(receivedata);

        if(revData[0] == 0x55 && revData[1] ==0x02 ){
            if(revData[2]==0x02 && size ==28 && isCRCOk(revData,28)){  //28字节报文，机器人位置，小端数据
//                if(isCRCOk(revData,28)){  //CRC校验
                    Double positionx= BytesUtil.parseDouble( BytesUtil.getNInts(revData,3,8));   //小端模式
                    Double positiony=BytesUtil.parseDouble( BytesUtil.getNInts(revData, 11,8));
                    Double rotaion_yaml=BytesUtil.parseDouble(BytesUtil.getNInts(revData,19,8));
                    SerialController.robotPose(positionx,positiony,rotaion_yaml);
                     Log.d(TAG ,"收到位置控制指令");
                    Log.d(TAG, "positionx:"+positionx+"positiony:"+positiony+ "rotation:"+rotaion_yaml);

//                }
            }else if(size==15 && isCRCOk(revData, 15) ){  //15字节报文
//                if (isCRCOk(revData, 15)) {  //CRC校验
                    switch (revData[2]){
                    case 0x05:  //RFID ,数据(big endian)uint_32
                        long rfid =BytesUtil.parseUnint32( BytesUtil.reverseInts(BytesUtil.getNInts(revData,3,4)) ,4);
                        SerialController.getRfid(rfid);
                        Log.d(TAG ,"收到RFID消息");
                        break;
                    case 0x01:  //PM_Hum_Smoke_Volt
                        int pm25= BytesUtil.parseUnint16( BytesUtil.reverseInts(BytesUtil.getNInts(revData,3,2)));
                        int pm10=BytesUtil.parseUnint16( BytesUtil.reverseInts(BytesUtil.getNInts(revData,5,2)));
//                            int zero= revData[7]; //零上or零下
                        int temperature= revData[8]; //绝对温度
                        int humidity=revData[9] ; //湿度
                        int smoke=BytesUtil.parseUnint16( BytesUtil.reverseInts(BytesUtil.getNInts(revData,10,2))); //
                        int voltage=revData[12];//电压
                        int direction=revData[13];
                        SerialController.robotInfo(pm25,pm10,temperature,(byte)humidity,smoke,(byte)voltage,(byte)direction);
                        Log.d(TAG ,"收到环境数据");
                        break;
                    case 0x03: // face detected
                        break;
                    case 0x04: // scan_shelve
                        break;
                    default:
                    {  Log.d(TAG ,"收到未知指令");}
                }
            }else{
                Log.d(TAG,"报文长度或CRC校验不通过");
            }
        }else{
            //数据头不符
            Log.d(TAG ,"报文头不符");
        }
    }

    /**
     * 获取CRC校验码
     * 校验方式：n个要校验的数做和，去最后一个字节表示的无符号数值
     * @param revData:需要校验数据数组
     * @param n：要校验数据个数
     * @return 校验码
     */
    public static int getCRCNum(int[] revData,int n){
        int sum=0;
        int crcnum=0; //校验码
        for(int i=0;i<n;i++){
            sum+=revData[i];
        }
        crcnum = sum & 0xff;
        return crcnum;
    }

    /**
     * 校验是否通过
     * @param revData：
     * @param size：数据长度
     * @return  ok=true ;else =false
     */
    public static  boolean isCRCOk(int [] revData,int size){
        if(revData[size-1] == getCRCNum(revData,size-1)){
            return true;
        }
        return false;
    }


}
