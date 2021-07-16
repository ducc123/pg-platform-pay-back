package net.reappay.pg.payments.paymentsback.dto;

import lombok.Data;
import net.reappay.pg.payments.paymentsback.dto.OrderDto;

import java.sql.Date;
import java.sql.Time;

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
    private String amount;
    private String merchantNo;
    private String authSendType;
    private String approvalSendType;
    private String point1;
    private String point2;
    private String point3;
    private String point4;
    private String vanTransactionNo;
    private String filler;
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
    private int vatAmt;

    private String tranType;

    private String ApprovalMsg;
    private String ApprovalCode;

    private String userSeq;
    private String PgSeq;
    private String custId;

    public void setorderDate(OrderDto orderDto) {
        this.orderDate = orderDto.getOrderDate();
    }

    public void setorderTime(OrderDto orderDto) {
        this.orderTime = orderDto.getOrderTime();
    }

    public void setfreeAmt(OrderDto orderDto) {
        this.freeAmt = orderDto.getFreeAmt();
    }

    public void setvatAmt(OrderDto orderDto) {
        this.vatAmt = orderDto.getVatAmt();
    }

    public void settotAmt(OrderDto orderDto) {
        this.totAmt = orderDto.getTotAmt();
    }

    public void setcurrency(OrderDto orderDto) {
        this.currency = orderDto.getCurrencyType();
    }

    private String currency;
    private String payChnCate;

    private int splAmt;
    private int svcAmt;
    private String tranStatus;
    private int totAmt;



}
