package com.example.qman.rockpad.dao.dbcommand;

/**
 * Created by wei on 2018/3/16.
 */

public class EnvParam {
    String id;
    String rid;     //机器人序号
    String time;      //时间值换算为小时， 12:34:21=str(12+34/60+21/3600)="12.5725"
    String humidity;
    String temperature;
    String pm10;
    String pm25;
    String smoke;

    public EnvParam(String id, String rid, String time, String humidity, String temperature, String pm10, String pm25, String smoke) {
        this.id = id;
        this.rid = rid;
        this.time = time;
        this.humidity = humidity;
        this.temperature = temperature;
        this.pm10 = pm10;
        this.pm25 = pm25;
        this.smoke = smoke;
    }

    public EnvParam() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }
}
