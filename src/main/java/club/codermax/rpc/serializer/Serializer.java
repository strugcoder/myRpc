package club.codermax.rpc.serializer;


/**
 * 策略的定义
 *
 * 一个策略接口和一组实现这个接口对应的实现类
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);


    /**
     * 反序列化
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
