#include <memory.h>
#include <malloc.h>
#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
//#include "jbig.h"
#import "jbig.h"
#include "com_android_jbig_JBigConvert.h"
#include <stdio.h>
#include <pthread.h>
//#include "touch_com.h"

#define TOUCH_COM_MAX_ENCSIGNSIZE 4096*4
#define TOUCH_COM_MAX_ORGSIGNSIZE 4096*4

#ifndef eprintf

#define eprintf(...) __android_log_print(ANDROID_LOG_ERROR,"@",__VA_ARGS__)

#endif


#define RGB565_R(p) ((((p) & 0xF800) >> 11) << 3)

#define RGB565_G(p) ((((p) & 0x7E0 ) >> 5)  << 2)

#define RGB565_B(p) ( ((p) & 0x1F  )        << 3)

#define MAKE_RGB565(r, g, b) ((((r) >> 3) << 11) | (((g) >> 2) << 5) | ((b) >> 3))


#define RGBA_A(p) (((p) & 0xFF000000) >> 24)

#define RGBA_R(p) (((p) & 0x00FF0000) >> 16)

#define RGBA_G(p) (((p) & 0x0000FF00) >>  8)

#define RGBA_B(p)  ((p) & 0x000000FF)

#define MAKE_RGBA(r, g, b, a) (((a) << 24) | ((r) << 16) | ((g) << 8) | (b))

