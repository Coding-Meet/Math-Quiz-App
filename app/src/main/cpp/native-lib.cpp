#include <jni.h>




extern "C"
JNIEXPORT jstring JNICALL
Java_com_coding_meet_mathquizapp_util_SecurityManger_00024Keys_Secretkey(JNIEnv *env,
                                                                         jobject thiz) {
    return env ->NewStringUTF("750GdiCY1GWXzRjH2W9esuGDGAzNNvbVbxfAAAN65mt5WrZwlz49KgpYzFhLQejd");
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_coding_meet_mathquizapp_util_SecurityManger_00024Keys_ALGORITHM(JNIEnv *env,
                                                                         jobject thiz) {
    return env ->NewStringUTF("AAEBQAwAAAAQAAAAAwAAALvnpx7c0XDQrRduHM7PiEqcMD/GsEc/9x8iSXfYibg=");

}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_coding_meet_mathquizapp_util_SecurityManger_00024Keys_TRANSFORMATION(JNIEnv *env,
                                                                              jobject thiz) {
    return env ->NewStringUTF("AAEBQAwAAAAQAAAAFAAAAMcSLgMkvQiCuBcuEdPNm6xoz2MQKFePFqxNIaNJqT966t6XjQ1kk7c/mXpGRXmQQA==");

}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_coding_meet_mathquizapp_util_SecurityManger_00024Keys_KEYPASSWORD(JNIEnv *env,
                                                                           jobject thiz) {
    return env ->NewStringUTF("AAEBQAwAAAAQAAAAEAAAAFsL9Ue1xFVrtHAiL+HWDMKDVEiLNbWSuJHiNSu9Dx66lTU/v3nrF8R+tgDH");

}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_coding_meet_mathquizapp_util_SecurityManger_00024Keys_IVPASSWORD(JNIEnv *env,
                                                                          jobject thiz) {
    return env ->NewStringUTF("AAEBQAwAAAAQAAAAEAAAAPoXst4aNssK95ZMZ/+sxlJH2fMAD5gvlPpVAr1IEuC4IaW5maom1jg1/LmH");

}