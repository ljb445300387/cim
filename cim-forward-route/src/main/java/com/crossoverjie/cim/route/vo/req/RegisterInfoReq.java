package com.crossoverjie.cim.route.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 22:04
 * @since JDK 1.8
 */
@Data
public class RegisterInfoReq extends BaseRequest {
    @NotNull(message = "用户名不能为空")
    @ApiModelProperty(required = true, value = "userName", example = "zhangsan")
    private String userName;
}
