package stonectr.serial.callBackEvent;

/**
 * Created by Sunshine on 2017/3/28.
 */

public class UartEventOther extends UartBaseEvent {

    private int commandType;
    private int msg1;
    private int msg2;

    public int getCommandType() {
        return this.commandType;
    }

    public int getMsg1() {
        return this.msg1;
    }

    public int getMsg2() {
        return this.msg2;
    }


    public UartEventOther(int commandType, int msg1, int msg2)
    {
        this.commandType = commandType;
        this.msg1 = msg1;
        this.msg2 = msg2;
    }
}
