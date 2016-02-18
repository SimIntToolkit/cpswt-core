#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include "c2w_process_ProcessId.h"

JNIEXPORT jint JNICALL Java_c2w_process_ProcessId_setProcessGroupId(JNIEnv *env, jobject obj) {
  setpgid( 0, 0 );
  jint pid = getpid();
  return pid;
}
