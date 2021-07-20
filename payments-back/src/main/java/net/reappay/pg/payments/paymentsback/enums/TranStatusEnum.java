package net.reappay.pg.payments.paymentsback.enums;

public enum TranStatusEnum {
    APPROVAL("승인", "00"),
    CANCEL_REQUEST("취소요청", "05"),
    DAY_CANCEL("당일취소", "10"),
    PURCHASE_CANCEL("매입취소", "20"),
    NETWORK_CANCEL("망상취소", "90");

    private String desc;
    private String code;

    TranStatusEnum(String desc, String code) {
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
