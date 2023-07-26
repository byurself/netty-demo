package com.lpc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author byu_rself
 * @date 2023/7/26 20:04
 */
public class WriteClient {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));

        // 3.接收数据
        int count = 0;
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
        while (true) {
            count += sc.read(buffer);
            System.out.println(count);
            buffer.clear();
        }
    }
}
