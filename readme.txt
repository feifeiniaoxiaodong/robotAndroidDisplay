﻿2018.03.17; wei;  serial message test

    #报文发送
    navigation (2.3,3.6,12.3,30.5)
    serial send message :55 01 40 CD CC CC CC CC CC 0C 40 9A 99 99 99 99 99 28 40 00 00 00 00 00 80 3E 40 A8

    send forward message: 55 01 10 00 00 00 66

    send backward  msg:55 01 10 01 00 00 67

    send lift msg:55 01 10 03 00 00 69

    send right mag:55 01 10 02 00 00 68

    send top msg: 55 01 10 04 00 00 6A


    #报文接收
    姿态控制 len=28

    环境,len=15
    55 02 01 00 23 00 15 0A 02 10 00 14 23 02 25

    #串口粘连的数据环境数据和运动数据连在一起，不要随便改数据，累加和校验错误
    55 02 01 00 0D 00 15 00 16 15 01 31 00 00 D7 55 02 02 66 66 66 66 66 26 55 40 CD CC CC CC CC 2C 55 40 33 33 33 33 33 33 55 40 97

<<<<<<< HEAD

2018.03.29  dong yanwei

1、完成了串口驱动，native层实现了串口打开的关闭两个功能；串口读、写函数及其他基础功能移到的Java层；
2、完成串口接收和发送功能：
	串口信息接收：温湿度信息接收和显示； 机器人位置信息接收 ；RFID信息接收
	串口发送功能实现：机器人行走指令下发，前、后、左、右 ； 导航信息发送 x\y\r
3、 添加阿里云Mysql数据库操作函数，建立连接、增、删、改、查，只进行了测试，未使用






=======
2018.04.14 调试
    1、 添加语音唤醒功能，串口接收语音唤醒命令，串口地址: /dev/ttyS3   波特率：115200
    2、 温湿度和底盘控制串口号：/dev/ttyS0     波特率：115200
    3、 播放目录中的音频，iot3288开发版上目录地址：/mnt/internal_sd/Music/robot/
    4、
>>>>>>> musiconline









