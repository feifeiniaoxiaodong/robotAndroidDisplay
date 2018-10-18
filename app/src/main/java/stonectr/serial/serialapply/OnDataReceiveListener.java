package stonectr.serial.serialapply;

/**
 * Created by wei on 2018/3/9.
 */

public interface OnDataReceiveListener {
    public void onDataReceive(byte[] buffer,int size); //在接收函数中解析收到的数据
}
