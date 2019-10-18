package com.crossoverjie.cim.route.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CimException;
import com.crossoverjie.cim.common.pojo.CimUserInfo;
import com.crossoverjie.cim.route.service.AccountService;
import com.crossoverjie.cim.route.service.UserInfoCacheService;
import com.crossoverjie.cim.route.vo.req.ChatReq;
import com.crossoverjie.cim.route.vo.req.LoginReq;
import com.crossoverjie.cim.route.vo.res.CimServerRes;
import com.crossoverjie.cim.route.vo.res.RegisterInfoRes;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.crossoverjie.cim.common.enums.StatusEnum.OFF_LINE;
import static com.crossoverjie.cim.route.constant.Constant.ACCOUNT_PREFIX;
import static com.crossoverjie.cim.route.constant.Constant.ROUTE_PREFIX;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 21:58
 * @since JDK 1.8
 */
@Service
@Slf4j
public class AccountServiceRedisImpl implements AccountService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserInfoCacheService userInfoCacheService;
    @Autowired
    private OkHttpClient okHttpClient;
    private MediaType mediaType = MediaType.parse("application/json");

    @Override
    public RegisterInfoRes register(RegisterInfoRes info) {
        String key = ACCOUNT_PREFIX + info.getUserId();

        String name = redisTemplate.opsForValue().get(info.getUserName());
        if (null == name) {
            //为了方便查询，冗余一份
            redisTemplate.opsForValue().set(key, info.getUserName());
            redisTemplate.opsForValue().set(info.getUserName(), key);
        } else {
            long userId = Long.parseLong(name.split(":")[1]);
            info.setUserId(userId);
            info.setUserName(info.getUserName());
        }

        return info;
    }

    @Override
    public StatusEnum login(LoginReq loginReq) throws Exception {
        //再去Redis里查询
        String key = ACCOUNT_PREFIX + loginReq.getUserId();
        String userName = redisTemplate.opsForValue().get(key);
        if (null == userName) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }

        if (!userName.equals(loginReq.getUserName())) {
            return StatusEnum.ACCOUNT_NOT_MATCH;
        }

        //登录成功，保存登录状态
        boolean status = userInfoCacheService.saveAndCheckUserLoginStatus(loginReq.getUserId());
        if (status == false) {
            //重复登录
            return StatusEnum.REPEAT_LOGIN;
        }

        return StatusEnum.SUCCESS;
    }

    @Override
    public void saveRouteInfo(LoginReq loginReq, String msg) throws Exception {
        String key = ROUTE_PREFIX + loginReq.getUserId();
        redisTemplate.opsForValue().set(key, msg);
    }

    @Override
    public Map<Long, CimServerRes> loadRouteRelated() {
        Map<Long, CimServerRes> routes = new HashMap<>(64);
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        ScanOptions options = ScanOptions.scanOptions().match(ROUTE_PREFIX + "*").build();
        Cursor<byte[]> scan = connection.scan(options);
        while (scan.hasNext()) {
            byte[] next = scan.next();
            String key = new String(next, StandardCharsets.UTF_8);
            log.info("key={}", key);
            parseServerInfo(routes, key);
        }
        try {
            scan.close();
        } catch (IOException e) {
            log.error("IOException", e);
        }
        return routes;
    }

    @Override
    public CimServerRes loadRouteRelatedByUserId(Long userId) {
        String value = redisTemplate.opsForValue().get(ROUTE_PREFIX + userId);

        if (value == null) {
            throw new CimException(OFF_LINE);
        }

        String[] server = value.split(":");
        CimServerRes cimServerRes = new CimServerRes(server[0], Integer.parseInt(server[1]), Integer.parseInt(server[2]));
        return cimServerRes;
    }

    private void parseServerInfo(Map<Long, CimServerRes> routes, String key) {
        long userId = Long.valueOf(key.split(":")[1]);
        String value = redisTemplate.opsForValue().get(key);
        String[] server = value.split(":");
        CimServerRes cimServerRes = new CimServerRes(server[0], Integer.parseInt(server[1]), Integer.parseInt(server[2]));
        routes.put(userId, cimServerRes);
    }


    @Override
    public void pushMsg(String url, long sendUserId, ChatReq groupReqVO) throws Exception {
        CimUserInfo cimUserInfo = userInfoCacheService.loadUserInfoByUserId(sendUserId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", cimUserInfo.getUserName() + ":" + groupReqVO.getMsg());
        jsonObject.put("userId", groupReqVO.getUserId());
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(mediaType, jsonObject.toString()))
                .build();
        try (Response response = okHttpClient.newCall(request).execute();) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }

    @Override
    public void offLine(Long userId) throws Exception {

        // TODO: 2019-01-21 改为一个原子命令，以防数据一致性

        //删除路由
        redisTemplate.delete(ROUTE_PREFIX + userId);

        //删除登录状态
        userInfoCacheService.removeLoginStatus(userId);
    }
}
