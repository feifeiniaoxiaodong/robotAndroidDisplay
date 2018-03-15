LOCAL_PATH       :=  $(call my-dir)
include              $(CLEAR_VARS)
LOCAL_MODULE     :=  SerialPort
LOCAL_SRC_FILES  :=  Interface.cpp SerialPort.cpp Util.cpp
LOCAL_LDLIBS     :=  -llog
include              $(BUILD_SHARED_LIBRARY)