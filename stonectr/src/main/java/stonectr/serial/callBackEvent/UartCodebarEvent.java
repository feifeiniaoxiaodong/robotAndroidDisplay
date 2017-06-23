package stonectr.serial.callBackEvent;

/**
 * Created by Sunshine on 2017/5/10.
 */

public class UartCodebarEvent extends UartBaseEvent {
    private boolean is2DBar ;
    private String bar;
    public void setIs2DBar(boolean is2DBar)
    {
        this.is2DBar = is2DBar;
    }
    public boolean getIs2DBar()
    {
        return is2DBar;
    }
    public void setBar(String bar)
    {
        this.bar = bar;
    }
    public String getBar()
    {
        return bar;
    }
}

