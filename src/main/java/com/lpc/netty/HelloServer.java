package com.lpc.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author byu_rself
 * @date 2023/7/27 14:48
 */
public class HelloServer {

    public static void main(String[] args) {
        // 1.启动器：负责组装 netty 组件，启动服务器
        new ServerBootstrap()
                // 2.组
                .group(new NioEventLoopGroup())
                // 3.选择服务器的 ServerSocketChannel 实现
                .channel(NioServerSocketChannel.class)
                // 4.负责处理读写，决定能执行哪些操作(handler)
                .childHandler(
                        // 5.channel:代表和客户端进行数据读写的通道 Initializer:负责添加别的 handler
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                // 6.添加具体 handler
                                ch.pipeline().addLast(new StringDecoder()); // 将 ByteBuf 转为字符串
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() { // 自定义handler
                                    // 读事件
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                        })
                // 7.绑定监听端口
                .bind(8080);
    }
}
