package net.reappay.pg.payments.paymentsback;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.reappay.pg.payments.paymentsback.proto.NotiResultRequest;
import net.reappay.pg.payments.paymentsback.proto.PaymentServiceGrpc;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class voidTests {

    @Test
    void test () {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8182)
                .usePlaintext()
                .build();
        PaymentServiceGrpc.PaymentServiceBlockingStub orders = PaymentServiceGrpc.newBlockingStub(channel);

        final NotiResultRequest request = NotiResultRequest.newBuilder()
                .setTranSeq("1")
                .setNotiStatus(true)
                .build();

        orders.notiResultCall(request);

        channel.shutdown();
    }
}
