package com.crossoverjie.cim.client.init;

import com.crossoverjie.cim.client.handler.CimClientHandler;
import com.crossoverjie.cim.common.protocol.CimResponseProto.CimResProtocol;
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
 * Date: 23/02/2018 22:47
 * @since JDK 1.8
 */
@Component
public class CimClientHandleInitializer extends ChannelInitializer<Channel> {
    @Autowired
    private CimClientHandler cimClientHandler;

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                //10 秒没发送消息 将IdleStateHandler 添加到 ChannelPipeline 中
                .addLast(new IdleStateHandler(0, 10, 0))

                //心跳解码
                //.addLast(new HeartbeatEncode())

                // google Protobuf 编解码
                //拆包解码
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(CimResProtocol.getDefaultInstance()))
                //
                //拆包编码
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(cimClientHandler);
    }
}
