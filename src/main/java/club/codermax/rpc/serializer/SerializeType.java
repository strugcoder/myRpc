package club.codermax.rpc.serializer;


/**
 * 序列化的类型
 */
public enum SerializeType {

    DefaultJavaSerializer("Java"),
    ProtoStuffSerializer("ProtoStuff");

    private String serializeType;

    SerializeType(String serializeType) {
        this.serializeType = serializeType;
    }

    public String getSerializeType() {
        return serializeType;
    }

    public static SerializeType queryByType(String serializeType) {
        if ("".equals(serializeType) || serializeType == null)
            return null;


        for (SerializeType serialize : SerializeType.values()) {
            if (serializeType.equals(serialize.getSerializeType())) {
                return serialize;
            }
        }
        return null;
    }
}
