package net.reappay.pg.payments.paymentsback.enums;

public enum ResultCodeEnum {

    NORMAL_RESULT("정상승인", "0000"),
    FAIL_RESULT("승인실패","9999"),
    NETWORK_CANCEL("망상취소", "6666");

    private String desc;
    private String code;

    ResultCodeEnum(String desc, String code) {
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
