package net.reappay.pg.payments.paymentsback.enums;

public enum TranStepEnum {
    TRAN_COMPLETE("거래완료", "00"),
    PURCHASE_REQUEST("매입요청", "20"),
    RETURN("반송", "40"),
    PURCHASE_COMPLETE("매입완료", "50"),
    PURCHASE_HOLD("매입보류", "60");

    private String desc;
    private String code;

    TranStepEnum(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }

    public String desc() {
        return this.desc;
    }

    public String code() {
        return this.code;
    }

}
