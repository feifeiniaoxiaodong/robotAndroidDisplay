//
// Created by fjx on 2018/2/3.
//

/************************Copyright(c)*******************************
**                       StoneAge
**                       Instritute of Intelligent Robotics Research Lab.
**------------------------------------------FileInfo-------------------------------------------------------
** File name:                 SerialPort.c
** Last modified Date:  	2018-1-4
** Last Version:              1.0
** Descriptions:
**------------------------------------------------------------------------------------------------------
** Created by:               fjx
** Created date:            2018-1-4
** Version:                  1.0
** Descriptions:             provide interface and implement of SerialPort operation
**------------------------------------------------------------------------------------------------------
** Modified by:
** Modified date:
** Version:
** Descriptions:
*******************************************************************/

#include "SerialPort.h"

#include "android/log.h"
static const char* TAG = "Serial_Port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)

//宏定义
#define FALSE  -1
#define TRUE   0

/*******************************************************************
* 名称：                  UARTX_Open
* 功能：                打开串口并返回串口设备文件描述
* 入口参数：        fd    :文件描述符     port :串口号(ttyS0,ttyS1,ttyS2)
* 出口参数：        正确返回为1，错误返回为0
*******************************************************************/
static int UARTX_Open(int fd,const char* port);
static void UARTX_Close(int fd);
static int UARTX_Set(int fd,int speed,int flow_ctrl,int databits,int stopbits,int parity);
static int UARTX_Init(int fd, int speed,int flow_ctrl,int databits,int stopbits,int parity);
static int UARTX_Recv(int fd, char *rcv_buf,int data_len);
static int UARTX_Send(int fd, char *send_buf,int data_len);
int SerialPort::add(int x,int y)
{
	return x+y;
}

int SerialPort::open()
{
	//printf("open_port:%s.",this->port);
	LOGI("open_port_name:%s.",this->port);
	fd = UARTX_Open(fd,this->port);
	LOGI("open_port_fd:%d.",fd);
	if(fd != -1)
	{
		UARTX_Init(fd,baudRate,0,databits,stopbits,parity);
	}
	return fd;
}
void SerialPort::close()
{
	UARTX_Close(fd);
	fd = -1;
}
int SerialPort::read(char* rcv_buf,int length)
{
	if(isOpen())
	{
		return UARTX_Recv(fd,rcv_buf,length);
	}
	else
	{
		return -1;
	}
}
int SerialPort::write(char* tx_buf,int length)
{
	if(isOpen())
	{
		return UARTX_Send(fd,tx_buf,length);
	}
	else 
	{
		return -1;
	}
}
// 0:false  1:true
int SerialPort::isOpen()
{
	return (fd != -1)?1:0;
}

void SerialPort::set(char* _port,int _baudRate,int _databits,int _stopbits,int _parity )
{
	this->baudRate = _baudRate;
	this->stopbits = _stopbits;
	this->databits = _databits;
	this->parity = _parity;
	char* p = this->portBuf;
	while((*p++ = *_port++) != '\0')
	{
		
	}
	this->port = this->portBuf;
	printf("set_port:%s.",this->port);

}


static int UARTX_Open(int fd,const char* port)
{

	fd = open( port, O_RDWR|O_NOCTTY|O_NDELAY);
	LOGI("(SerialPort.cpp.UARTX_Open)fd=%d",fd);
	if (FALSE == fd)
	{
	   LOGE("Can't Open Serial Port");
	   return(FALSE);
	}
     //恢复串口为阻塞状态
    if(fcntl(fd, F_SETFL, 0) < 0)
	{
		LOGE("fcntl failed!\n");
		return(FALSE);
	}
	else
	{
		LOGI("fcntl=%d\n",fcntl(fd, F_SETFL,0));
	}
      //测试是否为终端设备
	if(0 == isatty(STDIN_FILENO))
	{
		LOGE("standard input is not a terminal device\n");
		// now give up testing isatty. fjx 2018-03-03 11:28:42
		//return(FALSE);
	}
	else
	{
		LOGI("isatty success!\n");
	}
  LOGI("fd->open=%d\n",fd);
  return fd;
}
/*******************************************************************
* 名称：                UARTX_Close
* 功能：                关闭串口并返回串口设备文件描述
* 入口参数：        fd    :文件描述符     port :串口号(ttyS0,ttyS1,ttyS2)
* 出口参数：        void
*******************************************************************/

