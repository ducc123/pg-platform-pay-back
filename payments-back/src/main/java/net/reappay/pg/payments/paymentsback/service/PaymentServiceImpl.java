package net.reappay.pg.payments.paymentsback.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import net.reappay.pg.payments.paymentsback.dto.ApprDto;
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
    private final MybatisServiceImpl mybatisServiceImpl;

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

        ApprDto apprDto = mybatisServiceImpl.findApprovalTranSeq(payDto.getTranSeq());

        payDto.setOrderDate(apprDto.getOrderDate());
        payDto.setOrderTime(apprDto.getOrderTime());
        payDto.setFreeAmt(apprDto.getFreeAmt());
        payDto.setVatAmt(apprDto.getVatAmt());
        payDto.setTotAmt(apprDto.getTotAmt());
        payDto.setCurrencyType(apprDto.getCurrency());
        payDto.setGoodsCode(apprDto.getGoodsCode());
        payDto.setGoodsName(apprDto.getGoodsName());
        payDto.setCustName(apprDto.getCustName());
        payDto.setCustPhone(apprDto.getCustPhone());
        payDto.setCustIp(apprDto.getCustIp());
        payDto.setOrderNumber(apprDto.getOrderNumber());
        payDto.setPgMerchNo(apprDto.getPgMerchNo());
        payDto.setPgTid(apprDto.getPgTid());
        payDto.setStatus(apprDto.getStatus());
        payDto.setInstallment(apprDto.getInstallment());

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
        UserSeq                 = mybatisServiceImpl.findUserSeqUserId(payDto.getCustId());
        payDto.setUserSeq(UserSeq);
        payDto.setPayChnCate("10");

        //승인완료 후 승인정보받아서 tb_approval 업데이트
        mybatisServiceImpl.addApproval(payDto);

        String ResultCode           = payDto.getResultCode();
        String ResultSuccessCode    = "000";

        if(ResultCode.equals(ResultSuccessCode)) {
            log.debug("인증결제성공({}) 결제결과 = code: {} msg: {}",payDto.getTranSeq(),payDto.getResultCode(),payDto.getResultMsg());

            payDto.setApprovalCode(ResultCode);
            payDto.setApprovalMsg(payDto.getResultMsg());

            //승인정보 tb_transaction 저장
            mybatisServiceImpl.addTransaction(payDto);
            //승인정보 tb_transaction_card 저장
            mybatisServiceImpl.addTransactionCard(payDto);
        } else {
            log.debug("인증결제실패({}) 결제결과 = code: {} msg: {}",payDto.getTranSeq(),payDto.getResultCode(),payDto.getResultMsg());
            //승인정보 tb_transaction_error 저장(승인실패시)
            mybatisServiceImpl.addTransactionError(payDto);
        }

        //tb_tran_cardpg_yyMM 저장
        mybatisServiceImpl.addTransactionCardPg(payDto);
        
        //기본 response처리
        PaymentResponse response = PaymentResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public PaymentServiceImpl(MybatisServiceImpl mybatisServiceImpl) {
        this.mybatisServiceImpl = mybatisServiceImpl;
    }

    public PayDto validAmt(PayDto payDto){

         return payDto;
    }

    /*public void payLimitAmtValidation(PayDto payDto) {
        String limitAmt = functionService.limitAmtCheck(payDto);

        log.debug("=======> limitAmt : {}", limitAmt);

        // 한도금액
        if (limitAmt=="0") {
            PayTidInfo payTidInfo = payDto.getPayTidInfo();

            // 건별 결제 한도
            int perLimitAmt = ValueUtils.getInt(payTidInfo.getAppreqChkAmt());

            log.debug("=======> perLimitAmt : {}", perLimitAmt);

            // 건별 한도가 0이 아니면 비교 (0인경우 한도x)
            if (perLimitAmt > 0) {
                int payAmt = ValueUtils.getInt(payDto.getTotAmt());

                log.debug("=======> payAmt : {}", payAmt);

                // 결제 금액이 건 별 결제 한도보다 많은 경우
                if (payAmt > perLimitAmt) {
                    log.error("### "+3003 + " : 건별 결제한도를 초과하였습니다.");
                    throw new InvalidPayAmtException("건별 결제한도를 초과하였습니다.", 3003);
                }
            }
        } else {

            if (limitAmt=="1") {
                log.error("### "+3004 + " : 월 결제한도를 초과하였습니다");
                throw new InvalidPayAmtException("월 결제한도를 초과하였습니다.", 3004);
            } else if (limitAmt=="2") {
                log.error("### "+3005 + " : 년 결제한도를 초과하였습니다.");
                throw new InvalidPayAmtException("년 결제한도를 초과하였습니다.", 3005);
            } else {
                log.error("### "+3006 + " : 결제한도를 초과하였습니다.");
                throw new InvalidPayAmtException("결제한도를 초과하였습니다.", 3006);
            }
        }

    }*/
}
