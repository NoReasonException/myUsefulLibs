#include "com_noReasonException_InotifyWrapper_InotifyWrapper.h"
#include <string.h>
#include <stdlib.h>
extern int initializeInotify(char path[]);
extern char *getLastModifiedFile();
extern int waitForFileEvent();
extern int getLastModifiedType();
JNIEXPORT jint JNICALL Java_com_noReasonException_InotifyWrapper_InotifyWrapper_initializeInotify(JNIEnv *env, jobject thisz, jbyteArray path){
    jbyte *a = env->GetByteArrayElements(path,NULL);    //get jbyte reference
    int len = env->GetArrayLength(path);
    //int len=(strlen((const char *)a));                  //get len of array given
    char *b =(char *) malloc(len+1);                    //allocate second buffer for converting to C style string
    memcpy(b,a,len);                                    //copy the elements untill len-1
    b[len]='\0';
                                                        //put the null terminated ...
    int retval=initializeInotify(b);                    //call the initializer..
    free(b);                                            //Free temp buffer
    env->ReleaseByteArrayElements(path,a,JNI_ABORT);    //Release path object

    return retval;
}
JNIEXPORT jint JNICALL Java_com_noReasonException_InotifyWrapper_InotifyWrapper_waitForFileEvent(JNIEnv *, jobject){
    return waitForFileEvent();

}
JNIEXPORT jbyteArray JNICALL Java_com_noReasonException_InotifyWrapper_InotifyWrapper_getLastModifiedFile(JNIEnv *env, jobject thiz){
    char *getLastModifiedFilePath = getLastModifiedFile();                  //get path of last modified file

    if(getLastModifiedFilePath==NULL){
        jclass EmptyStackException = env->FindClass("java/io/IOException");
        env->ThrowNew(EmptyStackException,"No Other files detected , try call waitForFileEvent()");
        return env->NewByteArray(0);

    }

    ssize_t pathSize=strlen(getLastModifiedFilePath);                       //get size of that path
    jbyteArray retval = env->NewByteArray(pathSize);                          //Allocate a new jbyteArray to return !
    env->SetByteArrayRegion(retval,0,pathSize,(jbyte*)getLastModifiedFilePath);
    return retval;
}
JNIEXPORT jint JNICALL Java_com_noReasonException_InotifyWrapper_InotifyWrapper_getLastModifiedType(JNIEnv *, jobject){
    return getLastModifiedType();

}

