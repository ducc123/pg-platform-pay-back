package net.reappay.pg.payments.paymentsback.entity;

import lombok.Data;

@Data
public class PayTidInfo {

    private String bizNo;
    private String corpNo;
    private String compNm;
    private String pgMerchNo;
    private String merchNm;
    private String pgTid;
    private String pgTidNm;
    private String certType;
    private String taxFlag;
    private String payMtdSeq;
    private String payChnCate;
    private String appreqChkAmt;
    private String svcStat;
    private String pgSvcStat;
}
