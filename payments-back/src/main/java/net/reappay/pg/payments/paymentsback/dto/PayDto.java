package net.reappay.pg.payments.paymentsback.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PayDto {

    private String apprType;
    private String certiType;
    private String authty;
    private String interest;
    private String passwd;
    private String lastTidNum;
    private String currencyType;
    private String status;
    private String ipAddr;
    private String tradeDate;
    private String tradeTime;
    private String issCode;
    private String aquCode;
    private String cavv;
    private String xid;
    private String eci;
    private String kbAppOtp;
    private String KVPNOINT;
    private String KVPQUOTA;
    private String KVPPGID;
    private String KVPCARDCODE;
    private String KVPSESSIONKEY;
    private String KVPENCDATA;
    private String authNo;
    private String message1;
    private String message2;
    private String cardNo;
    private String cardCode;
    private String expDate;
    private String installment;
    private BigDecimal amount;
    private String productType;
    private String merchantNo;
    private String authSendType;
    private String approvalSendType;
    private String point1;
    private String point2;
    private String point3;
    private String point4;
    private String vanTransactionNo;
    private String pgSeq;
    private String authType;
    private String MPIPositionType;
    private String MPIReUseType;
    private String EncData;
    private String resultCode;
    private String resultMsg;
    private String etcData1;
    private String etcData2;
    private String etcData3;
    private String etcData4;
    private String etcData5;

    private String tranSeq;
    private String tableYd;

    private String orderDate;
    private String orderTime;

    private int freeAmt;
    private BigDecimal vatAmt;
    private BigDecimal svcAmt;
    private BigDecimal splAmt;
    private BigDecimal totAmt;

    private String tranType;
    private String ApprovalMsg;
    private String ApprovalCode;

    private String userSeq;
    private String PgSeq;
    private String custId;
    private String custName;
    private String custPhone;
    private String custIp;
    private String custEmail;
    private String goodsName;
    private String goodsCode;
    private String orderNumber;
    private String pgMerchNo;
    private String pgTid;

    private String payChnCate;
    private String tranStatus;
    private Enum payMethod;
    private String storeId;
    private String codeNm;
    private String cardIssuNm;
    private int notiTryCnt;
    private String notiStatus;
    private int tranChkFlag;
    private int acqrChkFlag;
    private String billkeyYn;
    private String tranCate;
    private String acqrStatus;
    private String dpstStatus;
    private String stlmStatus;

}
