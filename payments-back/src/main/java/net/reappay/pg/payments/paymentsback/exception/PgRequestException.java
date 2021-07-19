package net.reappay.pg.payments.paymentsback.exception;

import java.io.Serializable;

public class PgRequestException extends RuntimeException implements Serializable {

    private Integer errorCode = 1001;

    public PgRequestException(String message, Integer errorCode) {

        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
