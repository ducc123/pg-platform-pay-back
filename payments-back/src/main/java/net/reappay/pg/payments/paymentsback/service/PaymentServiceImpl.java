package net.reappay.pg.payments.paymentsback.service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import net.reappay.pg.payments.paymentsback.dto.ApprDto;
import net.reappay.pg.payments.paymentsback.dto.OrderDto;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import net.reappay.pg.payments.paymentsback.dto.UserDto;
import net.reappay.pg.payments.paymentsback.entity.PayTerminalInfo;
import net.reappay.pg.payments.paymentsback.entity.PayTidInfo;
import net.reappay.pg.payments.paymentsback.enums.PayMethodEnum;
import net.reappay.pg.payments.paymentsback.exception.PgRequestException;
import net.reappay.pg.payments.paymentsback.proto.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

@Slf4j
@GrpcService
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    @Autowired
    private final MybatisServiceImpl mybatisServiceImpl;
    
    //주문처리 서비스
    @Override
    public void orderCall(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {

        log.debug("###인증결제 주문처리시작");
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto orderDto       = modelMapper.map(request, OrderDto.class);

        //transeq 생성해서 저장
        String tranSeq = mybatisServiceImpl.getTranSeq();
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

        UserDto userDto = mybatisServiceImpl.findUserInfo(orderDto.getCustId());
        orderDto.setUserCate(userDto.getUserCate());
        orderDto.setCurrencyType("KRW");

        //PgMerchNo PgTid설정
        String PgMerchNo        = "";
        String PgTid            = "";
        String ResultCode       = "0000";
        String ResultMessage    = "정상적으로 주문이 완료되었습니다.";

        try {
            PgMerchNo               = userDto.getPgMerchNo();
            PgTid                   = userDto.getPgTid();

            orderDto.setPgMerchNo(PgMerchNo);
            orderDto.setPgTid(PgTid);
            log.info("PgMerchNo============="+  PgMerchNo);
            log.info("PgTid============="+      PgTid);
            PayTidInfo payTidInfo = mybatisServiceImpl.findPgTidInfo(orderDto);
            orderDto.setPayMtdSeq(payTidInfo.getPayMtdSeq());

            //터미널 정보가져오기
            PayTerminalInfo payTerminalInfo = mybatisServiceImpl.findTerminal(orderDto);
            orderDto.setStoreId(payTerminalInfo.getTerminalNo());

        } catch (NullPointerException e) {
            ResultCode = "9999";
            ResultMessage = "error : "+e.getMessage();
            throw new PgRequestException("error : "+e.getMessage(),9999);
        }
        orderDto.setPgMerchNo(PgMerchNo);
        orderDto.setPgTid(PgTid);

        //클라이언트 아이피정보 설정
        String cip              = null;
        try {
            cip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ResultCode = "9999";
            ResultMessage = "error : "+e.getMessage();
            throw new PgRequestException("error : "+e.getMessage(),9999);
        }
        orderDto.setCustIp(cip);

        //한도체크 ing....
        payLimitAmtValidation(orderDto);

        //가맹점 주문정보 tb_approval 저장
        mybatisServiceImpl.addOrder(orderDto);

        //기본 response처리
        OrderResponse response  = OrderResponse.newBuilder()
                .setResultCode(ResultCode)
                .setResultMessage(ResultMessage)
                .setTranSeq(tranSeq)
                .setPgTid(PgTid)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //승인처리 서비스
    @Override
    public void paymentCall(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {

        log.debug("###인증결제 승인처리시작");
        String ResultCode       = "0000";
        String ResultMessage    = "정상적으로 결제가 완료되었습니다.";

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
        payDto.setTableYd("TB_TRAN_CARDPG_"+cardpgdatestr);

        payDto.setNotiTryCnt(0);
        payDto.setNotiStatus("00");
        payDto.setTranChkFlag(0);
        payDto.setAcqrChkFlag(0);
        payDto.setBillkeyYn("N");

        ApprDto apprDto = mybatisServiceImpl.findApprovalTranSeq(payDto.getTranSeq());

        //카드명 받아오기
        payDto.setCardNo(payDto.getCardNo().substring(0,6));
        Map<String, String> cardIss = mybatisServiceImpl.findCardIssCdByCardNoString(payDto);

        payDto.setIssCode(cardIss.get("ISS_CD"));
        payDto.setCardIssuNm(cardIss.get("CODE_NM"));
        payDto.setOrderDate(apprDto.getOrderDate());
        payDto.setOrderTime(apprDto.getOrderTime());
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
        payDto.setTranStatus("00");
        payDto.setTranType(apprDto.getTranType());
        payDto.setCustId(apprDto.getCustId());
        payDto.setCurrencyType("KRW");
        payDto.setMerchantNo("ksnet");
        payDto.setPayMethod(PayMethodEnum.CARD);
        payDto.setStoreId(apprDto.getStoreId());

        //금액계산
        setAmt(payDto);

        //승인클라이언트 아이피정보 설정
        String cip              = null;
        try {
            cip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ResultCode = "9999";
            ResultMessage = "error : "+e.getMessage();
            throw new PgRequestException("error : "+e.getMessage(),9999);
        }
        payDto.setIpAddr(cip);

        String UserSeq  = "";
        UserDto userDto = mybatisServiceImpl.findUserInfo(payDto.getCustId());
        UserSeq         = userDto.getUserSeq();
        payDto.setUserSeq(UserSeq);
        payDto.setPayChnCate("002");

        //승인완료 후 승인정보받아서 tb_approval 업데이트
        mybatisServiceImpl.addApproval(payDto);

        String ResultSuccessCode    = "0000";
        if(ResultCode.equals(ResultSuccessCode)) {
            log.debug("인증결제성공({}) 결제결과 = code: {} msg: {}",payDto.getTranSeq(),ResultCode,ResultMessage);

            payDto.setApprovalCode(ResultCode);
            payDto.setApprovalMsg(ResultMessage);

            //승인정보 tb_transaction 저장
            mybatisServiceImpl.addTransaction(payDto);
            //승인정보 tb_transaction_card 저장
            mybatisServiceImpl.addTransactionCard(payDto);

        } else {
            log.debug("인증결제실패({}) 결제결과 = code: {} msg: {}",payDto.getTranSeq(),ResultCode,ResultMessage);
            //승인정보 tb_transaction_error 저장(승인실패시)
            mybatisServiceImpl.addTransactionError(payDto);
        }

        //tb_tran_cardpg_YYmm 저장
        mybatisServiceImpl.addTransactionCardPg(payDto);
        
        //기본 response처리
        PaymentResponse response = PaymentResponse.newBuilder()
                .setResultCode(ResultCode)
                .setResultMessage(ResultMessage)
                .setApprDt(datestr)
                .setApprTm(timestr)
                .setApprNo(payDto.getAuthNo())
                .setIssCd(payDto.getCardCode())
                .setIssNm(payDto.getCardIssuNm())
                .setPayAmt(payDto.getTotAmt().intValue())
                .setPgTidPayAmt("0")
                .setPgTidCommision("0")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void setAmt(PayDto payDto){

        BigDecimal totBd    = payDto.getAmount();
        BigDecimal vatBd    = totBd.divide(new BigDecimal("11"), 0, RoundingMode.DOWN);
        BigDecimal splAmt   = totBd.subtract(vatBd);
        BigDecimal svcAmt   = totBd.subtract(splAmt.add(vatBd));

        payDto.setTotAmt(totBd);                // 결제금액 입력
        payDto.setVatAmt(vatBd);                // 부가세 금액 입력
        payDto.setSvcAmt(svcAmt);                // 과세 금액
        payDto.setSplAmt(splAmt);               // 공급가액

    }

    public PaymentServiceImpl(MybatisServiceImpl mybatisServiceImpl) {
        this.mybatisServiceImpl = mybatisServiceImpl;
    }

    public void payLimitAmtValidation(OrderDto orderDto) {
        //String limitAmt = mybatisServiceImpl.limitAmtCheck(orderDto);
        String limitAmt = "0";
        log.debug("=======> limitAmt : {}", limitAmt);

        // 한도금액
        if (limitAmt=="0") {
            PayTidInfo payTidInfo = mybatisServiceImpl.findPgTidInfo(orderDto);

            // 건별 결제 한도
            int perLimitAmt = Integer.parseInt(payTidInfo.getAppreqChkAmt());

            log.debug("=======> perLimitAmt : {}", perLimitAmt);

            // 건별 한도가 0이 아니면 비교 (0인경우 한도x)
            if (perLimitAmt > 0) {
                int payAmt = orderDto.getTotAmt();

                log.debug("=======> payAmt : {}", payAmt);

                // 결제 금액이 건 별 결제 한도보다 많은 경우
                if (payAmt > perLimitAmt) {
                    log.error("### "+3003 + " : 건별 결제한도를 초과하였습니다.");
                    throw new PgRequestException("건별 결제한도를 초과하였습니다.", 3003);
                }
            }
        } else {

            if (limitAmt=="1") {
                log.error("### "+3004 + " : 월 결제한도를 초과하였습니다");
                throw new PgRequestException("월 결제한도를 초과하였습니다.", 3004);
            } else if (limitAmt=="2") {
                log.error("### "+3005 + " : 년 결제한도를 초과하였습니다.");
                throw new PgRequestException("년 결제한도를 초과하였습니다.", 3005);
            } else {
                log.error("### "+3006 + " : 결제한도를 초과하였습니다.");
                throw new PgRequestException("결제한도를 초과하였습니다.", 3006);
            }
        }

    }
}
