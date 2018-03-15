package stonectr.serial;


import stonectr.serial.callBackEvent.UartBaseEvent;

/**
 * Created by Sunshine on 2017/3/22.
 */

public interface ControlCallBack {
       void onReback(UartBaseEvent event);
}
