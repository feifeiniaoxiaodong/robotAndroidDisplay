package stonectr.serial.callBackEvent;

/**
 * Created by sunshine on 2017/6/25.
 */

public class UartShelvesInfoEvent extends UartBaseEvent {

    private int top, middle;

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getMiddle() {
        return middle;
    }

    public void setMiddle(int middle) {
        this.middle = middle;
    }

}
