//
// Created by leedonghun on 2020-01-16.
//
#include "com_example_leedonghun_speakenglish_ActivityForRoomImageChange.h"
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

//opencv를 이용한  이미지  edge detection
extern "C"
JNIEXPORT void JNICALL
Java_com_example_leedonghun_speakenglish_ActivityForRoomImageChange_edge
(JNIEnv *env, jobject instance,jlong inputimage,jlong outputimage, jint th1,jint th2) {

Mat &inputMat = *(Mat *) inputimage;
Mat &outputMat = *(Mat *) outputimage;

cvtColor(inputMat, outputMat, COLOR_RGB2GRAY);

Canny(outputMat, outputMat, th1, th2);

}//이미지 필터링  edge 효과끝


//이미지 필터링  -> 카툰
extern "C"
JNIEXPORT void JNICALL
Java_com_example_leedonghun_speakenglish_ActivityForRoomImageChange_cartoon
(JNIEnv *env, jobject instance,jlong inputimage,jlong outputimage, jint th1,jint th2) {


    Mat &inputMat = *(Mat *) inputimage;
    Mat &outputMat = *(Mat *) outputimage;


    cvtColor(inputMat, outputMat,COLOR_BGR2GRAY);
    medianBlur(outputMat,outputMat,15);

    adaptiveThreshold(outputMat,outputMat,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,15,2);




}//이미지 필터링 카툰 끝


//이미지 필터링 ->  흑백 효과
extern "C"
JNIEXPORT void JNICALL
Java_com_example_leedonghun_speakenglish_ActivityForRoomImageChange_gray
(JNIEnv *env, jobject instance,jlong inputimage,jlong outputimage, jint th1,jint th2) {


    Mat &inputMat = *(Mat *) inputimage;
    Mat &outputMat = *(Mat *) outputimage;
    cvtColor(inputMat,outputMat,COLOR_BGR2GRAY);



}//이미지 필터링 흑백 끝


//이미지 필터링 ->  horror효과
extern "C"
JNIEXPORT void JNICALL
Java_com_example_leedonghun_speakenglish_ActivityForRoomImageChange_horror
(JNIEnv *env, jobject instance,jlong inputimage,jlong outputimage, jint th1,jint th2) {


    Mat &inputMat = *(Mat *) inputimage;
    Mat &outputMat = *(Mat *) outputimage;
    cvtColor(inputMat,outputMat,COLOR_BGR2Luv);

}//이미지 필터링 horror 끝.
