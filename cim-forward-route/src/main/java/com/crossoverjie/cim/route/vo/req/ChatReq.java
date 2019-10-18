package com.crossoverjie.cim.route.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Function: Google Protocol 编解码发送
 *
 * @author crossoverJie
 * Date: 2018/05/21 15:56
 * @since JDK 1.8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatReq extends BaseRequest {

    @NotNull(message = "userId 不能为空")
    @ApiModelProperty(required = true, value = "userId", example = "1545574049323")
    private Long userId;


    @NotNull(message = "msg 不能为空")
    @ApiModelProperty(required = true, value = "msg", example = "hello")
    private String msg;
}
