package club.codermax.rpc.protocol.netty;

import club.codermax.rpc.framework.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private ChannelHandlerContext context;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        logger.info("server channelRead...");
        logger.info(ctx.channel().remoteAddress() + "->server: " + rpcRequest.toString());
        InvokeTask invokeTask = new InvokeTask(rpcRequest, ctx);
        NettyServer.submit(invokeTask);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }
}
