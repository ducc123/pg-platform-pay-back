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

        OrderServiceGrpc.OrderServiceBlockingStub orders = OrderServiceGrpc.newBlockingStub(channel);

        Random rnd = new Random();
        String randomStr = String.valueOf(rnd.nextInt(10000000));

        orders.orderCall(OrderRequest.newBuilder()
                .setStoreId("coehdus1")
                .setCustId("coehdus1")
                .setTranType("10")
                .setCustName("주문자")
                .setCustEmail("test@test.com")
                .setCustPhone("01022221111")
                .setOrderName("주문자명")
                .setGoodsName("상품명%^%^&&!~~")
                .setGoodsCode("123456")
                .setProductType("1")
                .setPayMethod("CARD")
                .setTotAmt(100)
                .setFreeAmt(1)
                .setVatAmt(1)
                .setInstallment("0")
                .setStoreId("299991919")
                .setOrderNumber(randomStr)
                .setReturnUrl("http://naver.com")
                .setSuccessUrl("http://naver.com")
                .setFailtureUrl("http://naver.com")
                .build()
        );

        channel.shutdown();
    }

}
