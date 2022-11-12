package com.github.lyra.android;

import java.nio.ByteBuffer;

class LyraDecoder {
    private final long nativePtr;

    public LyraDecoder(int sampleRateHz, int numChannels, String modelBasePath) {
        this.nativePtr = create(sampleRateHz, numChannels, modelBasePath);
    }

    public boolean setEncodedPacket(ByteBuffer packet, int packetSize) {
        return setEncodedPacket(nativePtr, packet, packetSize);
    }

    public int decodeSamples(int numSamples, ByteBuffer outBuffer) {
        return decodeSamples(nativePtr, numSamples, outBuffer);
    }

    public void release() {
        release(nativePtr);
    }

    private static native long create(int sampleRateHz, int numChannels, String modelBasePath);
    private static native boolean setEncodedPacket(long ptr, ByteBuffer packet, int packetSize);
    private static native int decodeSamples(long ptr, int numSamples, ByteBuffer outBuffer);
    private static native void release(long ptr);
}
