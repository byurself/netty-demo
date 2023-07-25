package com.lpc.bytebuffer;

import java.nio.ByteBuffer;

import static com.lpc.utils.ByteBufferUtil.debugAll;

/**
 * @author byu_rself
 * @date 2023/7/25 14:57
 */
public class TestByteBufferReadWrite {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61); // 'a'
        debugAll(buffer);
        buffer.put(new byte[]{0x62, 0x63, 0x64}); // 'b','c','d'
        debugAll(buffer);
        // System.out.println(buffer.get());
        buffer.flip();
        System.out.println(buffer.get());
        debugAll(buffer);
        buffer.compact();
        debugAll(buffer);
        buffer.put(new byte[]{0x65, 0x66}); // 'e','f'
        debugAll(buffer);
    }
}
