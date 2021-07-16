package net.reappay.pg.payments.paymentsback.dto;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class OrderDto {

    private String storeId;
    private String orderNumber;
    private String productType;
    private Integer totAmt;
    private Integer freeAmt;
    private String goodsName;
    private Integer vatAmt;
    private String custId;
    private String apprType;
    private String certiType;
    private String pgMerchNo;
    private String pgTid;
    private Enum payMethod;
    private String orderName;
    private String goodsCode;
    private String custIp;
    private String custName;
    private String custEmail;
    private String custPhone;
    private String orderDate;
    private String orderTime;
    private String userCate;
    private String payMtdSeq;


    private String expDate;
    private String installment;
    private String currencyType;
    private String merchantNo;
    private String returnUrl;       // 결제 성공 후 데이터 처리를 위한 서버 URL
    private String successUrl;      // 결제 성공 후 REDIRECT URL (필수)
    private String failtureUrl;      // 결제 실패 후 REDIRECT URL (없을경우 이동하지 않음)
    private String etcData1;        // 기타 추가 데이터 (결제요청시 받은 데이터를 그대로 되돌려줌)
    private String etcData2;
    private String etcData3;
    private String etcData4;
    private String etcData5;

    private String userId;

    private Date tranDate;
    private Time tranTime;

    private String resultMsg;
    private String resultCode;

    private String tranSeq;
    private String tranType;

}
