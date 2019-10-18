package com.crossoverjie.cim.server.server;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.protocol.CimRequestProto;
import com.crossoverjie.cim.server.init.CimServerInitializer;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import com.crossoverjie.cim.server.vo.req.SendMsgReqVO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 21/05/2018 00:30
 * @since JDK 1.8
 */
@Component
@Slf4j
public class CimServer {
    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();

    @Value("${cim.server.port}")
    private int nettyPort;

    /**
     * 启动 cim server
     *
     * @return
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(nettyPort))
                //保持长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new CimServerInitializer());

        ChannelFuture future = bootstrap.bind().sync();
        if (future.isSuccess()) {
            log.info("启动 cim server 成功");
        }
    }


    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        boss.shutdownGracefully().syncUninterruptibly();
        work.shutdownGracefully().syncUninterruptibly();
        log.info("关闭 cim server 成功");
    }


    /**
     * 发送 Google Protocol 编码消息
     *
     * @param sendMsgReqVO 消息
     */
    public void sendMsg(SendMsgReqVO sendMsgReqVO) {
        NioSocketChannel socketChannel = SessionSocketHolder.get(sendMsgReqVO.getUserId());

        if (null == socketChannel) {
            throw new NullPointerException(String.format("客户端[%d]不在线！", sendMsgReqVO.getUserId()));
        }
        CimRequestProto.CimReqProtocol protocol = CimRequestProto.CimReqProtocol.newBuilder()
                .setRequestId(sendMsgReqVO.getUserId())
                .setReqMsg(sendMsgReqVO.getMsg())
                .setType(Constants.CommandType.MSG)
                .build();

        ChannelFuture future = socketChannel.writeAndFlush(protocol);
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("服务端手动发送 Google Protocol 成功={}", sendMsgReqVO.toString()));
    }
}
