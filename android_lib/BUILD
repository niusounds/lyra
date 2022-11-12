load("@rules_android//android:rules.bzl", "android_binary", "android_library")

# Placeholder for jni import

package(default_visibility = ["//visibility:public"])

licenses(["notice"])

# Android .so
cc_library(
    name = "lyra_jni",
    srcs = [
        "lyra_jni.cc",
        "@org_tensorflow//tensorflow/lite:libtensorflowlite.so",
    ],
    linkopts = [
        "-landroid",
        "-ldl",
        "-llog",
    ],
    deps = [
        "//:decoder_main_lib",
        "//:encoder_main_lib",
        "//:lyra_benchmark_lib",
        "//:lyra_config",
        "@com_google_absl//absl/random",
    ],
    alwayslink = True,
)

android_library(
    name = "lyra_android_lib",
    srcs = [
        "//android_lib/java/com/github/lyra/android:LyraEncoder.java",
        "//android_lib/java/com/github/lyra/android:LyraDecoder.java",
    ],
    custom_package = "com.github.lyra.android",
    deps = [
        ":lyra_jni",
        # "@maven//:androidx_annotation_annotation",
        # "@maven//:androidx_appcompat_appcompat",
        # "@maven//:androidx_constraintlayout_constraintlayout",
        # "@maven//:androidx_core_core",
        "@org_tensorflow//tensorflow/lite:libtensorflowlite.so",
    ],
)

# filegroup(
#     name = "assets",
#     srcs = ["//:android_lib_assets"],
# )
