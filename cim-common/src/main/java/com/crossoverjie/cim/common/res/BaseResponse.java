package com.crossoverjie.cim.common.res;


import com.crossoverjie.cim.common.enums.StatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author admin
 */
@Data
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {
    private String code;

    private String message;

    private String reqNo;

    private T dataBody;

    public BaseResponse(T dataBody) {
        this.dataBody = dataBody;
    }

    public BaseResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(String code, String message, T dataBody) {
        this.code = code;
        this.message = message;
        this.dataBody = dataBody;
    }

    public BaseResponse(String code, String message, String reqNo, T dataBody) {
        this.code = code;
        this.message = message;
        this.reqNo = reqNo;
        this.dataBody = dataBody;
    }

    public static <T> BaseResponse<T> create(T t) {
        return new BaseResponse<T>(t);
    }

    public static <T> BaseResponse<T> create(T t, StatusEnum statusEnum) {
        return new BaseResponse<T>(statusEnum.getCode(), statusEnum.getMessage(), t);
    }

    public static <T> BaseResponse<T> createSuccess(T t, String message) {
        return new BaseResponse<T>(StatusEnum.SUCCESS.getCode(), StringUtils.isBlank(message) ? StatusEnum.SUCCESS.getMessage() : message, t);
    }

    public static <T> BaseResponse<T> createFail(T t, String message) {
        return new BaseResponse<T>(StatusEnum.FAIL.getCode(), StringUtils.isBlank(message) ? StatusEnum.FAIL.getMessage() : message, t);
    }

    public static <T> BaseResponse<T> create(T t, StatusEnum statusEnum, String message) {

        return new BaseResponse<T>(statusEnum.getCode(), message, t);
    }

}
