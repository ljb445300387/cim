package com.crossoverjie.cim.client.handler;

import com.crossoverjie.cim.client.service.CustomMsgHandleListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Function:消息回调 bean
 *
 * @author crossoverJie
 * Date: 2018/12/26 17:37
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@Component
public class MsgHandleCaller {

    /**
     * 回调接口
     */
    private CustomMsgHandleListener msgHandleListener;
}
