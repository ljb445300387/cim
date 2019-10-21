package com.crossoverjie.cim.client.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Function: Google Protocol 编解码发送
 *
 * @author crossoverJie
 * Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
@Data
public class GoogleProtocol extends BaseRequest {
    @NotNull(message = "requestId 不能为空")
    @ApiModelProperty(required = true, value = "requestId", example = "123")
    private Integer requestId;

    @NotNull(message = "msg 不能为空")
    @ApiModelProperty(required = true, value = "msg", example = "hello")
    private String msg;
}
