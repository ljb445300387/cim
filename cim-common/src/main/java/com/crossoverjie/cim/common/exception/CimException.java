package com.crossoverjie.cim.common.exception;


import com.crossoverjie.cim.common.enums.StatusEnum;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/8/25 15:26
 * @since JDK 1.8
 */
public class CimException extends GenericException {

    public static final String BY_PEER = "Connection reset by peer";

    public CimException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public CimException(Exception e, String errorCode, String errorMessage) {
        super(e, errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public CimException(String message) {
        super(message);
        this.errorMessage = message;
    }

    public CimException(StatusEnum statusEnum) {
        super(statusEnum.getMessage());
        this.errorMessage = statusEnum.message();
        this.errorCode = statusEnum.getCode();
    }

    public CimException(StatusEnum statusEnum, String message) {
        super(message);
        this.errorMessage = message;
        this.errorCode = statusEnum.getCode();
    }

    public CimException(Exception oriEx) {
        super(oriEx);
    }

    public CimException(Throwable oriEx) {
        super(oriEx);
    }

    public CimException(String message, Exception oriEx) {
        super(message, oriEx);
        this.errorMessage = message;
    }

    public CimException(String message, Throwable oriEx) {
        super(message, oriEx);
        this.errorMessage = message;
    }


    public static boolean isResetByPeer(String msg) {
        if (BY_PEER.equals(msg)) {
            return true;
        }
        return false;
    }

}