static void UARTX_Close(int fd)
{
    close(fd);
}

/*******************************************************************
* 名称：                UARTX_Set
* 功能：                设置串口数据位，停止位和效验位
* 入口参数：        fd        串口文件描述符
*                              speed     串口速度
*                              flow_ctrl   数据流控制
*                           databits   数据位   取值为 7 或者8
*                           stopbits   停止位   取值为 1 或者2
*                           parity     效验类型 取值为N,E,O,,S
*出口参数：          正确返回为1，错误返回为0
*******************************************************************/
static int UARTX_Set(int fd,int speed,int flow_ctrl,int databits,int stopbits,int parity)
{

    int   i;
    int   status;
    int   speed_arr[] = { B115200, B19200, B9600, B4800, B2400, B1200, B300};
    int   name_arr[] = {115200,  19200,  9600,  4800,  2400,  1200,  300};

    struct termios options;

    /*tcgetattr(fd,&options)得到与fd指向对象的相关参数，并将它们保存于options,该函数还可以测试配置是否正确，该串口是否可用等。若调用成功，函数返回值为0，若调用失败，函数返回值为1.
    */
    if  ( tcgetattr( fd,&options)  !=  0)
       {
          LOGI("SetupSerial 1");
          return(FALSE);
       }

    //设置串口输入波特率和输出波特率
    for ( i= 0;  i < sizeof(speed_arr) / sizeof(int);  i++)
                {
                     if  (speed == name_arr[i])
                            {
                                 cfsetispeed(&options, speed_arr[i]);
                                 cfsetospeed(&options, speed_arr[i]);
                            }
              }

    //修改控制模式，保证程序不会占用串口
    options.c_cflag |= CLOCAL;
    //修改控制模式，使得能够从串口中读取输入数据
    options.c_cflag |= CREAD;

    //设置数据流控制
    switch(flow_ctrl)
    {

       case 0 ://不使用流控制
              options.c_cflag &= ~CRTSCTS;
              break;

       case 1 ://使用硬件流控制
              options.c_cflag |= CRTSCTS;
              break;
       case 2 ://使用软件流控制
              options.c_cflag |= IXON | IXOFF | IXANY;
              break;
    }
    //设置数据位
    //屏蔽其他标志位
    options.c_cflag &= ~CSIZE;
    switch (databits)
    {
       case 5    :
                     options.c_cflag |= CS5;
                     break;
       case 6    :
                     options.c_cflag |= CS6;
                     break;
       case 7    :
                 options.c_cflag |= CS7;
                 break;
       case 8:
                 options.c_cflag |= CS8;
                 break;
       default:
                 fprintf(stderr,"Unsupported data size\n");
                 return (FALSE);
    }
    //设置校验位
    switch (parity)
    {
       case 'n':
       case 'N': //无奇偶校验位。
                 options.c_cflag &= ~PARENB;
                 options.c_iflag &= ~INPCK;
                 break;
       case 'o':
       case 'O'://设置为奇校验
                 options.c_cflag |= (PARODD | PARENB);
                 options.c_iflag |= INPCK;
                 break;
       case 'e':
       case 'E'://设置为偶校验
                 options.c_cflag |= PARENB;
                 options.c_cflag &= ~PARODD;
                 options.c_iflag |= INPCK;
                 break;
       case 's':
       case 'S': //设置为空格
                 options.c_cflag &= ~PARENB;
                 options.c_cflag &= ~CSTOPB;
                 break;
        default:
                 fprintf(stderr,"Unsupported parity\n");
                 return (FALSE);
    }
    // 设置停止位
    switch (stopbits)
    {
       case 1:
                 options.c_cflag &= ~CSTOPB; break;
       case 2:
                 options.c_cflag |= CSTOPB; break;
       default:
                       fprintf(stderr,"Unsupported stop bits\n");
                       return (FALSE);
    }

  //修改输出模式，原始数据输出
  options.c_oflag &= ~OPOST;

  options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);//我加的
