package com.crossoverjie.cim.route.vo.req;

import com.crossoverjie.cim.common.req.BaseRequest;
import lombok.Data;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 22:30
 * @since JDK 1.8
 */
@Data
public class LoginReq extends BaseRequest {
    private Long userId;
    private String userName;
}
