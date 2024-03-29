// Copyright 2018 The MACE Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/* Header for class com_xiaomi_mace_JniMaceUtils */

#ifndef MACE_EXAMPLES_ANDROID_MACELIBRARY_SRC_MAIN_CPP_IMAGE_CLASSIFY_H_
#define MACE_EXAMPLES_ANDROID_MACELIBRARY_SRC_MAIN_CPP_IMAGE_CLASSIFY_H_

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_xiaomi_mace_JniMaceUtils
 * Method:    maceCreateGPUContext
 * Signature: (Ljava/lang/String;IIIILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL
Java_com_xiaomi_mace_JniMaceUtils_maceCreateGPUContext(JNIEnv *,
                                                       jclass,
                                                       jstring);

/*
 * Class:     com_xiaomi_mace_JniMaceUtils
 * Method:    maceCreateEngine
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL
Java_com_xiaomi_mace_JniMaceUtils_maceCreateEngine
  (JNIEnv *, jclass, jint, jint, jint, jint, jstring, jstring, jstring);

/*
 * Class:     com_xiaomi_mace_JniMaceUtils
 * Method:    maceClassify
 * Signature: ([F)[F
 */
JNIEXPORT jfloatArray JNICALL
Java_com_xiaomi_mace_JniMaceUtils_maceClassify
  (JNIEnv *, jclass, jfloatArray);

#ifdef __cplusplus
}
#endif
#endif  // MACE_EXAMPLES_ANDROID_MACELIBRARY_SRC_MAIN_CPP_IMAGE_CLASSIFY_H_
