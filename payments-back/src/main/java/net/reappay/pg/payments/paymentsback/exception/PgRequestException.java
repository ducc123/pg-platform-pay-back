package net.reappay.pg.payments.paymentsback.exception;

import java.io.Serializable;

public class PgRequestException extends RuntimeException implements Serializable {

    private String errorCode = "1001";

    public PgRequestException(String message, String errorCode) {

        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
