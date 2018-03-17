package com.example.qman.rockpad.test;

import com.example.qman.rockpad.dao.DBService;
import com.example.qman.rockpad.dao.dbcommand.EnvParam;

/**
 * Created by wei on 2018/3/17.
 */

public class MysqlTestThread implements  Runnable {

    @Override
    public void run() {

        DBService.getInstence().openConnection();

//        DBService.getInstence().selectRockmoveById("1");
//
//        DBService.getInstence().selectRocklightById("1");

        EnvParam envParam=new EnvParam("2","3","2018-03-17 11:15:01" ,"56","20","ds","56","42");

//        DBService.getInstence().insert(envParam);
//        DBService.getInstence().selectEnvParam("2");

        DBService.getInstence().selectRockmoveById("1");

        DBService.getInstence().selectRockmoveById("2");

        DBService.getInstence().selectRockmoveById("3");


    }
}