#ifdef __cplusplus
extern "C" {
#endif
static unsigned char *buffer;
static unsigned char *enc_buf;
static unsigned long enc_len;

void output_bie(unsigned char *start, size_t len, void *file) {
    if (enc_len + len < TOUCH_COM_MAX_ENCSIGNSIZE) {
        memcpy(enc_buf + enc_len, start, len);
    }
    enc_len += len;
}

void JBIG_Compressing(unsigned long ulX, unsigned long ulY,
                      unsigned char *pucInBuf, unsigned long ulInLen,
                      unsigned char *pucOutBuf, unsigned long *pulOutLen) {
//    eprintf("original C Code: %s\n", "enter JBitConvertCompress...");
    unsigned char *bitmaps[1];
    struct jbg_enc_state *se;
    //FILE *fout = stdout;
    void *fout = NULL;

    buffer = malloc(TOUCH_COM_MAX_ORGSIGNSIZE);
    memset(buffer, 0, sizeof(TOUCH_COM_MAX_ORGSIGNSIZE));
    memcpy(buffer, pucInBuf, ulInLen);
    bitmaps[0] = buffer;
    //bitmaps[0] = pucInBuf;

    enc_buf = malloc(TOUCH_COM_MAX_ENCSIGNSIZE);
    memset(enc_buf, 0, sizeof(TOUCH_COM_MAX_ENCSIGNSIZE));
    enc_len = 0;

    se = (struct jbg_enc_state *) malloc(sizeof(struct jbg_enc_state));
    memset(se, 0, sizeof(struct jbg_enc_state));

    /* initialize encoder */
    jbg_enc_init(se, ulX, ulY, 1, bitmaps, output_bie, fout);

    /* encode image */
    jbg_enc_out(se);

    if (enc_len < TOUCH_COM_MAX_ENCSIGNSIZE) {
        memcpy(pucOutBuf, enc_buf, enc_len);
        *pulOutLen = enc_len;
    } else {
        *pulOutLen = 0;
    }

    /* release allocated resources */
    jbg_enc_free(se);

    free(buffer);
    free(enc_buf);
    free((unsigned char *) se);
}

void JBIG_Decompressing(unsigned char *pucInBuf, unsigned long ulInLen,
                        unsigned char *pucOutBuf, unsigned long *pulOutLen) {
    eprintf("original C Code: %s\n", "enter JBitConvertDecompress...");
    unsigned char ucResult = 0;
    int i = 0;
    struct jbg_dec_state *sd;

    sd = (struct jbg_dec_state *) malloc(sizeof(struct jbg_dec_state));
    memset(sd, 0, sizeof(struct jbg_dec_state));

    jbg_dec_init(sd);

    ucResult = jbg_dec_in(sd, pucInBuf, ulInLen, NULL);
    if (ucResult == JBG_EOK) {
        unsigned long width = jbg_dec_getwidth(sd);
        unsigned long height = jbg_dec_getheight(sd);
        int planes = jbg_dec_getplanes(sd);
        *pulOutLen = jbg_dec_getsize(sd);
        for (i = 0; i < planes; i++) {    // // 这里 planes 必须为 1
            unsigned char *data = jbg_dec_getimage(sd, i);
            memcpy(pucOutBuf, data, *pulOutLen);
        }
    }

    jbg_dec_free(sd);

    free((unsigned char *) sd);
}

/**
 * Char*转JByteArray
 */
jbyteArray ConvertCharToJByteArray(JNIEnv *jniEnv, char *pBuf, int outLen) {
    jbyteArray data = (*jniEnv)->NewByteArray(jniEnv, outLen);
//    eprintf("Jni-DC: %s,%d\n", "return char data length...", outLen);
    (*jniEnv)->SetByteArrayRegion(jniEnv, data, 0, outLen, (const jbyte *) pBuf);//使用数据
    jsize len = (*jniEnv)->GetArrayLength(jniEnv, data);
//    eprintf("Jni-DC: %s,%d\n", "return jbyteArray data length...", len);
//    (*jniEnv)->DeleteLocalRef(jniEnv, data);
    return data;
}

/**
 * JByteArray转Char*
 */
unsigned char *ConvertJByteArrayToChars(JNIEnv *env, jbyteArray byteArray) {
    unsigned char *chars = NULL;
    jbyte *bytes;
    bytes = (*env)->GetByteArrayElements(env, byteArray, 0);
    int chars_len = (*env)->GetArrayLength(env, byteArray);
    chars = (unsigned char *) malloc((size_t) (chars_len + 1));
    memset(chars, 0, (size_t) (chars_len + 1));
    memcpy(chars, bytes, (size_t) chars_len);
    chars[chars_len] = 0;
    (*env)->ReleaseByteArrayElements(env, byteArray, bytes, 0);
    return chars;
}
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
/*
 * Class:     com_android_jbig_JBigConvert
 * Method:    decompressing
 * Signature: (Landroid/graphics/Bitmap;[B)I
 */
JNIEXPORT jbyteArray JNICALL Java_com_android_jbig_JBigConvert_decompressing
        (JNIEnv *env, jobject obj, jbyteArray compressedBitByte) {
    eprintf("Jni-DC: %s\n", "enter JBitConvertDecompress...");
    unsigned char outBuf[2048 * 4];
    unsigned long inLen = (unsigned long) (*env)->GetArrayLength(env, compressedBitByte);
    unsigned long outLen = 0;
    int m;
    JBIG_Decompressing(ConvertJByteArrayToChars(env, compressedBitByte), inLen, outBuf, &outLen);
    eprintf("Jni-DC: outLen= %ld\n", outLen);
//    for (m = 0; m < outLen; m++) {
//        eprintf("%02x ", outBuf[m]);
//    }
    return ConvertCharToJByteArray(env, outBuf, outLen);
}

JavaVM *g_VM;

/*
 * Class:     com_android_jbig_JBigConvert
 * Method:    compressing
 * Signature: (Landroid/graphics/Bitmap;[B)V
 * Java_com_android_jbig_JBigConvert_compressing
 */
JNIEXPORT jbyteArray JNICALL Java_com_android_jbig_JBigConvert_compressing
        (JNIEnv *env, jobject obj, jobject bitmapIn, jbyteArray byteIn) {
    eprintf("Jni-DC: %s\n", "enter JBitConvertCompress...");

    AndroidBitmapInfo infoIn;
    void *srcBuf;
    // Get image info
    if (AndroidBitmap_getInfo(env, bitmapIn, &infoIn) != ANDROID_BITMAP_RESULT_SUCCESS) {
        eprintf("Jni-DC:AndroidBitmap_getInfo failed!");
        return ConvertCharToJByteArray(env,"-996",1);
    }

    // Check image
    if (infoIn.format != ANDROID_BITMAP_FORMAT_RGBA_8888 &&
        infoIn.format != ANDROID_BITMAP_FORMAT_RGB_565) {
        eprintf("Jni-DC:Only support ANDROID_BITMAP_FORMAT_RGBA_8888 and ANDROID_BITMAP_FORMAT_RGB_565");
        return ConvertCharToJByteArray(env,"-997",1);
    }

    // Lock all images，用于获取像素数组的地址
    if (AndroidBitmap_lockPixels(env, bitmapIn, &srcBuf) != ANDROID_BITMAP_RESULT_SUCCESS) {
        eprintf("Jni-DC:AndroidBitmap_lockPixels failed!");
        return ConvertCharToJByteArray(env,"-998",1);
    }
    // height width
    int h = infoIn.height;
    int w = infoIn.width;
    eprintf("Jni-DC: bitmapWidth=%d,bitmapHeight=%d", w, h);

    // Start
    int arrayLen = (*env)->GetArrayLength(env, byteIn);
    eprintf("Jni-DC: 传入的Byte[]长度=%d", arrayLen);
    unsigned char outBuf[2048 * 4];
    unsigned long outLen = 0;

    JBIG_Compressing((unsigned long) w, (unsigned long) h,
                     ConvertJByteArrayToChars(env, byteIn), (unsigned long) arrayLen, outBuf,
                     &outLen);

    eprintf("Jni-DC: outLen= %ld\n", outLen);
    AndroidBitmap_unlockPixels(env, bitmapIn);
    unsigned int i;
//    for (i = 0; i < outLen; i++) {
//        eprintf("%02x ", outBuf[i]);
//    }
    return ConvertCharToJByteArray(env, outBuf, outLen);
}

//test
//        unsigned char bitmap[]={
//
//        };
//    eprintf("bitmapSize=%d",sizeof(bitmap));
//    unsigned char* llll = ConvertJByteArrayToChars(env,bitmap);
//    unsigned char inBuf[2048*40], outBuf[2048*4];
//    unsigned int i ,m;
//    unsigned long inLen = 0, outLen = 0;
//    JBIG_Compressing((unsigned long)w, (unsigned long)h, bitmap, 76800, outBuf,&outLen);
//    eprintf("Jni-DC: outBit= %s\n", outBuf);
//    eprintf("Jni-DC: outLen= %ld\n", outLen);
//    for (i = 0; i < outLen; i++) {
//        eprintf("out%02x ", outBuf[i]);
//    }
//    eprintf("=================================================================");
//    JBIG_Decompressing(outBuf, outLen, inBuf, &inLen);
////    eprintf("Jni-DC: inBufSize= %d\n", sizeof(inBuf));
//    eprintf("Jni-DC: inBuf= %s\n", inBuf);
//    eprintf("Jni-DC: inLen= %ld\n", inLen);
//    for (m = 0; m < inLen; m++) {
////        if (m != 0 && m % 32 == 0) eprintf("\r\n");
//        eprintf("%02x ", inBuf[m]);
//    }
//    eprintf("=================================================================");
//    eprintf("=================================================================");
//    eprintf("=================================================================");
//    for (m = 0; m < 76800; m++) {
////        if (m != 0 && m % 32 == 0) eprintf("\r\n");
//        eprintf("in%02x ", inBuf[m]);
//    }


/*
 * Class:     com_android_jbig_JBigConvert
 * Method:    compressingWH
 * Signature: (II[B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_android_jbig_JBigConvert_compressingWH
        (JNIEnv *env, jobject obj, jint width, jint height, jbyteArray byteIn) {
    eprintf("Jni-DC: %s\n", "enter JBitConvertCompressWH...");
    eprintf("Jni-DC: bitmapWidth=%d,bitmapHeight=%d", width, height);
    // Start
    int arrayLen = (*env)->GetArrayLength(env, byteIn);
    eprintf("Jni-DC: 传入的Byte[]长度=%d", arrayLen);
    unsigned char outBuf[2048 * 4];
    unsigned long outLen = 0;

    JBIG_Compressing((unsigned long) width, (unsigned long) height,
                     ConvertJByteArrayToChars(env, byteIn), (unsigned long) arrayLen, outBuf,
                     &outLen);

    eprintf("Jni-DC: outLen= %ld\n", outLen);
    unsigned int i;
//    for (i = 0; i < outLen; i++) {
//        eprintf("%02x ", outBuf[i]);
//    }
    return ConvertCharToJByteArray(env, outBuf, outLen);
}

#ifdef __cplusplus
}
#endif
//#endif