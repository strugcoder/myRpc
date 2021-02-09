package club.codermax.rpc.protocol;


import club.codermax.rpc.framework.RpcRequest;
import club.codermax.rpc.framework.URL;

/**
 * 定义可插拔的协议
 */
public interface Procotol {

    void start(URL url);
    Object send(URL url, RpcRequest invocation);

}
