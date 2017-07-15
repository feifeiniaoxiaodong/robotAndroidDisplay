package com.example.qman.rockpad.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Qman on 2017/2/15.
 */

public class TimerUtil extends AppCompatActivity {
    public static Timer timer = null;//计时器
    public static TimerTask timerTask = null;
    /**
     * 开始自动减时
     */
    public static void startTime(final Activity activity,  long timeLength){
        if(timer==null){
            timer = new Timer();
        }
        timerTask = new TimerTask() {

            @Override
            public void run() {
                activity.finish();
                stopTime();
            }
        };
        timer.schedule(timerTask, timeLength);//1000ms执行一次
    }

    /**
     * 停止自动减时
     */
    public static void stopTime() {
        if(timer!=null)
            timer.cancel();
        timer = null;
    }
}
