//
// Created by leedonghun on 2019-08-21.
//
#include "com_example_leedonghun_speakenglish_FaceDetectionAlbum.h"
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

float resize(Mat img_src, Mat &img_resize, int resize_width){

    float scale = resize_width / (float)img_src.cols ;

    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    }
    else {
        img_resize = img_src;
    }
    return scale;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_leedonghun_speakenglish_FaceDetectionAlbum_loadCascade(JNIEnv *env,
                                                          jobject instance,
                                                          jstring cascadeFileName_) {

//    const char *cascadeFileName = env->GetStringUTFChars(cascadeFileName_, 0);
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);
    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();
    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);

    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    }else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);
    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);
    return ret;
//    env->ReleaseStringUTFChars(cascadeFileName_, cascadeFileName);
}

extern "C"

JNIEXPORT jint JNICALL
Java_com_example_leedonghun_speakenglish_FaceDetectionAlbum_detect(JNIEnv *env,jobject instance,jlong cascadeClassifier_face,
                                                     jlong cascadeClassifier_eye,
                                                     jint a,
                                                     jlong matAddrInput,
                                                     jlong matAddrResult) {


    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    int ret = 0;


    img_result = img_input.clone();
    std::vector <Rect> faces;
    Mat img_gray;
    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);
    Mat img_resize;
    //flip(img_gray, img_resize, 0); //영상 좌우 반전하기
    float resizeRatio = resize(img_gray, img_resize, 100);
    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale(img_resize, faces, 1.1, 2,
                                                                     0 | CASCADE_SCALE_IMAGE,
                                                                     Size(10, 10));
    // __android_log_print(ANDROID_LOG_DEBUG, (char *) "native-lib :: ",
    //                  (char *) "face %d found ", faces.size());
    ret = faces.size();


    if (a == 1) {
        for (int i = 0; i < faces.size(); i++) {
            double real_facesize_x = faces[i].x / resizeRatio;
            double real_facesize_y = faces[i].y / resizeRatio;
            double real_facesize_width = faces[i].width / resizeRatio;
            double real_facesize_height = faces[i].height / resizeRatio;

            Point center(real_facesize_x + real_facesize_width / 2,
                         real_facesize_y + real_facesize_height / 2);
            ellipse(img_result, center, Size(real_facesize_width / 2, real_facesize_height / 2), 0,
                    0, 360,
                    Scalar(255, 0, 255), 30, 8, 0);

            Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width,
                           real_facesize_height);
            Mat faceROI = img_gray(face_area);
            std::vector <Rect> eyes;


            //-- In each face, detect eyes

        }
    }
    return ret;

}
