package com.example.qman.rockpad.service;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


import com.example.qman.rockpad.application.StoneRbtApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import stonectr.serial.SerialController;

public class MobileMsgHandler {

    private boolean rcvFlag = false;
    private SerialController serialController = SerialController.getInstance();
    private String remoteIP, localIP;
    private Context context;
    private static MobileMsgHandler mobileMsgHandler;

    public static MobileMsgHandler getInstance()
    {
        if(mobileMsgHandler == null)
        {
            mobileMsgHandler = new MobileMsgHandler();
        }
        return mobileMsgHandler;
    }

    private MobileMsgHandler() {
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public void setRemoteIP(String ip)
    {
        this.remoteIP = ip;
        //将本地地址传递给手机
        Log.d("remote ip ", "setRemoteIP: " + ip);
        new SocketSendThread("ip " + localIP).start();
    }
    /**
     * 获取系统IP
     */
    public void getSystemIP()
    {
        WifiManager wifiManager = (WifiManager) StoneRbtApp.getApplication().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            //ipShow.setText("当前系统wifi未开启，准备开启WiFi");
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        localIP = intToIp(ipAddress);
    //    ipShow.setText("当前系统IP为" + ip);
    }
    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    private void sendTestBroadcast(String str)
    {
        Intent intent = new  Intent();
        intent.setAction("com.stoneRbt.uartBroadcast");
        intent.putExtra("info","voice_test");
        intent.putExtra("sort",str);
        //发送无序广播
        context.sendBroadcast(intent);
    }

    private void playMsg(String msgInfo) {
//        Intent intent = new Intent(this, TTSTool2.class);
//        intent.putExtra("test", msgInfo);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        getApplication().startActivity(intent);
    }

    public void voiceParse()
    {

    }

    /**
     * 向手机端发送消息
     */

    public void sendMsg( String msg)
    {
        if (remoteIP != null)
            new SocketSendThread(msg).start();
    }

    public void sendRbtInfoMsg(String msg)
    {
        if (remoteIP != null)
            new SocketSendThread(msg).start();
        else
            Log.d("remote ip error", "remote ip is null");
    }

    class SocketSendThread extends Thread
    {
        String msg;
        public SocketSendThread(String msg)
        {
            this.msg = msg;
        }
        @Override
        public void run()
        {
            Socket socket;
            OutputStream ou;
            try {
                socket = new Socket(remoteIP,3200);
                ou = socket.getOutputStream();
                BufferedReader br =new BufferedReader(new InputStreamReader(socket.getInputStream())) ;
                ou.write(msg.getBytes("gbk"));
                String line=br.readLine();
                ou.flush();
                ou.close();
                br.close();
                socket.close();
             //   String line=br.readLine();
          //      Log.d("get ", line);
            } catch (IOException e) {
                Log.d("remote", "remote  ERROR");
                e.printStackTrace();
            }
        }
    }
    /**
     * 开启接收手机端信号线程
     */
    public void startMobileRcv()
    {
        if (!rcvFlag)
        {
            rcvFlag = true;
            new Thread(rcvThread).start();
        }
        else
        {
            Log.d("mobile receive thread", "mobile receive thread is running...");
        }
    }

    public void stopMobileRcv()
    {
        rcvFlag = false;
    }

    /**
     * 接受手机端线程
     */
    Runnable rcvThread = new Runnable()
    {
        @Override
        public void run()
        {
            try {
                ServerSocket serivce = new ServerSocket(3200);
                Log.d("socket rcv", "rcv start ");
                while (rcvFlag) {
                    Socket socket = serivce.accept();
                    OutputStream ou = socket.getOutputStream();
                    BufferedReader br =new BufferedReader(new InputStreamReader(socket.getInputStream())) ;
                    String line = null;
                    ou.write("got it!".getBytes("gbk"));
                    ou.flush();
                    //半关闭socket
                    socket.shutdownOutput();
                    //获取客户端的信息
                    while ((line = br.readLine()) != null)
                    {
                        mobileMsgParse(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 解析手机端信号
     * @param msg
     */
    private void mobileMsgParse(String msg)
    {
        String[] s = msg.split(" ");
        if (s[0].equals("move"))
        {
            //执行移动指令
            if (s[1].equals("forward"))
            {
              //  msgShow.setText("获得向前指令");
               Log.d("get Mobile msg ","获得向前指令");
                serialController.sendForward();
            }
            else if (s[1].equals("backward"))
            {
               // msgShow.setText("获得向后指令");
                Log.d("get Mobile msg ","获得向后指令");
                serialController.sendBackward();
            }
            else if (s[1].equals("left"))
            {
               // msgShow.setText("获得向左指令");
                serialController.sendLeft();
            }
            else if (s[1].equals("right"))
            {
               // msgShow.setText("获得向右指令");
                serialController.sendRight();
            }
            else if (s[1].equals("up"))
            {
                serialController.platformUp();
            }
            else if (s[1].equals("down"))
            {
                serialController.platformDown();
            }
            else if (s[1].equals("bottom"))
            {
                serialController.platformBottom();
            }
            else if (s[1].equals("stop"))
            {
                serialController.platformStop();
            }
        }
        else if(s[0].equals("ctr"))
        {
            //msgShow.setText("获得控制方式切换指令");
        }
        else if(s[0].equals("voice_test"))
        {
            switch (s[1])
            {
                case "0" :
                    sendTestBroadcast("0");
                    Log.d("test", "mobileMsgParse:  wake");
                    break;
                case "1" :
                    sendTestBroadcast("1");
                    break;
                case "2" :
                    sendTestBroadcast("2");
                    break;
                case "3" :
                    sendTestBroadcast("3");
                    break;
                case "4" :
                    sendTestBroadcast("4");
                    break;
                case "5" :
                    sendTestBroadcast("5");
                    break;
                case "6" :
                    sendTestBroadcast("6");
                    break;
            }
        }
    }
}