//options.c_lflag &= ~(ISIG | ICANON);

    //设置等待时间和最小接收字符
    options.c_cc[VTIME] = 1; /* 读取一个字符等待1*(1/10)s */
    options.c_cc[VMIN] = 1; /* 读取字符的最少个数为1 */

    //如果发生数据溢出，接收数据，但是不再读取 刷新收到的数据但是不读
    tcflush(fd,TCIFLUSH);

    //激活配置 (将修改后的termios数据设置到串口中）
    if (tcsetattr(fd,TCSANOW,&options) != 0)
           {
               perror("com set error!\n");
              return (FALSE);
           }
    return (TRUE);
}
/*******************************************************************
* 名称：                UARTX_Init()
* 功能：                串口初始化
* 入口参数：        fd       :  文件描述符
*               speed  :  串口速度
*                              flow_ctrl  数据流控制
*               databits   数据位   取值为 7 或者8
*                           stopbits   停止位   取值为 1 或者2
*                           parity     效验类型 取值为N,E,O,,S
*
* 出口参数：        正确返回为1，错误返回为0
*******************************************************************/
static int UARTX_Init(int fd, int speed,int flow_ctrl,int databits,int stopbits,int parity)
{
    int err;
    //设置串口数据帧格式
    if (UARTX_Set(fd,speed,flow_ctrl,databits,stopbits,parity) == FALSE)
   {
	   return FALSE;
   }
    else
	{
	   return  TRUE;
	}
}

/*******************************************************************
* 名称：                  UARTX_Recv
* 功能：                接收串口数据
* 入口参数：        fd                  :文件描述符
*                              rcv_buf     :接收串口中数据存入rcv_buf缓冲区中
*                              data_len    :一帧数据的长度
* 出口参数：        正确返回为1，错误返回为0
*******************************************************************/
static int UARTX_Recv(int fd, char *rcv_buf,int data_len)
{
    int len,fs_sel;
    fd_set fs_read;

    struct timeval time;

    FD_ZERO(&fs_read);
    FD_SET(fd,&fs_read);

    time.tv_sec = 10;
    time.tv_usec = 0;

    //使用select实现串口的多路通信
    fs_sel = select(fd+1,&fs_read,NULL,NULL,&time);
    if(fs_sel)
    {
		len = read(fd,rcv_buf,data_len);
		//printf("I am right!(version1.2) len = %d fs_sel = %d\n",len,fs_sel);
		return len;
	}
    else
   {
		printf("Sorry,I am wrong!");
		return FALSE;
   }
}
/********************************************************************
* 名称：                  UARTX_Send
* 功能：                发送数据
* 入口参数：        fd                  :文件描述符
*                              send_buf    :存放串口发送数据
*                              data_len    :一帧数据的个数
* 出口参数：        正确返回为1，错误返回为0
*******************************************************************/
static int UARTX_Send(int fd, char *send_buf,int data_len)
{
    int len = 0;

    len = write(fd,send_buf,data_len);
    if (len == data_len )
	{
		return len;
	}
    else
	{

		tcflush(fd,TCOFLUSH);
		return FALSE;
	}

}


