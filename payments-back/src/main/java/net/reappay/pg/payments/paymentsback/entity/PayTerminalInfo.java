package net.reappay.pg.payments.paymentsback.entity;

import lombok.Data;

@Data
public class PayTerminalInfo {

    private String payMtdSeq;
    private String startDt;
    private String endDt;
    private String appIssCd;
    private String tidMtd;
    private String terminalNo;
    private String payMtd;
    private String merchNo;
    private String terminalPwd;
    private String fieldKey;
    private String fieldIv;
}
