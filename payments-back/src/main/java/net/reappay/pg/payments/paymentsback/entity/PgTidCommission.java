package net.reappay.pg.payments.paymentsback.entity;

import lombok.Data;

@Data
public class PgTidCommission {

    private String pgTidCmsSeq;
    private String pgTid;
    private String commission;
    private String startDt;
    private String endDt;
    private String useFlag;
    private String insDt;
    private String insUser;
}
