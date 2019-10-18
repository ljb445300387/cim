package com.crossoverjie.cim.server.init;

import com.crossoverjie.cim.common.protocol.CimRequestProto;
import com.crossoverjie.cim.server.handle.CimServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 17/05/2018 18:51
 * @since JDK 1.8
 */
@Component
public class CimServerInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private CimServerHandler cimServerHandler;

    @Override
    protected void initChannel(Channel ch) {

        ch.pipeline()
                //11 秒没有向客户端发送消息就发生心跳
                .addLast(new IdleStateHandler(11, 0, 0))
                // google Protobuf 编解码
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(CimRequestProto.CimReqProtocol.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(cimServerHandler);
    }
}
