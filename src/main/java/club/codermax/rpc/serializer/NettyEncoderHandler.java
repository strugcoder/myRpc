package club.codermax.rpc.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoderHandler extends MessageToByteEncoder {


    // 序列化类型
    private SerializeType serializeType;

    public NettyEncoderHandler(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) {
        // 将对象序列化成字节数组
        byte[] data = SerializerEngine.serialize(in, serializeType.getSerializeType());
        // 将字节数组（消息体）的长度写入消息头
        out.writeInt(data.length);
        // 写入序列化后的数组
        out.writeBytes(data);
    }
}
