package stonectr.serial.utils;

public class CRCVerify {

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
