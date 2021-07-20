package net.reappay.pg.payments.paymentsback.enums;

public enum PayMethodEnum {
    CARD("신용카드", "card"),
    BANK("실시간계좌이체", "bank"),
    VIRTUALBANK("가상계좌", "vbank"),
    PHONE("휴대폰결제", "phone");

    private String desc;
    private String code;

    PayMethodEnum(String desc, String code) {
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
