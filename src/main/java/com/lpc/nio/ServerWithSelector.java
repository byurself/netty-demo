package com.lpc.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.lpc.utils.ByteBufferUtil.debugAll;
import static com.lpc.utils.ByteBufferUtil.debugRead;

/**
 * @author byu_rself
 * @date 2023/7/26 15:34
 */
@Slf4j
public class ServerWithSelector {

    public static void main(String[] args) throws IOException {
        // 1.创建 selector
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2.建立 Channel 和 Selector 的联系(注册)
        // SelectionKey：将来事件发生时，通过它可以知道事件和哪个 channel 的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // key 只关注 accept 事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key: {}", sscKey);

        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            // 3.select 方法：没有事件发生则线程阻塞，有事件发生线程才会运行
            // select 在事件未处理时，它不会阻塞，事件发生后要么处理，要么取消，不能置之不理
            selector.select();
            // 4.处理事件 selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 处理 key 时，要从 selectedKeys 集合中删除，否则下次处理就会有问题
                iterator.remove();
                log.debug("key: {}", key);
                // 5.区分事件类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel(); // 拿到触发事件的 channel
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        int length = channel.read(buffer); // 如果客户端是正常断开，read方法的返回值是 -1
                        if (length == -1) {
                            key.cancel();
                        } else {
                            buffer.flip();
                            // debugAll(buffer);
                            System.out.println(Charset.defaultCharset().decode(buffer));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel(); // 因为客户端断开了，因此需要将 key 取消(从 selector 的 keys 集合中真正删除 key
                    }
                }

                // key.cancel();
            }
        }
    }
}
