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
                    ByteBuffer buffer = ByteBuffer.allocate(16); // attachment附件
                    // 将 ByteBuffer 作为附件关联到 selectionKey
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel(); // 拿到触发事件的 channel
                        // 获取 SelectionKey 上关联的附件
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int length = channel.read(buffer); // 如果客户端是正常断开，read方法的返回值是 -1
                        if (length == -1) {
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() << 1); // 扩容
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer); // 替换为新 ByteBuffer
                            }
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

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
}
