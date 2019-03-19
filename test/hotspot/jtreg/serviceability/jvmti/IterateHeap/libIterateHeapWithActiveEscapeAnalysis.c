/*
 * Copyright (c) 2019 SAP SE. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include <stdio.h>
#include <string.h>
#include "jvmti.h"
#include "jni.h"

#ifndef JNI_ENV_ARG
#define JNI_ENV_ARG(x,y) x, y
#define JNI_ENV_PTR(x) (*x)
#endif

#define FAILED -1
#define OK      0

static jvmtiEnv *jvmti;

static jint Agent_Initialize(JavaVM *jvm, char *options, void *reserved);

static void ShowErrorMessage(jvmtiEnv *jvmti, jvmtiError errCode, const char *message) {
    char *errMsg;
    jvmtiError result;

    result = (*jvmti)->GetErrorName(jvmti, errCode, &errMsg);
    if (result == JVMTI_ERROR_NONE) {
        fprintf(stderr, "%s: %s (%d)\n", message, errMsg, errCode);
        (*jvmti)->Deallocate(jvmti, (unsigned char *)errMsg);
    } else {
        fprintf(stderr, "%s (%d)\n", message, errCode);
    }
}

JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *jvm, char *options, void *reserved) {
    return Agent_Initialize(jvm, options, reserved);
}

JNIEXPORT jint JNICALL
Agent_OnAttach(JavaVM *jvm, char *options, void *reserved) {
    return Agent_Initialize(jvm, options, reserved);
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *jvm, void *reserved) {
    jint res;
    JNIEnv *env;

    res = JNI_ENV_PTR(jvm)->GetEnv(JNI_ENV_ARG(jvm, (void **) &env),
                                   JNI_VERSION_9);
    if (res != JNI_OK || env == NULL) {
        fprintf(stderr, "Error: GetEnv call failed(%d)!\n", res);
        return JNI_ERR;
    }

    return JNI_VERSION_9;
}

static jint
Agent_Initialize(JavaVM *jvm, char *options, void *reserved) {
    jint res;
    jvmtiError err;
    jvmtiCapabilities caps;

    printf("Agent_OnLoad started\n");

    memset(&caps, 0, sizeof(caps));

    res = JNI_ENV_PTR(jvm)->GetEnv(JNI_ENV_ARG(jvm, (void **) &jvmti),
                                   JVMTI_VERSION_9);
    if (res != JNI_OK || jvmti == NULL) {
        fprintf(stderr, "Error: wrong result of a valid call to GetEnv!\n");
        return JNI_ERR;
    }

    caps.can_tag_objects = 1;

    err = (*jvmti)->AddCapabilities(jvmti, &caps);
    if (err != JVMTI_ERROR_NONE) {
        ShowErrorMessage(jvmti, err,
                         "Agent_OnLoad: error in JVMTI AddCapabilities");
        return JNI_ERR;
    }

    err = (*jvmti)->GetCapabilities(jvmti, &caps);
    if (err != JVMTI_ERROR_NONE) {
        ShowErrorMessage(jvmti, err,
                         "Agent_OnLoad: error in JVMTI GetCapabilities");
        return JNI_ERR;
    }

    if (!caps.can_tag_objects) {
        fprintf(stderr, "Warning: didn't get the capability can_tag_objects\n");
        return JNI_ERR;
    }

    printf("Agent_OnLoad finished\n");
    return JNI_OK;
}

JNIEXPORT jint JNICALL
Java_IterateHeapWithActiveEscapeAnalysis_jvmtiTagClass(JNIEnv *env, jclass cls, jclass clsToTag, jlong tag) {
    jvmtiError err;
    err = (*jvmti)->SetTag(jvmti, clsToTag, tag);
    if (err != JVMTI_ERROR_NONE) {
        ShowErrorMessage(jvmti, err,
                         "jvmtiTagClass: error in JVMTI SetTag");
        return FAILED;
    }
    return OK;
}

typedef struct Tag_And_Counter {
    jlong instance_counter;
    jlong class_tag;
} Tag_And_Counter;

JNIEXPORT jvmtiIterationControl JNICALL
stackReferenceCallback(jvmtiHeapRootKind root_kind,
                       jlong class_tag,
                       jlong size,
                       jlong* tag_ptr,
                       jlong thread_tag,
                       jint depth,
                       jmethodID method,
                       jint slot,
                       void* d) {
    Tag_And_Counter* data = (Tag_And_Counter*) d;
    if (class_tag == data->class_tag) {
        data->instance_counter++;
    }
    return JVMTI_ITERATION_CONTINUE;
}

JNIEXPORT jlong JNICALL
Java_IterateHeapWithActiveEscapeAnalysis_countInstancesOfClass(JNIEnv *env, jclass cls, jlong clsTag) {
    jvmtiError err;
    Tag_And_Counter data = {0, clsTag};

    jint idx = 0;

    err = (*jvmti)->IterateOverReachableObjects(jvmti,
                                                NULL /*jvmtiHeapRootCallback*/,
                                                stackReferenceCallback,
                                                NULL /* jvmtiObjectReferenceCallback */,
                                                &data);
    if (err != JVMTI_ERROR_NONE) {
        ShowErrorMessage(jvmti, err,
                         "countInstancesOfClass: error in JVMTI IterateOverReachableObjects");
        return FAILED;
    }

    return data.instance_counter;
}
