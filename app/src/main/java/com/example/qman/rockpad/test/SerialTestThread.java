package com.example.qman.rockpad.test;

import com.example.qman.rockpad.dao.dbcommand.EnvParam;

import stonectr.serial.SerialController;

/**
 * Created by wei on 2018/3/17.
 */

public class SerialTestThread implements Runnable {

    public SerialTestThread() {
    }


    @Override
    public void run() {


        //测试串口发送报文命令
        //在SerialService中已经初始化了串口，这里直接使用串口发送即可

        SerialController.getInstance().navigation(2.3,3.6,12.3,30.5);//导航

        SerialController.getInstance().sendForward();

        SerialController.getInstance().sendBackward();

        SerialController.getInstance().sendLeft();

        SerialController.getInstance().sendRight();

        SerialController.getInstance().sendStop();


        /*new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();*/

    }
}
