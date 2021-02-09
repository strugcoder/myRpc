package club.codermax.rpc.protocol.netty.channelpool;

import club.codermax.rpc.framework.MessageCallBack;

import java.util.concurrent.ConcurrentHashMap;

public class ResponseHolder {

    private static ResponseHolder holder = new ResponseHolder();

    private ResponseHolder(){}

    public static ResponseHolder getInstance() {
        return holder;
    }

    public ConcurrentHashMap<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<>();
}
