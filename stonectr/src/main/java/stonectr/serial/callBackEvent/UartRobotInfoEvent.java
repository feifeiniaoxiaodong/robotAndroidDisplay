package stonectr.serial.callBackEvent;

/**
 * Created by Sunshine on 2017/5/12.
 */

public class UartRobotInfoEvent extends UartBaseEvent {
    private int pm25, pm10, temperature, smoke;
    private byte humidity, level;
    private boolean charging;

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getPm10() {
        return pm10;
    }

    public void setPm10(int pm10) {
        this.pm10 = pm10;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getSmoke() {
        return smoke;
    }

    public void setSmoke(int smoke) {
        this.smoke = smoke;
    }

    public byte getHumidity() {
        return humidity;
    }

    public void setHumidity(byte humidity) {
        this.humidity = humidity;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(int isCharging) {
        if (isCharging == 0)
            this.charging = false;
        else if (isCharging == 1)
            this.charging = true;
    }

}
