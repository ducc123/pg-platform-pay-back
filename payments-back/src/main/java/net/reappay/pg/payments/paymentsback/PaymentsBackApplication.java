package net.reappay.pg.payments.paymentsback;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.reappay.pg.payments.paymentsback.proto.SampleRequest;
import net.reappay.pg.payments.paymentsback.proto.SampleResponse;
import net.reappay.pg.payments.paymentsback.proto.SampleServiceGrpc;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentsBackApplication {

    @Test
    void contextLoads() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8182)
                .usePlaintext()
                .build();

        SampleServiceGrpc.SampleServiceBlockingStub stub = SampleServiceGrpc.newBlockingStub(channel);

        SampleResponse sampleResponse = stub.sampleCall(SampleRequest.newBuilder()
                .setUserId("Jonathan")
                .setMessage("Nice")
                .build()
        );

        System.out.println(sampleResponse.getMessage());
        System.out.println("ddddddddddddd");
        channel.shutdown();
    }
}
