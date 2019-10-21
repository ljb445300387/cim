package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.client.CimClient;
import com.crossoverjie.cim.common.kit.HeartBeatHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-20 17:16
 * @since JDK 1.8
 */
@Service
public class ClientHeartBeatHandlerImpl implements HeartBeatHandler {
    @Autowired
    private CimClient cimClient;

    @Override
    public void process(ChannelHandlerContext ctx) {
        cimClient.reconnect();
    }

}
