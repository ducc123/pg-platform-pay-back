package net.reappay.pg.payments.paymentsback;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.reappay.pg.payments.paymentsback.proto.PaymentRequest;
import net.reappay.pg.payments.paymentsback.proto.PaymentResponse;
import net.reappay.pg.payments.paymentsback.proto.PaymentServiceGrpc;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PayApprovalTests {

    @Test
    void contextLoads() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8182)
                .usePlaintext()
                .build();

        PaymentServiceGrpc.PaymentServiceBlockingStub pays = PaymentServiceGrpc.newBlockingStub(channel);

        PaymentResponse paymentProto = pays.paymentCall(PaymentRequest.newBuilder()
                .setTranSeq("2021072114302300006862")
                .setPgSeq("123456789012")
                .setApprType("Order")
                .setCertiType("a")
                .setAuthty("2")
                .setInterest("0")
                .setPasswd("11")
                .setLastTidNum("19801230")
                .setCurrencyType("KRW")
                .setStatus("O")
                .setIssCode("11")
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
                .setAuthNo("12345678")
                .setMessage1("Message1")
                .setMessage2("Message2")
                .setCardNo("941019******")
                .setCardCode("026")
                .setExpDate("2112")
                .setInstallment("5")
                .setMerchantNo("30")
                .setAuthSendType("31")
                .setApprovalSendType("1")
                .setPoint1("")
                .setPoint2("")
                .setPoint3("")
                .setPoint4("")
                .setVanTransactionNo("37")
                .setAuthType("39")
                .setMPIPositionType("40")
                .setMPIReUseType("1")
                .setEncData("EncData encriptString~~")
                .build()
        );

        System.out.println("ResultCode = "+     paymentProto.getResultCode());                        //결과코드
        System.out.println("ResultMessage = "+  paymentProto.getResultMessage());                     //결과메시지
        System.out.println("ApprDt = "+         paymentProto.getApprDt());                            //승인날짜
        System.out.println("ApprTm = "+         paymentProto.getApprTm());                            //승인시간
        System.out.println("ApprNo = "+         paymentProto.getApprNo());                            //승인번호
        System.out.println("IssCd = "+          paymentProto.getIssCd());                             //카드사코드
        System.out.println("IssNm = "+          paymentProto.getIssNm());                             //카드사이름
        System.out.println("PayAmt = "+         paymentProto.getPayAmt());                            //결제금액
        System.out.println("PgTidPayAmt = "+    paymentProto.getPgTidPayAmt());                       //가맹점 지급액
        System.out.println("PgTidCommision = "+ paymentProto.getPgTidCommision());                    //가맹점 수수료

        channel.shutdown();

    }

}
