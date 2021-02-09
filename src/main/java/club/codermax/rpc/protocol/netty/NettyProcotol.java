package club.codermax.rpc.protocol.netty;

import club.codermax.rpc.framework.MessageCallBack;
import club.codermax.rpc.framework.RpcRequest;
import club.codermax.rpc.framework.URL;
import club.codermax.rpc.protocol.Procotol;
import club.codermax.rpc.protocol.netty.channelpool.NettyChannelPoolFactory;
import club.codermax.rpc.protocol.netty.channelpool.ResponseHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NettyProcotol implements Procotol {
    @Override
    public void start(URL url) {
        NettyServer nettyServer = NettyServer.getInstance();
        nettyServer.start(url.getHost(), url.getPort());
    }

    @Override
    public Object send(URL url, RpcRequest invocation) {
        ArrayBlockingQueue<Channel> queue = NettyChannelPoolFactory.getInstance().acquire(url);
        Channel channel = null;

        try {
            channel = queue.poll(invocation.getTimeOut(), TimeUnit.MILLISECONDS);
            if (channel == null || !channel.isActive() || !channel.isOpen() || !channel.isWritable()) {
                channel = queue.poll(invocation.getTimeOut(), TimeUnit.MILLISECONDS);
                if (channel == null) {
                    channel = NettyChannelPoolFactory.getInstance().registerChannel(url);
                }
            }
            // 将本次调用信息写入netty通道，发起异步调用
            ChannelFuture channelFuture = channel.writeAndFlush(invocation);
            channelFuture.syncUninterruptibly();
            MessageCallBack callBack = new MessageCallBack(invocation);
            ResponseHolder.getInstance().mapCallBack.put(invocation.getRequestId(), callBack);
            try {
                return callBack.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("release: " + channel.id());
            NettyChannelPoolFactory.getInstance().release(queue,channel,url);
        }
        return null;
    }
}
