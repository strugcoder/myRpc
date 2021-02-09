package club.codermax.rpc.protocol.netty.channelpool;

import club.codermax.rpc.framework.Configuration;
import club.codermax.rpc.framework.RpcResponse;
import club.codermax.rpc.framework.URL;
import club.codermax.rpc.protocol.netty.NettyClientHandler;
import club.codermax.rpc.serializer.NettyDecoderHandler;
import club.codermax.rpc.serializer.NettyEncoderHandler;
import club.codermax.rpc.serializer.SerializeType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class NettyChannelPoolFactoryTest implements Callable<Channel> {

    private static final Logger logger = LoggerFactory.getLogger(NettyChannelPoolFactoryTest.class);

    // 初始化Netty Channel注册队列的长度，该值可配置信息
    private static final int channelConnectSize = 10;

    private static Executor executor = Executors.newFixedThreadPool(10);


    // Key为服务提供者地址，value为Netty Channel阻塞队列
    private static final Map<URL, ArrayBlockingQueue<Channel>> channelPoolMap = new ConcurrentHashMap<>();

    private static NettyChannelPoolFactoryTest INSTANCE = new NettyChannelPoolFactoryTest();

    private NettyChannelPoolFactoryTest() {
    }

    public static NettyChannelPoolFactoryTest getInstance() {
        return INSTANCE;
    }

    private List<String> serviceMetaDataList = new ArrayList<>();

    @Override
    public Channel call() throws Exception {
        return null;
    }
    // 根据配置文件中需要调用的接口信息来初始化Channel
    public void initNettyChannelPoolFactory(List<String> providerList) {
        long a1 = System.currentTimeMillis();
        // 获取服务提供者地址列表
        Set<URL> set = new HashSet<>();
        for (String provide : providerList) {
            String[] s = provide.split("\\|");
            String serviceIp = s[0];
            int servicePort = Integer.parseInt(s[1]);
            URL url = new URL(serviceIp, servicePort);
            set.add(url);
        }
        long a2 = System.currentTimeMillis();
        logger.warn("获取服务提供者地址列表" + (a2 - a1));

        // 为每个 ip 端口 建立多个Channel并放入到阻塞队列中
        for (URL url : set) {
            int channelSize = 0;
            if (channelSize < channelConnectSize) {
                Channel channel = null;
                while (channel == null) {  // 确保能够注册进入
                    channel = registerChannel(url);
                }
                channelSize++;

                ArrayBlockingQueue<Channel> queue = channelPoolMap.get(url);
                if (queue == null) {
                    queue = new ArrayBlockingQueue<>(channelConnectSize);
                    channelPoolMap.put(url, queue);
                }
                queue.offer(channel);
            }
        }
        logger.warn("为每个 ip 端口 建立多个Channel并放入到阻塞队列中" + (System.currentTimeMillis() - a2));
    }


    public Channel registerChannel(URL url) {

        final SerializeType serializeType = SerializeType.queryByType(Configuration.getInstance().getSerialize());

        NioEventLoopGroup group = new NioEventLoopGroup(10);

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // netty编解码器
                            pipeline.addLast(new NettyEncoderHandler(serializeType));
                            pipeline.addLast(new NettyDecoderHandler(RpcResponse.class, serializeType));
                            pipeline.addLast("handler", new NettyClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(url.getHost(), url.getPort()).sync();
            Channel channel = future.channel();


            // 等待netty服务端链路建立通知信号，这里也可以使用 future.await() 这个监听器 可以 吗 ？？？
            final CountDownLatch countDownLatch = new CountDownLatch(1);

            final List<Boolean> isSuccess = new ArrayList<>(1);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        isSuccess.add(true);
                    } else {
                        isSuccess.add(false);
                    }
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            if (isSuccess.get(0)) {
                return channel;
            }

        } catch (Exception e) {
            group.shutdownGracefully();
            e.printStackTrace();
        }
        return null;
    }


    // 根据url获取阻塞队列
    public ArrayBlockingQueue<Channel> acquire(URL url) {
        System.out.println(channelPoolMap.toString());
        return channelPoolMap.get(url);
    }

    // channel使用完毕后进行回收，如果当前channel可以用，放入到队列中继续使用，如果不可用，新创建，保证有10个
    public void release(ArrayBlockingQueue<Channel> queue, Channel channel, URL url) {
        if (queue == null)
            return;
        // 需要检查 channel 是否可用，如果不可用，重新注册一个放入阻塞队列中
        if (channel == null || !channel.isActive() || !channel.isOpen() || !channel.isWritable()) {
            if (channel != null) {
                // 意义？？？？？
                channel.deregister().syncUninterruptibly().awaitUninterruptibly();
                channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
            }

            Channel c = null;
            while (c == null) {
                c = registerChannel(url);
            }
            queue.offer(c);
            return;
        }
        queue.offer(channel);
    }



}


