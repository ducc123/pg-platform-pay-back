package net.reappay.pg.payments.paymentsback;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.reappay.pg.payments.paymentsback.proto.PaymentRequest;
import net.reappay.pg.payments.paymentsback.proto.PaymentServiceGrpc;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
class PayApprovalTests {

    @Test
    void contextLoads() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8182)
                .usePlaintext()
                .build();

        PaymentServiceGrpc.PaymentServiceBlockingStub pays = PaymentServiceGrpc.newBlockingStub(channel);

        pays.paymentCall(PaymentRequest.newBuilder()
                .setTranSeq("2021071517401600006746")
                .setApprType("eeee")
                .setCertiType("a")
                .setAuthty("2")
                .setInterest("0")
                .setPasswd("11")
                .setLastTidNum("801230")
                .setCurrencyType("KRW")
                .setStatus("O")
                .setIssCode("11")
                .setAcqCode("12")
                .setCavv("13")
                .setXid("14")
                .setEci("15")
                .setKbAppOtp("16")
                .setKVPNOINT("17")
                .setKVPQUOTA("18")
                .setKVPPGID("19")
                .setKVPCARDCODE("20")
                .setKVPSESSIONKEY("21")
                .setKVPENCDATA("22")
                .setAuthNo("23")
                .setMessage1("24")
                .setMessage2("25")
                .setCardNo("11112222333344444")
                .setCardCode("11")
                .setExpDate("27")
                .setInstallment("12")
                .setAmount("1000000000")
                .setMerchantNo("30")
                .setAuthSendType("31")
                .setApprovalSendType("1")
                .setPoint1("")
                .setPoint2("")
                .setPoint3("")
                .setPoint4("")
                .setVanTransactionNo("37")
                .setFiller("")
                .setAuthType("39")
                .setMPIPositionType("40")
                .setMPIReUseType("1")
                .setEncData("EncData encriptString~~")
                .setResultCode("000")
                .setResultMsg("정상결제완료")
                .setEtcData1("11")
                .setEtcData2("22")
                .setEtcData3("33")
                .setEtcData4("44")
                .setEtcData5("55")
                .build()
        );

        channel.shutdown();

    }

}
