package com.crossoverjie.cim.client.handler;

import com.crossoverjie.cim.client.service.EchoService;
import com.crossoverjie.cim.client.service.ShutDownMsg;
import com.crossoverjie.cim.client.service.impl.ClientHeartBeatHandlerImpl;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.protocol.CimRequestProto.CimReqProtocol;
import com.crossoverjie.cim.common.protocol.CimResponseProto;
import com.crossoverjie.cim.common.util.NettyAttrUtil;
import com.vdurmont.emoji.EmojiParser;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 16/02/2018 18:09
 * @since JDK 1.8
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class CimClientHandler extends SimpleChannelInboundHandler<CimResponseProto.CimResProtocol> {
    @Autowired
    private MsgHandleCaller caller;
    @Resource(name = "callBackThreadPool")
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource(name = "scheduledTask")
    private ScheduledExecutorService scheduledExecutorService;
    @Autowired
    private ShutDownMsg shutDownMsg;
    @Autowired
    private EchoService echoService;
    @Resource(name = "heartBeat")
    private CimReqProtocol heartBeat;
    @Autowired
    private ClientHeartBeatHandlerImpl clientHeartBeatHandler;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (!(evt instanceof IdleStateEvent)) {
            return;
        }
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
        log.info("定时检测服务端是否存活");
        if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
            ctx.writeAndFlush(heartBeat).addListeners((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("IO error,close Channel");
                    future.channel().close();
                }
            });
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //客户端和服务端建立连接时调用
        log.info("cim server connect success!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        //用户主动退出，不执行重连逻辑
        if (shutDownMsg.checkStatus()) {
            return;
        }
        log.info("客户端断开了，重新连接！");
        // TODO: 2019-01-22 后期可以改为不用定时任务，连上后就关闭任务 节省性能。
        scheduledExecutorService.scheduleAtFixedRate(() -> clientHeartBeatHandler.process(ctx), 0, 10, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CimResponseProto.CimResProtocol msg) {
        //心跳更新时间
        if (msg.getType() == Constants.CommandType.PING) {
            log.info("收到服务端心跳！！！");
            NettyAttrUtil.updateReaderTime(ctx.channel(), System.currentTimeMillis());
            return;
        }

        if (msg.getType() != Constants.CommandType.PING) {
            //回调消息
            callBackMsg(msg.getResMsg());
            //将消息中的 emoji 表情格式化为 Unicode 编码以便在终端可以显示
            echoService.echo(EmojiParser.parseToUnicode(msg.getResMsg()));
        }


    }

    /**
     * 回调消息
     *
     * @param msg
     */
    private void callBackMsg(String msg) {
        threadPoolExecutor.execute(() -> caller.getMsgHandleListener().handle(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //异常时断开连接
        log.error("", cause);
        ctx.close();
    }
}
