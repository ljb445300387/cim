package com.crossoverjie.cim.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.client.config.AppConfiguration;
import com.crossoverjie.cim.client.service.EchoService;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.req.GroupReq;
import com.crossoverjie.cim.client.vo.req.LoginReq;
import com.crossoverjie.cim.client.vo.req.SingleChatReq;
import com.crossoverjie.cim.client.vo.res.CimServerRes;
import com.crossoverjie.cim.client.vo.res.OnlineUsersRes;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.res.BaseResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/22 22:27
 * @since JDK 1.8
 */
@Service
public class RouteRequestImpl implements RouteRequest {

    private final static Logger LOGGER = LoggerFactory.getLogger(RouteRequestImpl.class);

    @Autowired
    private OkHttpClient okHttpClient;

    private MediaType mediaType = MediaType.parse("application/json");

    @Value("${cim.group.route.request.url}")
    private String groupRouteRequestUrl;

    @Value("${cim.p2p.route.request.url}")
    private String p2pRouteRequestUrl;

    @Value("${cim.server.route.request.url}")
    private String serverRouteLoginUrl;

    @Value("${cim.server.online.user.url}")
    private String onlineUserUrl;
    @Autowired
    private EchoService echoService;
    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void sendGroupMsg(GroupReq groupReq) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", groupReq.getMsg());
        jsonObject.put("userId", groupReq.getUserId());
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .url(groupRouteRequestUrl)
                .post(requestBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    @Override
    public void sendP2PMsg(SingleChatReq singleChatReq) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", singleChatReq.getMsg());
        jsonObject.put("userId", singleChatReq.getUserId());
        jsonObject.put("receiveUserId", singleChatReq.getReceiveUserId());
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .url(p2pRouteRequestUrl)
                .post(requestBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String json = response.body().string();
            BaseResponse baseResponse = JSON.parseObject(json, BaseResponse.class);
            //选择的账号不存在
            if (baseResponse.getCode().equals(StatusEnum.OFF_LINE.getCode())) {
                LOGGER.error(singleChatReq.getReceiveUserId() + ":" + StatusEnum.OFF_LINE.getMessage());
            }
        }
    }

    @Override
    public CimServerRes.ServerInfo getCimServer(LoginReq loginReq) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", loginReq.getUserId());
        jsonObject.put("userName", loginReq.getUserName());
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url(serverRouteLoginUrl)
                .post(requestBody)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected code " + response);
            }
            String json = response.body().string();
            CimServerRes cimServerRes = JSON.parseObject(json, CimServerRes.class);
            //重复失败
            if (!cimServerRes.getCode().equals(StatusEnum.SUCCESS.getCode())) {
                echoService.echo(cimServerRes.getMessage());
                System.exit(-1);
            }
            return cimServerRes.getDataBody();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public List<OnlineUsersRes.DataBodyBean> onlineUsers() throws Exception {
        JSONObject jsonObject = new JSONObject();
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url(onlineUserUrl)
                .post(requestBody)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String json = response.body().string();
            OnlineUsersRes onlineUsersRes = JSON.parseObject(json, OnlineUsersRes.class);
            return onlineUsersRes.getDataBody();
        }
    }

    @Override
    public void offLine() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", appConfiguration.getUserId());
        jsonObject.put("msg", "offLine");
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .url(appConfiguration.getClearRouteUrl())
                .post(requestBody)
                .build();

        try (Response ignored = okHttpClient.newCall(request).execute()) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
