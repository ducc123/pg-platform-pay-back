package net.reappay.pg.payments.paymentsback;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.reappay.pg.payments.paymentsback.proto.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.Charset;
import java.util.Random;

@SpringBootTest
class PayOrderTests {

    @Test
    void contextLoads() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8182)
                .usePlaintext()
                .build();
        PaymentServiceGrpc.PaymentServiceBlockingStub orders = PaymentServiceGrpc.newBlockingStub(channel);

        Random rnd = new Random();
        String randomStr = String.valueOf(rnd.nextInt(99000000));

        OrderResponse paymentProto = orders.orderCall(OrderRequest.newBuilder()
                .setCustId("coehdus1")
                .setCustName("주문자")
                .setCustEmail("test@test.com")
                .setCustPhone("01022221111")
                .setGoodsName("상품명%^%^&&!~~")
                .setGoodsCode("123456")
                .setTotAmt(100)
                .setInstallment("0")
                .setOrderSeq(randomStr)
                .setReturnUrl("http://naver.com")
                .setSuccessUrl("http://naver.com")
                .setFailtureUrl("http://naver.com")
                .build()
        );

        System.out.println("ResultCode = "+paymentProto.getResultCode());
        System.out.println("ResultMessage = "+paymentProto.getResultMessage());
        System.out.println("PgTid = "+paymentProto.getPgTid());
        System.out.println("TranSeq = "+paymentProto.getTranSeq());

        channel.shutdown();
    }

}
