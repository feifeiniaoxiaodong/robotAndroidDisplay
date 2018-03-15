2018年2月27日19:52:24    fjx

SerialPort.cpp
SerialPort.h
	是linux平台上操作串口的Cpp源文件，实现了对串口的本地操作，以类的形式提供了串口的操作。
stonectr_serial_SerialController.h
	是java生成的头文件
Interface.cpp
Interface.h
	是由SerialPort.cpp 和 stonectr_serial_SerialController.h对串口操作的JNI 实现。
Util.cpp
Util.h
	是辅助文件，实现一些通用的功能函数。