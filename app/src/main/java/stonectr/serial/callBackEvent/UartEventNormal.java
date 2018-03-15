package stonectr.serial.callBackEvent;

/**
 * Created by Sunshine on 2017/3/28.
 */

public class UartEventNormal extends UartBaseEvent {
    private int commandNum;
    public UartEventNormal(int num)
    {
        this.commandNum = num;
    }
    public int getCommandNum()
    {
        return this.commandNum;
    }
}
