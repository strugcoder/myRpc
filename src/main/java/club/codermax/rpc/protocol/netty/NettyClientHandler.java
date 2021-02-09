package club.codermax.rpc.protocol.netty;

import club.codermax.rpc.framework.MessageCallBack;
import club.codermax.rpc.framework.RpcResponse;
import club.codermax.rpc.protocol.netty.channelpool.ResponseHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private ChannelHandlerContext context;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("停止的时间是：" + new Date());
        System.out.println("HeartBeatClientHandler channelInactive");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
        System.out.println("激活的时间是：" + new Date());
        System.out.println("由：" + ctx.channel().id() + "激活");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        String responseId = rpcResponse.getResponseId();
        MessageCallBack callBack = ResponseHolder.getInstance().mapCallBack.get(responseId);
        // 不为空说明得到了响应，这时候唤醒进入到条件队列中执行
        if (callBack != null) {
            ResponseHolder.getInstance().mapCallBack.remove(responseId);
            callBack.over(rpcResponse);
        }
    }
}
