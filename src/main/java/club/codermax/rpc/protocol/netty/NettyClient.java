package club.codermax.rpc.protocol.netty;

import club.codermax.rpc.framework.Configuration;
import club.codermax.rpc.framework.RpcRequest;
import club.codermax.rpc.framework.RpcResponse;
import club.codermax.rpc.serializer.NettyDecoderHandler;
import club.codermax.rpc.serializer.NettyEncoderHandler;
import club.codermax.rpc.serializer.SerializeType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;

public class NettyClient {
    private static NettyClient INSTANCE = new NettyClient();

    private NettyClient() {
    }

    public static NettyClient getInstance() {
        return INSTANCE;
    }


    private SerializeType serializeType = SerializeType.queryByType(Configuration.getInstance().getSerialize());

    public void start(String host, Integer port) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NettyEncoderHandler(serializeType));
                            pipeline.addLast(new NettyDecoderHandler(RpcResponse.class, serializeType));
                            pipeline.addLast("handler", new NettyClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
        } catch (Exception e) {
            group.shutdownGracefully();
        }
    }
}
