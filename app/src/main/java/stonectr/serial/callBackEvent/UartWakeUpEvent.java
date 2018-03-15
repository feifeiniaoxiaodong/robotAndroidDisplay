package stonectr.serial.callBackEvent;

/**
 * Created by Sunshine on 2017/4/7.
 */

public class UartWakeUpEvent extends UartBaseEvent {
    private int angle;
    public UartWakeUpEvent(int angle)
    {
       this.angle = angle;
    }
    public int getAngle()
    {
        return this.angle;
    }
}
