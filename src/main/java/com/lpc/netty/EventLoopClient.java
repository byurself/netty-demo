package com.lpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author byu_rself
 * @date 2023/7/27 15:03
 */
@Slf4j
public class EventLoopClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 1.启动类
        ChannelFuture channelFuture = new Bootstrap()
                // 2.添加 EventLoop
                .group(group)
                // 3.选择客户端 channel 实现
                .channel(NioSocketChannel.class)
                // 4.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    // 在连接建立后被调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    }
                })
                // 5.连接到服务器
                // 异步非阻塞 main 发起了调用，真正执行 connect 的是 nio 线程
                .connect(new InetSocketAddress("localhost", 8080));

        Channel channel = channelFuture.sync().channel();
        log.debug("{}", channel);
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String s = scanner.nextLine();
                if ("q".equals(s)) {
                    channel.close(); // close方法为异步操作
                    // log.debug("处理关闭之后的操作"); // 不能在这里进行关闭后的操作
                    break;
                }
                channel.writeAndFlush(s);
            }
        }, "input").start();

        // 获取 CloseFuture 对象 1) 同步处理关闭 2) 异步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
        log.debug("waiting close...");
        // closeFuture.sync(); // 同步处理
        // log.debug("处理关闭之后的操作");
        // 异步处理
        closeFuture.addListener((ChannelFutureListener) future -> {
            log.debug("处理关闭之后的操作");
            group.shutdownGracefully();
        });

        // 1.使用 sync 方法同步处理结果
        /*// 阻塞方法，直到连接建立
        channelFuture.sync();
        // 代表连接对象
        Channel channel = channelFuture.channel();
        log.debug("{}", channel);
        channel.writeAndFlush("hello world!");*/

        // 2.使用 addListener(回调对象) 方法异步处理结果
        /*channelFuture.addListener(new ChannelFutureListener() {
            @Override
            // 在 nio 线程连接建立之后，会调用 operationComplete
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.debug("{}", channel);
                channel.writeAndFlush("hello world!");
            }
        });*/
    }
}
