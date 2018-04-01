package stonectr.serial.serialapply;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stonectr.serial.SerialController;
import stonectr.serial.utils.BytesUtil;

/**
 * Created by wei on 2018/3/17.
 */

public class ParseSerialData extends  Thread implements OnDataReceiveListener {

    private final String TAG="ParseSerialData";
    private volatile boolean isCountinue=true;
//    private IntBuffer intBuffer =IntBuffer.allocate(1024);
//    private
//    ArrayDeque<Integer>  arrayDeque= (ArrayDeque<Integer>) Collections.synchronizedCollection(new ArrayDeque<Integer>(1024));
//    Collections.synchronizedCollection(arra)

     List<Integer>  arrayList=(List<Integer> ) Collections.synchronizedList(new ArrayList<Integer> (1024));

    @Override
    public void onDataReceive(byte[] buffer, int size) {
        int [] revData= BytesUtil.byteToInt(buffer,size); //byte转化为int，暂时保存数据
//        List list = Arrays.asList(revData);
//        arrayDeque.addAll(arrayList);
//        arrayList.addAll(list);
        for(int i:revData){
            arrayList.add(i);
        }
    }


    @Override
    public void run() {

        while(isCountinue){
            while(arrayList.size()>3 ){

                if(arrayList.get(0) == 0x55 && arrayList.get(1)==0x02){   //Retrieves and removes

                    if(arrayList.get(2)==0x02 && arrayList.size()>=28){  //28字节
                        int[] msg28=new int[28];
                        for(int i=0;i<28;i++){
                            msg28[i]= arrayList.get(0); //取28字节
                            arrayList.remove(0);
                        }
                        //校验
                        if( HandleSerialData.isCRCOk(msg28,28)){
                            Double positionx= BytesUtil.parseDouble( BytesUtil.getNInts(msg28,3,8));   //位置数据，小端模式
                            Double positiony=BytesUtil.parseDouble( BytesUtil.getNInts(msg28, 11,8));
                            Double rotaion_yaml=BytesUtil.parseDouble(BytesUtil.getNInts(msg28,19,8));
                            SerialController.robotPose(positionx,positiony,rotaion_yaml);
                            Log.d(TAG ,"收到位置控制指令");
                            Log.d(TAG, "positionx:"+positionx+"positiony:"+positiony+ "rotation:"+rotaion_yaml);
                        }

                    }else if( (arrayList.get(2)==0x01 || arrayList.get(2)==0x05|| arrayList.get(2)==0x03||arrayList.get(2)==0x04)
                              && arrayList.size() >=15){ //15字节
                        int[] msg15=new int[15];
                        for(int i=0;i<15;i++){
                            msg15[i]= arrayList.get(0); //取15字节
                            arrayList.remove(0);
                        }
                        if(HandleSerialData.isCRCOk(msg15,15) ){
                            switch (msg15[2]){
                                case 0x05:  //RFID ,数据(big endian)uint_32
                                    long rfid =BytesUtil.parseUnint32( BytesUtil.reverseInts(BytesUtil.getNInts(msg15,3,4)) ,4);
                                    SerialController.getRfid(rfid);
                                    Log.d(TAG ,"收到RFID消息");
                                    break;
                                case 0x01:  //PM_Hum_Smoke_Volt
                                    int pm25= BytesUtil.parseUnint16( BytesUtil.reverseInts(BytesUtil.getNInts(msg15,3,2)));
                                    int pm10=BytesUtil.parseUnint16( BytesUtil.reverseInts(BytesUtil.getNInts(msg15,5,2)));
//                            int zero= revData[7]; //零上or零下
                                    int temperature= msg15[8]; //绝对温度
                                    int humidity=msg15[9] ; //湿度
                                    int smoke=BytesUtil.parseUnint16( BytesUtil.reverseInts(BytesUtil.getNInts(msg15,10,2))); //
                                    int voltage=msg15[12];//电压
                                    int direction=msg15[13];
                                    SerialController.robotInfo(pm25,pm10,temperature,(byte)humidity,smoke,(byte)voltage,(byte)direction);
                                    Log.d(TAG ,"收到环境数据"+ pm25+pm10+temperature+smoke);
                                    break;
                                case 0x03: // face detected
                                    break;
                                case 0x04: // scan_shelve
                                    break;
                                default:
                                {  Log.d(TAG ,"收到未知指令");}
                            }
                        }
                    } //size

                }else{ //如果连续两个字节都不符合，则删一个，继续检测
                    arrayList.remove(0);
                }//end 55  02

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }//end while

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }//end run


    public void stopParseSerialData(){
        this.isCountinue=false;
    }

}
