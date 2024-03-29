package com.lpc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author byu_rself
 * @date 2023/7/26 14:54
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        // sc.write(Charset.defaultCharset().encode("hello\nworld!\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdefghijklmnopqrstuvwxyz\n"));
        System.in.read();
    }
}
