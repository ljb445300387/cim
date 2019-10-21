package com.crossoverjie.cim.client.vo.res;

import lombok.Data;

import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/26 23:17
 * @since JDK 1.8
 */
@Data
public class OnlineUsersRes {
    /**
     * code : 9000
     * message : 成功
     * reqNo : null
     * dataBody : [{"userId":1545574841528,"userName":"zhangsan"},{"userId":1545574871143,
     * "userName":"crossoverJie"}]
     */
    private String code;
    private String message;
    private Object reqNo;
    private List<DataBodyBean> dataBody;

    @Data
    public static class DataBodyBean {
        private long userId;
        private String userName;
    }
}
