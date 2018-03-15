//
// Created by fjx on 2018/2/3.
//

#ifndef ROCKPAD_ANDROID_UTIL_H
#define ROCKPAD_ANDROID_UTIL_H
#include <jni.h>
#include<stdlib.h>
#include<string.h>
char* jstringToChar(JNIEnv* env, jstring jstr);
jstring charTojstring(JNIEnv* env, const char* pat);
int addxy(int x,int y);

#endif //ROCKPAD_ANDROID_UTIL_H
