package com.lpc.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.lpc.utils.ByteBufferUtil.debugAll;

/**
 * @author byu_rself
 * @date 2023/7/25 15:17
 */
public class TestByteBufferString {

    public static void main(String[] args) {
        // 1.字符串转为 ByteBuffer
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1);

        // 2.Charset
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);

        // 3.wrap
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        // ByteBuffer 转字符串 需在读模式下
        String str2 = StandardCharsets.UTF_8.decode(buffer2).toString();
        System.out.println(str2);

        buffer1.flip();
        String str1 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(str1);
    }
}