static void thread_Tx()
{
 /*
    int fd;                            //文件描述符
    int err;                           //返回调用函数的状态
    int len;
    int i;
    char send_buf[20]="tiger john";

    fd = UARTX_Open(fd,argv[1]); //打开串口，返回文件描述符
    do{
        err = UARTX_Init(fd,115200,0,8,1,'N');
        printf("Set Port Exactly!\n");
      }while(FALSE == err || FALSE == fd);


	for(i = 0;i < 10;i++)
	{
		len = UARTX_Send(fd,send_buf,10);
		if(len > 0)
			   printf(" %d send data successful\n",len);
		else
			   printf("send data failed!\n");

		sleep(1);
	}

	UARTX_Close(fd);

 */
}

static void thread_Rx()
{
/*
    int fd;                            //文件描述符
    int err;                           //返回调用函数的状态
    int len;
    int i;
    char rcv_buf[100];
    char send_buf[20]="tiger john";

    fd = UARTX_Open(fd,argv[1]); //打开串口，返回文件描述符

	while (1) //循环读取数据
	{
		len = UARTX_Recv(fd, rcv_buf,1);
		if(len > 0)
		{
			rcv_buf[len] = '\0';
		   //printf("receive data is %02X\n",(unsigned char)rcv_buf[0]);
		   printf("%02X ",(unsigned char)rcv_buf[0]);
			if((unsigned char)rcv_buf[0] == 0xED)
			{
				printf("\n");
			}
			//printf("len = %d\n",len);
		}
		else
		{
			printf("cannot receive data\n");
		}
		//sleep(1);
	}
	UARTX_Close(fd);
	*/
}
// Test code
int _main(int argc, char **argv);
int __main(int argc, char **argv)
{
	//(char*)"/dev/ttyUSB0"
	char* port;
	if(argc == 1)
	{
		port = (char*)"/dev/ttyS5";
		printf("default port num is %s",port);
	}
	else
	{
		port = argv[1];
	}
	SerialPort serialPort(port,115200,8,1,'N');
	serialPort.open();
	while(1)
	{
		static char buf[1] = {0x00};
		buf[0] += 1;
		if(FALSE == serialPort.write(buf,1))
		{
			printf("send error");
		}

		serialPort.read(buf,1);
		printf("%02X ",(unsigned char)buf[0]);
		if((unsigned char)buf[0] == 0xED)
		{
			printf("\n");
		}
	}
	_main(argc,argv);
}



int _main(int argc, char **argv)
{
    int fd;                            //文件描述符
    int err;                           //返回调用函数的状态
    int len;
    int i;
    char rcv_buf[100];
    char send_buf[20]="tiger john";
    if(argc != 3)
       {
              printf("Usage: %s /dev/ttySn 0(send data)/1 (receive data) \n",argv[0]);
              return FALSE;
       }
    fd = UARTX_Open(fd,argv[1]); //打开串口，返回文件描述符
    do{
                  err = UARTX_Init(fd,115200,0,8,1,'N');
                  printf("Set Port Exactly!\n");
				  printf("file descriptor :%d\n",fd);
       }while(FALSE == err || FALSE == fd);

    if(0 == strcmp(argv[2],"0"))
           {
                  for(i = 0;i < 10;i++)
                         {
                                len = UARTX_Send(fd,send_buf,10);
                                if(len > 0)
                                       printf(" %d send data successful\n",i);
                                else
                                       printf("send data failed!\n");

                                sleep(2);
                         }
                  UARTX_Close(fd);
           }
    else
           {

           while (1) //循环读取数据
                  {
                     len = UARTX_Recv(fd, rcv_buf,1);
                     if(len > 0)
                     {
                        rcv_buf[len] = '\0';
                       //printf("receive data is %02X\n",(unsigned char)rcv_buf[0]);
                       printf("%02X ",(unsigned char)rcv_buf[0]);
                        if((unsigned char)rcv_buf[0] == 0xED)
                        {
                            printf("\n");
                        }
			           //printf("len = %d\n",len);
                      }
                     else
                    {
                           printf("cannot receive data\n");
                    }
                     //sleep(1);
              }
       UARTX_Close(fd);
           }
    return 0;
}

/******************************End Of File***********************************/

