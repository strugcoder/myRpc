package club.codermax.rpc.protocol.netty;


import club.codermax.rpc.framework.RpcRequest;
import club.codermax.rpc.framework.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;


/**
 * InvokeTask 就是真实的任务实现，使用动态代理
 */
public class InvokeTask implements Runnable {

    private RpcRequest invocation;
    private ChannelHandlerContext ctx;

    public InvokeTask(RpcRequest invocation, ChannelHandlerContext ctx) {
        this.invocation = invocation;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        // 从注册中心根据接口，找接口的实现类
        String interfaceName = invocation.getInterfaceName();
        Class impClass = null;
        try {
            impClass = Class.forName(invocation.getImpl());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Method method;
        Object result = null;
        try {
            method = impClass.getMethod(invocation.getMethodName(),invocation.getParamTypes());
            //这块考虑实现类，是不是应该在 spring 里面拿
            // 方法
            result = method.invoke(impClass.newInstance(),invocation.getParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResponseId(invocation.getRequestId());
        rpcResponse.setData(result);

        // 写入rpcResponse，响应，addListener异步当响应完成写入之后进行执行
        ctx.writeAndFlush(rpcResponse).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("RPC Server Send message-id respone:" + invocation.getRequestId());
            }
        });
    }
}
