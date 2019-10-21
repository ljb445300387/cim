package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.service.CustomMsgHandleListener;
import com.crossoverjie.cim.client.service.MsgLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Function:自定义收到消息回调
 *
 * @author crossoverJie
 * Date: 2019/1/6 17:49
 * @since JDK 1.8
 */
@Component
public class MsgCallBackListener implements CustomMsgHandleListener {

    @Autowired
    private MsgLogger msgLogger;

    @Override
    public void handle(String msg) {
        msgLogger.log(msg);
    }
}
