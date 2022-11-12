# Call this script from repository root directory
bazel build -c opt android_lib:lyra_jni --config=android_arm64 --copt=-DBENCHMARK

out_dir="android_lib/gradle_root/src/main/jniLibs/arm64-v8a"
mkdir -p "$out_dir" && cp -f "bazel-bin/android_lib/liblyra_jni.so" "$out_dir/liblyra_jni.so"
