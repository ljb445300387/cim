package com.crossoverjie.cim.client.controller;

import com.crossoverjie.cim.client.client.CimClient;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.req.GoogleProtocol;
import com.crossoverjie.cim.client.vo.req.GroupReq;
import com.crossoverjie.cim.client.vo.req.SendMsgReq;
import com.crossoverjie.cim.client.vo.req.StringReq;
import com.crossoverjie.cim.client.vo.res.SendMsgRes;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NullBody;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 14:46
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/")
public class IndexController {

    /**
     * 统计 service
     */
    @Autowired
    private CounterService counterService;

    @Autowired
    private CimClient heartbeatClient ;



    @Autowired
    private RouteRequest routeRequest ;


    /**
     * 向服务端发消息 字符串
     * @param stringReq
     * @return
     */
    @ApiOperation("客户端发送消息，字符串")
    @RequestMapping(value = "sendStringMsg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<NullBody> sendStringMsg(@RequestBody StringReq stringReq){
        BaseResponse<NullBody> res = new BaseResponse();

        for (int i = 0; i < 100; i++) {
            heartbeatClient.sendStringMsg(stringReq.getMsg()) ;
        }

        // 利用 actuator 来自增
        counterService.increment(Constants.COUNTER_CLIENT_PUSH_COUNT);

        SendMsgRes sendMsgRes = new SendMsgRes() ;
        sendMsgRes.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }

    /**
     * 向服务端发消息 Google ProtoBuf
     * @param googleProtocol
     * @return
     */
    @ApiOperation("向服务端发消息 Google ProtoBuf")
    @RequestMapping(value = "sendProtoBufMsg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<NullBody> sendProtoBufMsg(@RequestBody GoogleProtocol googleProtocol){
        BaseResponse<NullBody> res = new BaseResponse();

        for (int i = 0; i < 100; i++) {
            heartbeatClient.sendGoogleProtocolMsg(googleProtocol) ;
        }

        // 利用 actuator 来自增
        counterService.increment(Constants.COUNTER_CLIENT_PUSH_COUNT);

        SendMsgRes sendMsgRes = new SendMsgRes() ;
        sendMsgRes.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }



    /**
     * 群发消息
     * @param sendMsgReq
     * @return
     */
    @ApiOperation("群发消息")
    @RequestMapping(value = "sendGroupMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse sendGroupMsg(@RequestBody SendMsgReq sendMsgReq) throws Exception {
        BaseResponse<NullBody> res = new BaseResponse();

        GroupReq groupReq = new GroupReq(sendMsgReq.getUserId(), sendMsgReq.getMsg()) ;
        routeRequest.sendGroupMsg(groupReq) ;

        counterService.increment(Constants.COUNTER_SERVER_PUSH_COUNT);

        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        return res ;
    }
}
