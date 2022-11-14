load("@rules_android//android:rules.bzl", "android_binary", "android_library")

# Placeholder for jni import

package(default_visibility = ["//visibility:public"])

licenses(["notice"])

# Android .so
cc_library(
    name = "lyra_jni",
    srcs = [
        "lyra_jni.cc",
    ],
    linkopts = [
        "-landroid",
        "-ldl",
        "-llog",
    ],
    deps = [
        "//:lyra_decoder",
        "//:lyra_encoder",
    ],
    alwayslink = True,
)

android_library(
    name = "lyra_android_lib",
    srcs = [
        "//android_lib/gradle_root/library/src/main/java/com/github/lyra/android:LyraDecoder.java",
        "//android_lib/gradle_root/library/src/main/java/com/github/lyra/android:LyraEncoder.java",
    ],
    custom_package = "com.github.lyra.android",
    deps = [
        ":lyra_jni",
        "@org_tensorflow//tensorflow/lite:libtensorflowlite.so",
    ],
)

filegroup(
    name = "assets",
    srcs = ["//:android_example_assets"],
)

android_binary(
    name = "lyra_android_lib_apk",
    assets = [
        ":assets",
    ],
    assets_dir = "model_coeffs",
    custom_package = "com.github.lyra.android.example",
    manifest = "gradle_root/library/src/main/AndroidManifest.xml",
    multidex = "native",
    deps = [":lyra_android_lib"],
)
