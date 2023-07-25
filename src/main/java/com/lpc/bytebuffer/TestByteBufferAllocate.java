package com.lpc.bytebuffer;

import java.nio.ByteBuffer;

/**
 * @author byu_rself
 * @date 2023/7/25 15:04
 */
public class TestByteBufferAllocate {

    public static void main(String[] args) {
        // class java.nio.HeapByteBuffer - Java堆内存，读写效率较低，受到 GC 的影响
        System.out.println(ByteBuffer.allocate(16).getClass());
        // class java.nio.DirectByteBuffer - 直接内存，读写效率高(少一次拷贝)，不会受 GC 的影响，分配的效率低
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }
}
