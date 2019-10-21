package com.crossoverjie.cim.client.client;

import com.crossoverjie.cim.client.config.AppConfiguration;
import com.crossoverjie.cim.client.init.CimClientHandleInitializer;
import com.crossoverjie.cim.client.service.EchoService;
import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.service.impl.ClientInfo;
import com.crossoverjie.cim.client.vo.req.GoogleProtocol;
import com.crossoverjie.cim.client.vo.req.LoginReq;
import com.crossoverjie.cim.client.vo.res.CimServerRes.ServerInfo;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.protocol.CimRequestProto.CimReqProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 22/05/2018 14:19
 * @since JDK 1.8
 */
@Component
@Slf4j
public class CimClient {
    private EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("cim-work"));
    @Value("${cim.user.id}")
    private long userId;
    @Value("${cim.user.userName}")
    private String userName;
    private SocketChannel channel;
    @Autowired
    private EchoService echoService;
    @Autowired
    private RouteRequest routeRequest;
    @Autowired
    private AppConfiguration configuration;
    @Autowired
    private MsgHandle msgHandle;
    @Autowired
    private ClientInfo clientInfo;
    /**
     * 重试次数
     */
    private int errorCount;
    @Autowired
    private CimClientHandleInitializer handler;

    @PostConstruct
    public void start() {
        //登录 + 获取可以使用的服务器 ip+port
        ServerInfo cimServer = routeRequest.getCimServer(new LoginReq(userId, userName));
        //保存系统信息
        clientInfo.saveServiceInfo(cimServer.getIp() + ":" + cimServer.getCimServerPort()).saveUserInfo(userId, userName);
        log.info("cimServer=[{}]", cimServer);
        ServerInfo serverInfo = cimServer;
        //启动客户端
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(handler);
            ChannelFuture future1 = bootstrap.connect(serverInfo.getIp(), serverInfo.getCimServerPort()).sync();
            if (future1.isSuccess()) {
                echoService.echo("start cim client success!");
                log.info("启动 cim client 成功");
            }
            channel = (SocketChannel) future1.channel();
        } catch (InterruptedException e) {
            log.error("连接失败", e);
        }
        //向服务端注册
        CimReqProtocol login = CimReqProtocol.newBuilder()
                .setRequestId(userId)
                .setReqMsg(userName)
                .setType(Constants.CommandType.LOGIN)
                .build();
        ChannelFuture future = channel.writeAndFlush(login);
        future.addListener((ChannelFutureListener) channelFuture -> echoService.echo("registry cim server success!"));
    }

    public void sendStringMsg(String msg) {
        ByteBuf message = Unpooled.buffer(msg.getBytes().length);
        message.writeBytes(msg.getBytes());
        ChannelFuture future = channel.writeAndFlush(message);
        future.addListener((ChannelFutureListener) channelFuture -> log.info("客户端手动发消息成功={}", msg));
    }

    /**
     * 发送 Google Protocol 编解码字符串
     *
     * @param googleProtocol
     */
    public void sendGoogleProtocolMsg(GoogleProtocol googleProtocol) {

        CimReqProtocol protocol = CimReqProtocol.newBuilder()
                .setRequestId(googleProtocol.getRequestId())
                .setReqMsg(googleProtocol.getMsg())
                .setType(Constants.CommandType.MSG)
                .build();
        ChannelFuture future = channel.writeAndFlush(protocol);
        future.addListener((ChannelFutureListener) channelFuture -> log.info("客户端手动发送 Google Protocol 成功={}", googleProtocol.toString()));

    }


    public void reconnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        //首先清除路由信息，下线
        routeRequest.offLine();

        log.info("reconnect....");
        start();
        log.info("reconnect success");
    }

    /**
     * 关闭
     *
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        if (channel != null) {
            channel.close();
        }
    }
}
