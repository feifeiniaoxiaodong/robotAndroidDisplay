//
// Created by fjx on 2018/2/3.
//

#ifndef ROCKPAD_ANDROID_SERIALPORT_H
#define ROCKPAD_ANDROID_SERIALPORT_H

//串口相关的头文件
#include<stdio.h>      /*标准输入输出定义*/
#include<stdlib.h>     /*标准函数库定义*/
#include<unistd.h>     /*Unix 标准函数定义*/
#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>      /*文件控制定义*/
#include<termios.h>    /*PPSIX 终端控制定义*/
#include<errno.h>      /*错误号定义*/
#include<string.h>
#include <pthread.h>

class SerialPort
{
	public:
		SerialPort():\
		port((char*)""),baudRate(115200),databits(8),stopbits(1),parity('N')
		{
		}
		SerialPort(char* _port,int _baudRate,int _databits,int _stopbits,int _parity):\
		port(_port),baudRate(_baudRate),databits(_databits),stopbits(_stopbits),parity(_parity)
		{

		}

		int open();
		void close();
		int read(char* rcv_buf,int length);
		int write(char* tx_buf,int length);
		int isOpen();
		void set(char* _port, int _baudRate = 115200,int _databits = 8,int _stopbits = 1,int _parity = 'N');

		int add(int x,int y);
	private:
		int fd;
		int baudRate;
		int stopbits;
		int databits;
		int parity;
		// point initial ,this should be considered paticularly.  fjx 2018-2-27 22:13:18
		char* port;
		char portBuf[20];
};

#endif //ROCKPAD_ANDROID_SERIALPORT_H
