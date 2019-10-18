package com.crossoverjie.cim.server.handle;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.exception.CimException;
import com.crossoverjie.cim.common.pojo.CimUserInfo;
import com.crossoverjie.cim.common.protocol.CimRequestProto.CimReqProtocol;
import com.crossoverjie.cim.common.util.NettyAttrUtil;
import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.kit.ServerHeartBeatHandlerImpl;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 17/05/2018 18:52
 * @since JDK 1.8
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class CimServerHandler extends SimpleChannelInboundHandler<CimReqProtocol> {
    @Autowired
    private ServerHeartBeatHandlerImpl serverHeartBeatHandler;
    @Autowired
    private OkHttpClient okHttpClient;
    @Autowired
    private AppConfiguration configuration;
    @Resource(name = "heartBeat")
    private CimReqProtocol cimReqProtocol;
    private final MediaType mediaType = MediaType.parse("application/json");

    /**
     * 取消绑定
     *
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //可能出现业务判断离线后再次触发 channelInactive
        CimUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
        if (userInfo != null) {
            log.warn("[{}]触发 channelInactive 掉线!", userInfo.getUserName());
            SessionSocketHolder.remove((NioSocketChannel) ctx.channel());
            SessionSocketHolder.removeSession(userInfo.getUserId());
            clearRouteInfo(userInfo);
            ctx.channel().close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("定时检测客户端端是否存活");
                serverHeartBeatHandler.process(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    private void clearRouteInfo(CimUserInfo userInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userInfo.getUserId());
            jsonObject.put("msg", "offLine");
            RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(configuration.getClearRouteUrl())
                    .post(requestBody)
                    .build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Unexpected code " + response);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CimReqProtocol msg) {
        log.info("收到msg={}", msg.toString());
        if (msg.getType() == Constants.CommandType.LOGIN) {
            //保存客户端与 Channel 之间的关系
            SessionSocketHolder.put(msg.getRequestId(), (NioSocketChannel) ctx.channel());
            SessionSocketHolder.saveSession(msg.getRequestId(), msg.getReqMsg());
            log.info("客户端[{}]上线成功", msg.getReqMsg());
            return;
        }

        //心跳更新时间
        if (msg.getType() == Constants.CommandType.PING) {
            NettyAttrUtil.updateReaderTime(ctx.channel(), System.currentTimeMillis());
            //向客户端响应 pong 消息
            ctx.writeAndFlush(cimReqProtocol).addListeners((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("IO error,close Channel");
                    future.channel().close();
                }
            });
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (CimException.isResetByPeer(cause.getMessage())) {
            return;
        }
        log.error(cause.getMessage(), cause);
    }

}
