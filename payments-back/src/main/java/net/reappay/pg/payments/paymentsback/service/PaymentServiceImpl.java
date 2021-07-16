package net.reappay.pg.payments.paymentsback.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import net.reappay.pg.payments.paymentsback.proto.PaymentRequest;
import net.reappay.pg.payments.paymentsback.proto.PaymentResponse;
import net.reappay.pg.payments.paymentsback.proto.PaymentServiceGrpc;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
@GrpcService
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    @Autowired
    private final MybatisService mybatisService;

    @Override
    public void paymentCall(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {

        ModelMapper modelMapper  = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PayDto payDto            = modelMapper.map(request, PayDto.class);

        //승인일 승인시간설정
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2   = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdf3   = new SimpleDateFormat("yyMM");
        String datestr = sdf.format(cal.getTime());
        String timestr = sdf2.format(cal.getTime());
        String cardpgdatestr = sdf3.format(cal.getTime());
        payDto.setTradeDate(datestr);
        payDto.setTradeTime(timestr);
        payDto.setTableYd(cardpgdatestr);

        int svcAmt = Integer.parseInt(payDto.getAmount())-payDto.getVatAmt();
        payDto.setSvcAmt(svcAmt);
        payDto.setTranStatus("10");
        //승인클라이언트 아이피정보 설정
        String cip              = null;
        try {
            cip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        payDto.setIpAddr(cip);

        String UserSeq          = "";
        UserSeq                 = mybatisService.findUserSeqUserId(payDto.getCustId());
        payDto.setUserSeq(UserSeq);
        payDto.setPayChnCate("10");

        //승인완료 후 승인정보받아서 tb_approval 업데이트
        mybatisService.addApproval(payDto);

        String ResultCode           = payDto.getResultCode();
        String ResultSuccessCode    = "000";

        if(ResultCode.equals(ResultSuccessCode)) {
            log.debug("인증결제성공({}) 결제결과 = code: {} msg: {}",payDto.getTranSeq(),payDto.getResultCode(),payDto.getResultMsg());
            //승인정보 tb_transaction 저장
            mybatisService.addTransaction(payDto);
            //승인정보 tb_transaction_card 저장
            mybatisService.addTransactionCard(payDto);
        } else {
            log.debug("인증결제실패({}) 결제결과 = code: {} msg: {}",payDto.getTranSeq(),payDto.getResultCode(),payDto.getResultMsg());
            //승인정보 tb_transaction_error 저장(승인실패시)
            mybatisService.addTransactionError(payDto);
        }

        //tb_tran_cardpg_yyMM 저장
        mybatisService.addTransactionCardPg(payDto);
        
        //기본 response처리
        PaymentResponse response = PaymentResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public PaymentServiceImpl(MybatisService mybatisService) {
        this.mybatisService = mybatisService;
    }
}
