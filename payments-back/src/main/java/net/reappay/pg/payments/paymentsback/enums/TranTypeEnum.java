package net.reappay.pg.payments.paymentsback.enums;

public enum TranTypeEnum {
    APPROVAL_REQUEST("승인요청", "10"),
    PURCHASE_REQUEST("매입요청", "20"),
    PURCHASE_CANCEL("매입취소", "30"),
    CANCEL_REQUEST("취소요청", "40"),
    NETWORK_CANCEL("망상취소", "50");

    private String desc;
    private String code;

    TranTypeEnum(String desc, String code) {
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
