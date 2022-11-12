package com.github.lyra.android;

import java.nio.ByteBuffer;

class LyraEncoder {
    private final long nativePtr;

    public LyraEncoder(int sample_rate_hz, int num_channels, int bitrate, boolean enable_dtx, String model_base_path) {
        this.nativePtr = create(sample_rate_hz, num_channels, bitrate, enable_dtx, model_base_path);
    }

    public int encode(short[] samples, int sample_length, ByteBuffer outBuffer) {
        return encode(nativePtr, samples, sample_length, outBuffer);
    }

    public void release() {
        release(nativePtr);
    }

    private static native long create(int sample_rate_hz, int num_channels, int bitrate, boolean enable_dtx, String model_base_path);
    private static native int encode(long ptr, short[] samples, int sample_length, ByteBuffer outBuffer);
    private static native void release(long ptr);
}
