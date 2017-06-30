package stonectr.serial.callBackEvent;

/**
 * Created by sunshine on 2017/6/24.
 */

public class UartGetFaceEvent extends UartBaseEvent {
    private int faceID;
    public void setFaceID(int id)
    {
        this.faceID = id;
    }
    public int getFaceID()
    {
        return faceID;
    }
}
