package com.crossoverjie.cim.client.vo.res;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/23 00:43
 * @since JDK 1.8
 */
@Data
public class CimServerRes implements Serializable {

    private String code;
    private String message;
    private Object reqNo;
    private ServerInfo dataBody;

    @Data
    public static class ServerInfo {
        private String ip;
        private Integer cimServerPort;
        private Integer httpPort;
    }

}
