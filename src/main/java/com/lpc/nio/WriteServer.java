package com.lpc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author byu_rself
 * @date 2023/7/26 19:57
 */
public class WriteServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);

                    // 1.向客户端发送大量数据
                    StringBuilder builder = new StringBuilder();
                    builder.append("a".repeat(30000000));

                    ByteBuffer buffer = Charset.defaultCharset().encode(builder.toString());
                    int length = sc.write(buffer); // 2.返回值代表实际写入的字节数
                    System.out.println(length);
                    if (buffer.hasRemaining()) {
                        // 关注可写事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        // scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                        // 把未写完的数据挂到 scKey
                        scKey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();

                    int length = sc.write(buffer);
                    System.out.println(length);
                    // 清理
                    if (!buffer.hasRemaining()) {
                        key.attach(null); // 清除buffer
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE); // 取消关注可写事件
                    }
                }
            }
        }
    }
}
