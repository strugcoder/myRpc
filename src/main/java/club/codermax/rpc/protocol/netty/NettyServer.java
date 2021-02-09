package club.codermax.rpc.protocol.netty;

import club.codermax.rpc.framework.Configuration;
import club.codermax.rpc.framework.RpcRequest;
import club.codermax.rpc.serializer.NettyDecoderHandler;
import club.codermax.rpc.serializer.NettyEncoderHandler;
import club.codermax.rpc.serializer.SerializeType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NettyServer {

    private static NettyServer INSTANCE = new NettyServer();
    // ? ? ? ? ?
    private static Executor executor = Executors.newCachedThreadPool();

    private NettyServer() {
    }

    public static NettyServer getInstance() {
        return INSTANCE;
    }

    private SerializeType serializeType = SerializeType.queryByType(Configuration.getInstance().getSerialize());


    public static void submit(Runnable t) {
        executor.execute(t);
    }

    public void start(String host, Integer port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列等待连接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //Socket参数，连接保活,启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();
                            // 注册解码器
                            pipeline.addLast(new NettyDecoderHandler(RpcRequest.class, serializeType));

                            pipeline.addLast(new NettyEncoderHandler(serializeType));

                            pipeline.addLast("handle", new NettyServerHandler());

                        }
                    });
            Channel channel = serverBootstrap.bind(host, port).sync().channel();
            System.out.println("server start in" + host + "listen at" + port);
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
