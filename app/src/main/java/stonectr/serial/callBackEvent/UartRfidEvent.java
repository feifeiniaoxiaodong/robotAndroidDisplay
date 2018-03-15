package stonectr.serial.callBackEvent;

/**
 * Created by sunshine on 2017/7/12.
 */

public class UartRfidEvent extends UartBaseEvent {

    private double id;
    public double getId()
    {
        return id;
    }
    public void setId(double id)
    {
        this.id = id;
    }

}
