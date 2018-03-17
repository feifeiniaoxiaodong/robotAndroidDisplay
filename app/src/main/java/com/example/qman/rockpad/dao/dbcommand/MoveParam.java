package com.example.qman.rockpad.dao.dbcommand;

/**
 * Created by wei on 2018/3/16.
 */

public class MoveParam {
    String id;  //按数据库插入顺序递增
    String rid; //机器人序号
    int   w ; //角速度,cmd_vel.angular.z=int(w)*0.006
    int    v; //线速度, cmd_vel.linear.x=int(v)*0.003

    public MoveParam() {
    }



    public MoveParam(String id, String rid, int w, int v) {
        this.id = id;
        this.rid = rid;
        this.w = w;
        this.v = v;
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

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }
}
