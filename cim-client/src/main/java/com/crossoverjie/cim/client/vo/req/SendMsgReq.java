package com.crossoverjie.cim.client.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
@Data
public class SendMsgReq extends BaseRequest {

    @NotNull(message = "msg 不能为空")
    @ApiModelProperty(required = true, value = "msg", example = "hello")
    private String msg;

    @NotNull(message = "userId 不能为空")
    @ApiModelProperty(required = true, value = "userId", example = "11")
    private Long userId;
}
