package net.reappay.pg.payments.paymentsback.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import net.reappay.pg.payments.paymentsback.dto.OrderDto;
import net.reappay.pg.payments.paymentsback.proto.*;
import org.apache.ibatis.annotations.Mapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

@Slf4j
@GrpcService
@Transactional
@Mapper
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    @Autowired
    private final MybatisService mybatisService;

    @Override
    public void orderCall(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto orderDto       = modelMapper.map(request, OrderDto.class);
        
        //transeq 생성해서 저장
        String tranSeq = mybatisService.getTranSeq();
        orderDto.setTranSeq(tranSeq);
        
        //주문일 주문시간설정
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2   = new SimpleDateFormat("HH:mm:ss");
        String datestr = sdf.format(cal.getTime());
        String timestr = sdf2.format(cal.getTime());
        orderDto.setOrderDate(datestr);
        orderDto.setOrderTime(timestr);
        orderDto.setTranType("10");

        //PgMerchNo PgTid설정
        String PgMerchNo        = "";
        String PgTid            = "";
        try {
            PgMerchNo           = mybatisService.findPgMerchNoUserId(request.getCustId());
            PgTid               = mybatisService.findPgTidUserId(request.getCustId());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        orderDto.setPgMerchNo(PgMerchNo);
        orderDto.setPgTid(PgTid);
        
        //클라이언트 아이피정보 설정
        String cip              = null;
        try {
            cip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        orderDto.setCustIp(cip);

        //가맹점 주문정보 tb_approval 저장
        mybatisService.addOrder(orderDto);

        //기본 response처리
        OrderResponse response  = OrderResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public OrderServiceImpl(MybatisService mybatisService) {
        this.mybatisService = mybatisService;
    }


}
