2018/03/17; wei;  serial message test

    #报文发送有待验证
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

