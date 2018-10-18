package stonectr.serial.utils;

import android.util.Log;

/**
 * Created by wei on 2018/3/9.
 */

public class BytesUtil {
    private static final String TAG="BytesUtil...";
    /**
     *     int[]  to byte[] 相互转化函数
     */
    public static int[]   byteToInt(byte[]  bytes){
        int size =bytes.length;
        int[] ints=new int[size];
        for(int i=0;i<size;i++){
            ints[i]= bytes[i]& 0xff;
        }
        return ints;
    }

    public static int[]   byteToInt(byte[]  bytes,int n){
        int size =n;
        int[] ints=new int[size];
        for(int i=0;i<size;i++){
            ints[i]= bytes[i]& 0xff;
        }
        return ints;
    }

    /**
     *  byte[] to int[] 相互转化
     */
    public static byte[] intToByte(int[] ints){
        int size=ints.length;
        byte [] bytes= new byte[size];
        for(int i=0;i<size;i++){
            bytes[i] =(byte) ints[i];
        }
        return bytes;
    }


    public static void printData(int [] datas){
        String str ="";
        for(int i:datas){
            str+= i;
        }
        Log.d("Receive Serival Data",str);
    }

    public  static double parseDouble(int[] dd )  {
        long a=0;
        if(dd.length !=8)  {
            Log.d(TAG,"转化为double,字节长度不等于8");
            return 0;
        }
        for(int i=0;i<8;i++){
            a |= ((dd[i]&0xffL)<< (i*8));
        }
        return Double.longBitsToDouble(a);
    }

    //从指定位置取制定长度的数组
    public static int[] getNInts(int[] dd,int src,int len){
        int [] desc =new int[len];
        System.arraycopy(dd,src,desc,0,len);
        return desc;
    }

    public static int parseUnint16(int [] dd){
        return   (dd[0]&0xff) | ((dd[1]&0xff)<<8);
    }

    //由字节数组解析unint数值 ，len<=4
    public static long parseUnint32(int[] dd,int len){
        long ss=0;
        if(len>4){
            Log.d(TAG,"parseunint,数组超长");
        }
        for(int i=0;i<len;i++){
            ss |= (dd[i] &0xff)<<(8*i) ;
        }
        return ss;
    }

    //数组反转
    public static int[]  reverseInts(int [] dd){
        int tmp;
        for(int i=0; i< dd.length /2;i++ ){
            tmp=dd[i];
            dd[i]=dd[dd.length-1-i] ;
            dd[dd.length-1-i]=tmp;
        }
        return dd;
    }

    //把double类型的数据转化8
    public static byte[] parseDoubleToBytes(double x){

        long longdouble= Double.doubleToLongBits(x);
        byte [] tt= new byte[8];
        for(int i=0;i<8;i++){
            tt[i] = (byte) (( longdouble>>(8*i))&0xff);
        }
        return tt;
    }

    //把double类型的数据转化8
    public static int[] parseDoubleToInts(double x){

        long longdouble= Double.doubleToLongBits(x);
        int [] tt= new int[8];
        for(int i=0;i<8;i++){
            tt[i] = (int) (( longdouble>>(8*i))&0xff);
        }
        return tt;
    }

}
