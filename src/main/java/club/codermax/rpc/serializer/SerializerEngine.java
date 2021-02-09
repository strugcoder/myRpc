package club.codermax.rpc.serializer;


import club.codermax.rpc.serializer.impl.DefaultJavaSerializer;
import club.codermax.rpc.serializer.impl.ProtoStuffSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分配不同的序列化方式，设计模式
 *
 * 策略模式
 *
 * 将if-else分支语句优化掉，这更像是一种策略工厂类，使用Map；来缓存策略，
 * 根据指定的loadStrategy直接重缓存中获取对应的策略，从未避免if-else分支判断逻辑（类似算法中的查表法）
 *
 * 运行时动态确定
 */
public class SerializerEngine {
    private static final Map<SerializeType, Serializer> serializerMap = new ConcurrentHashMap<>();

    static {
        serializerMap.put(SerializeType.DefaultJavaSerializer, new DefaultJavaSerializer());
        serializerMap.put(SerializeType.ProtoStuffSerializer, new ProtoStuffSerializer());

    }

    // 通用的序列化方法
    public static <T> byte[] serialize(T obj, String serializerType) {
        SerializeType serializeType = SerializeType.queryByType(serializerType);
        if (serializerType == null) {
            serializeType = SerializeType.DefaultJavaSerializer;
        }
        Serializer serializer = serializerMap.get(serializeType);
        return serializer.serialize(obj);
    }

    // 通用的反序列方法
    public static <T> T deserialize(byte[] data, Class<T> clazz, String serializerType){
        SerializeType serializeType = SerializeType.queryByType(serializerType);
        if (serializeType == null) {
            serializeType = SerializeType.DefaultJavaSerializer;
        }
        Serializer serializer = serializerMap.get(serializeType);
        return serializer.deserialize(data, clazz);
    }

}
