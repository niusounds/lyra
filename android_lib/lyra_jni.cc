// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include <jni.h>

#include "lyra_decoder.h"
#include "lyra_encoder.h"

const int kErrorEncodeFailed = -1;
const int kErrorOutBufferTooSmall = -2;

const int kErrorDecodeFailed = -1;

extern "C" JNIEXPORT jlong JNICALL
Java_com_github_lyra_android_LyraDecoder_create(JNIEnv* env, jobject this_obj,
                                                jint sampleRate, jint channels,
                                                jstring model_base_path) {
  const char* cpp_model_base_path = env->GetStringUTFChars(model_base_path, 0);
  auto decoder = chromemedia::codec::LyraDecoder::Create(sampleRate, channels,
                                                         cpp_model_base_path)
                     .release();

  return reinterpret_cast<jlong>(decoder);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_github_lyra_android_LyraDecoder_setEncodedPacket(JNIEnv* env, jobject,
                                                          jlong ptr,
                                                          jobject packet,
                                                          jint packetSize) {
  auto decoder = reinterpret_cast<chromemedia::codec::LyraDecoder*>(ptr);
  auto packetPtr = (uint8_t*)env->GetDirectBufferAddress(packet);
  return decoder->SetEncodedPacket(absl::MakeConstSpan(packetPtr, packetSize));
}

extern "C" JNIEXPORT jint JNICALL
Java_com_github_lyra_android_LyraDecoder_decodeSamples(JNIEnv* env, jobject,
                                                       jlong ptr,
                                                       jint numSamples,
                                                       jshortArray outSamples) {
  auto decoder = reinterpret_cast<chromemedia::codec::LyraDecoder*>(ptr);
  auto decoded = decoder->DecodeSamples(numSamples);
  if (!decoded.has_value()) {
    LOG(ERROR) << "Unable to decode samples " << numSamples << ".";
    return kErrorDecodeFailed;
  }

  auto outSamplesSize = env->GetArrayLength(outSamples);
  auto requiredSize = decoded.value().size();
  if (outSamplesSize < requiredSize) {
    LOG(ERROR) << "outSamples capacity " << outSamplesSize
               << " is insufficient to store decoded samples " << requiredSize
               << ".";
    return kErrorOutBufferTooSmall;
  }

  env->SetShortArrayRegion(outSamples, 0, requiredSize, &decoded.value()[0]);

  return requiredSize;
}

extern "C" JNIEXPORT void JNICALL
Java_com_github_lyra_android_LyraDecoder_release(JNIEnv* env, jobject this_obj,
                                                 jlong ptr) {
  auto decoder = reinterpret_cast<chromemedia::codec::LyraDecoder*>(ptr);
  if (decoder) {
    delete decoder;
  }
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_github_lyra_android_LyraEncoder_create(JNIEnv* env, jobject this_obj,
                                                jint sample_rate_hz,
                                                jint num_channels, jint bitrate,
                                                jboolean enable_dtx,
                                                jstring model_base_path) {
  const char* cpp_model_base_path = env->GetStringUTFChars(model_base_path, 0);

  auto encoder = chromemedia::codec::LyraEncoder::Create(
                     /*sample_rate_hz=*/sample_rate_hz,
                     /*num_channels=*/num_channels,
                     /*bitrate=*/bitrate,
                     /*enable_dtx=*/enable_dtx,
                     /*model_path=*/cpp_model_base_path)
                     .release();
  return reinterpret_cast<jlong>(encoder);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_github_lyra_android_LyraEncoder_encode(JNIEnv* env, jobject this_obj,
                                                jlong ptr, jshortArray samples,
                                                jint sample_length,
                                                jobject outBuffer,
                                                jint offset) {
  // copy to samples_vector
  std::vector<int16_t> samples_vector(sample_length);
  env->GetShortArrayRegion(samples, jsize{0}, sample_length,
                           &samples_vector[0]);

  auto encoder = reinterpret_cast<chromemedia::codec::LyraEncoder*>(ptr);
  auto encoded = encoder->Encode(
      absl::MakeConstSpan(&samples_vector.at(0), sample_length));
  if (!encoded.has_value()) {
    LOG(ERROR) << "Unable to encode features";
    return kErrorEncodeFailed;
  }

  auto outBufferPtr = (uint8_t*)env->GetDirectBufferAddress(outBuffer);
  auto outBufferSize = env->GetDirectBufferCapacity(outBuffer) - offset;
  auto requiredSize = encoded.value().size();
  if (outBufferSize < requiredSize) {
    LOG(ERROR) << "outBuffer capacity " << outBufferSize
               << " is insufficient to store encoded features " << requiredSize
               << ".";
    return kErrorOutBufferTooSmall;
  }

  memcpy(&outBufferPtr[offset], &encoded.value(), requiredSize);

  return requiredSize;
}

extern "C" JNIEXPORT void JNICALL
Java_com_github_lyra_android_LyraEncoder_release(JNIEnv* env, jobject this_obj,
                                                 jlong ptr) {
  auto encoder = reinterpret_cast<chromemedia::codec::LyraEncoder*>(ptr);
  if (encoder) {
    delete encoder;
  }
}
