package com.lpc.bytebuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author byu_rself
 * @date 2023/7/25 14:16
 */
@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        // FileChannel
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 从channel读取，向buffer写
                int len = channel.read(buffer);
                log.debug("读取到的字节数：{}", len);
                if (len == -1) {
                    break;
                }
                // 打印buffer的内容
                buffer.flip(); // 切换至读模式
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.debug("实际字节：{}", (char) b);
                }
                // 切换为写模式
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}