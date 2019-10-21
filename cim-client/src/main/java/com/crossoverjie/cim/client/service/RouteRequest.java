package com.crossoverjie.cim.client.service;

import com.crossoverjie.cim.client.vo.req.GroupReq;
import com.crossoverjie.cim.client.vo.req.LoginReq;
import com.crossoverjie.cim.client.vo.req.SingleChatReq;
import com.crossoverjie.cim.client.vo.res.CimServerRes;
import com.crossoverjie.cim.client.vo.res.OnlineUsersRes;

import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/22 22:26
 * @since JDK 1.8
 */
public interface RouteRequest {

    /**
     * 群发消息
     *
     * @param groupReq 消息
     * @throws Exception
     */
    void sendGroupMsg(GroupReq groupReq) throws Exception;


    /**
     * 私聊
     *
     * @param singleChatReq
     * @throws Exception
     */
    void sendP2PMsg(SingleChatReq singleChatReq) throws Exception;

    /**
     * 获取服务器
     *
     * @param loginReq
     * @return 服务ip+port
     * @throws Exception
     */
    CimServerRes.ServerInfo getCimServer(LoginReq loginReq);

    /**
     * 获取所有在线用户
     *
     * @return
     * @throws Exception
     */
    List<OnlineUsersRes.DataBodyBean> onlineUsers() throws Exception;


    void offLine();

}
