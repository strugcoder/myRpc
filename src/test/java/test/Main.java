package test;

import club.codermax.rpc.serializer.Serializer;
import club.codermax.rpc.serializer.impl.ProtoStuffSerializer;

public class Main {

    public static void main(String[] args) {
        LazyDoubleCheckSingleton singleton = LazyDoubleCheckSingleton.getInstance();

        Serializer serializer =  new ProtoStuffSerializer();
        byte[] serialize = serializer.serialize(singleton);
        LazyDoubleCheckSingleton deserialize = serializer.deserialize(serialize, LazyDoubleCheckSingleton.class);

        System.out.println(deserialize == singleton);
    }
}
