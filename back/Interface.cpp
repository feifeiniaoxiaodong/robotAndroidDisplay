//
// Created by fjx on 2018/2/3.
//
#include "Interface.h"

#include "android/log.h"
static const char* TAG = "Serial_Port_Interface";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)

/*
    description : create static serialPort operation agent.this var is a local var,
                  used just in this scope. fjx
    default params : "/dev/ttyS3",9600,8,1,'N'
*/
static SerialPort serialPort((char*)"/dev/ttyS3",9600,8,1,'N');


// add by fjx ---2018-2-3 14:39:32-----start
/*
 * Class:     stonectr_serial_SerialController
 * Method:    init
 * Signature: (Ljava/lang/String;IIII)V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_init
  (JNIEnv *env, jobject, jstring port, jint baudRate, jint dataBits, jint stopBits, jint parity)
  {
    char* tmp = jstringToChar(env, port);
    LOGI("Init_Port(jstring->char*):%s.\n",tmp);
    serialPort.set( tmp, baudRate, dataBits, stopBits, parity);
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    open
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_stonectr_serial_SerialController_open
  (JNIEnv *, jobject)
  {
    return serialPort.open();
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_close
  (JNIEnv *, jobject)
  {
    serialPort.close();
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    read
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_stonectr_serial_SerialController_read
  (JNIEnv *env, jobject, jbyteArray buf, jint size)
  {
    char* pData = (char*)env->GetByteArrayElements(buf, 0);
    char buffer[100] = {0};
    for(int i = 0;i < size;i++)
    {
        buffer[i] = pData[i];
        // 必须调用【env->SetByteArrayRegion(buf, 0,size,(const jbyte*)pData);】，否则【pData[i] = 199】不能成功。fjx-2018-03-03 13:17:44
        // pData[i] = 199;
        // LOGI("buffer[%d] = %d.\n",i,buffer[i]);
    }

    int count = serialPort.read(pData,size);
    for(int i = 0;i < count;i++)
    {
        LOGI("read (Interface.cpp %d bytes):%2X\n",count,pData[i]);
    }
    /*先获得数组的指针，通过指针修改，修改后再调用SetXXArrayRegion方法提交到Java数组里面（如果不提交的话，不会修改Java数组元素的值）。
      参考：http://blog.csdn.net/PZ0605/article/details/53010556
    */
    env->SetByteArrayRegion(buf, 0,size,(const jbyte*)pData);
    return count;
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    write
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_stonectr_serial_SerialController_write
  (JNIEnv *env, jobject, jbyteArray buf, jint size)
  {
    char* pData = (char*)env->GetByteArrayElements(buf,0);
    return serialPort.write(pData,size);
  }
/*
 * Class:     stonectr_serial_SerialController
 * Method:    isOpen
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_stonectr_serial_SerialController_isOpen
  (JNIEnv *, jobject)
  {
    return serialPort.isOpen();
  }
  
/*
 * Class:     stonectr_serial_SerialController
 * Method:    getString
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_stonectr_serial_SerialController_getString
  (JNIEnv * env, jobject,jstring jString)
  {
	// convert java string(jstring) to c string(char*)
	const char* tmp = jstringToChar(env, jString);
	LOGI("getString(jstring->char*):%s.\n",tmp);
	// convert c string(char*) to java string(jstring)
	char* cStr = (char*)"Hello i am from JNI!_fjx";
	jstring jStr =  env -> NewStringUTF(tmp);

	return jStr;
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    add
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_stonectr_serial_SerialController_add
  (JNIEnv *, jobject, jint x, jint y)
  {
    return serialPort.add(x,y);
  }
// add by fjx ---2018-2-3 14:39:32-----end



/*
 * Class:     stonectr_serial_SerialController
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_init__
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_release
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    openScanPort
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_openScanPort
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }


/*
 * Class:     stonectr_serial_SerialController
 * Method:    closeScanPort
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_closeScanPort
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    openWakePort
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_openWakePort
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    closeWakePort
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_closeWakePort
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    openROSPort
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_openROSPort
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    closeROSPort
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_closeROSPort
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    startWakeRcv
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_startWakeRcv
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    stopWakeRcv
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_stopWakeRcv
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    startScanRcv
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_startScanRcv
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    stopScanRcv
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_stopScanRcv
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    startRosRcv
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_startRosRcv
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    stopRosRcv
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_stopRosRcv
  (JNIEnv *, jclass)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendForward
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendForward__
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendBackward
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendBackward__
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendLeft
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendLeft__
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendRight
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendRight__
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendStop
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendStop
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendForward
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendForward__B
  (JNIEnv *, jobject, jbyte)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendBackward
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendBackward__B
  (JNIEnv *, jobject, jbyte)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendLeft
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendLeft__B
  (JNIEnv *, jobject, jbyte)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendRight
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendRight__B
  (JNIEnv *, jobject, jbyte)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    platformUp
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_platformUp
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    platformDown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_platformDown
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    platformStop
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_platformStop
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    platformBottom
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_platformBottom
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    moveTo
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_moveTo
  (JNIEnv *, jobject, jbyte)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    gotoScan
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_gotoScan
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    patrol
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_patrol
  (JNIEnv *, jobject, jbyte)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    navigation
 * Signature: ([B[B[B)V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_navigation
  (JNIEnv *, jobject, jbyteArray, jbyteArray, jbyteArray)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendMoveToLiving
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendMoveToLiving
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendMoveToBedroom
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendMoveToBedroom
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendMoveToYard
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendMoveToYard
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    sendPatrol
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_sendPatrol
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    voiceprintCheck
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_voiceprintCheck
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    voiceCheckSuccess
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_voiceCheckSuccess
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    voiceCheckFail
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_voiceCheckFail
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }

/*
 * Class:     stonectr_serial_SerialController
 * Method:    voiceChec2kFail
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_stonectr_serial_SerialController_voiceChec2kFail
  (JNIEnv *, jobject)
  {
    // current we didnot implement this body   ---fjx--2018-2-3 14:36:43
  }



