package club.codermax.rpc.framework;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


// 异步转同步的逻辑
public class MessageCallBack {

    private RpcRequest rpcRequest;
    private RpcResponse rpcResponse;

    private Lock lock = new ReentrantLock();

    private Condition finish = lock.newCondition();

    public MessageCallBack(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    public Object start() throws InterruptedException {
        try {
            lock.lock();

            // 设置一下超时时间，rpc服务器太久没有响应的话，直接返回空,
            // 也就是超过一秒之后，进入条件队列执行，无论是否获得到了响应
            finish.await(10 * 100, TimeUnit.MILLISECONDS);
            if (this.rpcResponse != null) {
                return this.rpcResponse.getData();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void over(RpcResponse rpcResponse) {
        try {
            lock.lock();
            // 获取到了值，然后就会唤醒进入到条件队列
            this.rpcResponse = rpcResponse;
            finish.signal();
        } finally {
            lock.unlock();
        }
    }
}
