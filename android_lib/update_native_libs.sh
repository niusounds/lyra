# Call this script from repository root directory
bazel build -c opt android_lib:lyra_android_lib_apk --config=android_arm64

out_dir="android_lib/gradle_root/src/main/jniLibs/arm64-v8a"
mkdir -p "$out_dir"
unzip bazel-bin/android_lib/lyra_android_lib_apk.apk lib/arm64-v8a/liblyra_android_lib_apk.so
mv -f "lib/arm64-v8a/liblyra_android_lib_apk.so" "$out_dir/liblyra_jni.so"
