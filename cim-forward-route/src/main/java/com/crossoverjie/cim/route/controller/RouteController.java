package com.crossoverjie.cim.route.controller;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CimException;
import com.crossoverjie.cim.common.pojo.CimUserInfo;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NullBody;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.route.cache.ServerCache;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.route.vo.req.ChatReq;
import com.crossoverjie.cim.route.vo.req.LoginReq;
import com.crossoverjie.cim.route.vo.req.RegisterInfoReq;
import com.crossoverjie.cim.route.vo.req.SingleChatReq;
import com.crossoverjie.cim.route.vo.res.CimServerRes;
import com.crossoverjie.cim.route.vo.res.RegisterInfoRes;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 22/05/2018 14:46
 * @since JDK 1.8
 */
@RestController
@RequestMapping("/")
@Slf4j
public class RouteController {
    @Autowired
    private ServerCache serverCache;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserInfoCacheService userInfoCacheService;
    @Autowired
    private RouteHandle routeHandle;

    @ApiOperation("群聊 API")
    @PostMapping("groupRoute")
    public BaseResponse<NullBody> groupRoute(@RequestBody ChatReq req) throws Exception {
        BaseResponse<NullBody> res = new BaseResponse();
        //获取所有的推送列表
        Map<Long, CimServerRes> serverResVOMap = accountService.loadRouteRelated();
        for (Map.Entry<Long, CimServerRes> cimServerResVOEntry : serverResVOMap.entrySet()) {
            Long userId = cimServerResVOEntry.getKey();
            CimServerRes value = cimServerResVOEntry.getValue();
            if (userId.equals(req.getUserId())) {
                //过滤掉自己
                CimUserInfo cimUserInfo = userInfoCacheService.loadUserInfoByUserId(req.getUserId());
                log.warn("过滤掉了发送者 userId={}", cimUserInfo.toString());
                continue;
            }

            //推送消息
            String url = "http://" + value.getIp() + ":" + value.getHttpPort() + "/sendMsg";
            ChatReq chatVO = new ChatReq(userId, req.getMsg());

            accountService.pushMsg(url, req.getUserId(), chatVO);

        }

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }


    /**
     * 私聊路由
     *
     * @param p2pRequest
     * @return
     */
    @ApiOperation("私聊 API")
    @PostMapping("p2pRoute")
    public BaseResponse<NullBody> p2pRoute(@RequestBody SingleChatReq p2pRequest) throws Exception {
        BaseResponse<NullBody> res = new BaseResponse();

        try {
            //获取接收消息用户的路由信息
            CimServerRes cimServerRes = accountService.loadRouteRelatedByUserId(p2pRequest.getReceiveUserId());
            //推送消息
            String url = "http://" + cimServerRes.getIp() + ":" + cimServerRes.getHttpPort() + "/sendMsg";

            //p2pRequest.getReceiveUserId()==>消息接收者的 userID
            ChatReq chatVO = new ChatReq(p2pRequest.getReceiveUserId(), p2pRequest.getMsg());
            accountService.pushMsg(url, p2pRequest.getUserId(), chatVO);

            res.setCode(StatusEnum.SUCCESS.getCode());
            res.setMessage(StatusEnum.SUCCESS.getMessage());

        } catch (CimException e) {
            res.setCode(e.getErrorCode());
            res.setMessage(e.getErrorMessage());
        }
        return res;
    }


    @ApiOperation("客户端下线")
    @PostMapping("offLine")
    public BaseResponse<NullBody> offLine(@RequestBody ChatReq groupReqVO) throws Exception {
        BaseResponse<NullBody> res = new BaseResponse();

        CimUserInfo cimUserInfo = userInfoCacheService.loadUserInfoByUserId(groupReqVO.getUserId());

        log.info("下线用户[{}]", cimUserInfo.toString());
        accountService.offLine(groupReqVO.getUserId());

        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    @ApiOperation("登录并获取服务器")
    @PostMapping("login")
    public BaseResponse<CimServerRes> login(@RequestBody LoginReq loginReq) throws Exception {
        BaseResponse<CimServerRes> res = new BaseResponse();

        //登录校验
        StatusEnum status = accountService.login(loginReq);
        if (status == StatusEnum.SUCCESS) {

            String server = routeHandle.routeServer(serverCache.getAll(), String.valueOf(loginReq.getUserId()));
            String[] serverInfo = server.split(":");
            CimServerRes vo = new CimServerRes(serverInfo[0], Integer.parseInt(serverInfo[1]), Integer.parseInt(serverInfo[2]));

            //保存路由信息
            accountService.saveRouteInfo(loginReq, server);

            res.setDataBody(vo);

        }
        res.setCode(status.getCode());
        res.setMessage(status.getMessage());

        return res;
    }

    @ApiOperation("注册账号")
    @PostMapping("registerAccount")
    public BaseResponse<RegisterInfoRes> registerAccount(@RequestBody RegisterInfoReq registerInfoReq) throws Exception {
        BaseResponse<RegisterInfoRes> res = new BaseResponse();

        long userId = System.currentTimeMillis();
        RegisterInfoRes info = new RegisterInfoRes(userId, registerInfoReq.getUserName());
        info = accountService.register(info);

        res.setDataBody(info);
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

    @ApiOperation("获取所有在线用户")
    @PostMapping("onlineUser")
    public BaseResponse<Set<CimUserInfo>> onlineUser() {
        BaseResponse<Set<CimUserInfo>> res = new BaseResponse();
        Set<CimUserInfo> cimUserInfos = userInfoCacheService.onlineUser();
        res.setDataBody(cimUserInfos);
        res.setCode(StatusEnum.SUCCESS.getCode());
        res.setMessage(StatusEnum.SUCCESS.getMessage());
        return res;
    }

}
