package com.crossoverjie.cim.server.kit;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.pojo.CimUserInfo;
import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-20 17:20
 * @since JDK 1.8
 */
@Component
@Slf4j
public class RouteHandler {
    private final MediaType mediaType = MediaType.parse("application/json");
    @Autowired
    private AppConfiguration configuration;

    public void userOffLine(CimUserInfo userInfo, NioSocketChannel channel) {
        if (userInfo != null) {
            SessionSocketHolder.removeSession(userInfo.getUserId());
            clearRouteInfo(userInfo);
        }
        SessionSocketHolder.remove(channel);

    }

    private void clearRouteInfo(CimUserInfo userInfo) {
        try {
            OkHttpClient okHttpClient = SpringBeanFactory.getBean(OkHttpClient.class);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userInfo.getUserId());
            jsonObject.put("msg", "offLine");
            RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(configuration.getClearRouteUrl())
                    .post(requestBody)
                    .build();
            try (Response response = okHttpClient.newCall(request).execute();) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
