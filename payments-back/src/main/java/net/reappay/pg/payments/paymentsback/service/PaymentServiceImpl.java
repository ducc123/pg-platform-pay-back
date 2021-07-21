package net.reappay.pg.payments.paymentsback.service;

import net.devh.boot.grpc.server.service.GrpcService;
import net.reappay.pg.payments.paymentsback.dto.ApprDto;
import net.reappay.pg.payments.paymentsback.dto.OrderDto;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import net.reappay.pg.payments.paymentsback.dto.UserDto;
import net.reappay.pg.payments.paymentsback.entity.PayTerminalInfo;
import net.reappay.pg.payments.paymentsback.entity.PayTidInfo;
import net.reappay.pg.payments.paymentsback.enums.*;
import net.reappay.pg.payments.paymentsback.proto.*;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

@Slf4j
@GrpcService
@Service("PaymentService")
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    @Resource(name="MybatisService")
    private final MybatisServiceImpl mybatisServiceImpl;

    @Resource(name="ValidationService")
    private final ValidationServiceImpl validationService;

    //주문처리 서비스@20210719
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public void orderCall(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {

        //기본 시작날짜시간 설정
        Calendar cal            = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2   = new SimpleDateFormat("HH:mm:ss");
        String datestr          = sdf.format(cal.getTime());
        String timestr          = sdf2.format(cal.getTime());
        
        log.debug("###인증결제 주문처리시작 ({} {})",datestr,timestr);
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDto orderDto       = modelMapper.map(request, OrderDto.class);

        //PgMerchNo PgTid설정
        String PgMerchNo;
        String PgTid=null;
        String ResultCode = "0000";
        String ResultMessage = "정상적으로 주문이 완료되었습니다.";

        if (orderDto.getCustId().isEmpty()){
            ResultCode       = "9999";
            ResultMessage    = "회원아이디를 확인해주세요.";

            //결과코드 , 메시지 설정
            orderDto.setResultCode(ResultCode);
            orderDto.setResultMessage(ResultMessage);
            log.debug("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        } else if (orderDto.getGoodsName().isEmpty()){
            ResultCode       = "9999";
            ResultMessage    = "상품명을 확인해주세요.";

            //결과코드 , 메시지 설정
            orderDto.setResultCode(ResultCode);
            orderDto.setResultMessage(ResultMessage);
            log.debug("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        } else if (orderDto.getTotAmt().toString().isEmpty() || orderDto.getTotAmt()<100){
            ResultCode       = "9999";
            ResultMessage    = "결제금액을 확인해주세요.";

            //결과코드 , 메시지 설정
            orderDto.setResultCode(ResultCode);
            orderDto.setResultMessage(ResultMessage);
            log.debug("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        } else if (orderDto.getOrderSeq().length()==0){
            ResultCode       = "9999";
            ResultMessage    = "주문번호를 확인해주세요.";

            //결과코드 , 메시지 설정
            orderDto.setResultCode(ResultCode);
            orderDto.setResultMessage(ResultMessage);
            log.debug("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        } else if (orderDto.getOrderSeq().isEmpty()){
            ResultCode       = "9999";
            ResultMessage    = "주문번호를 확인해주세요.";

            //결과코드 , 메시지 설정
            orderDto.setResultCode(ResultCode);
            orderDto.setResultMessage(ResultMessage);
            log.debug("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        } else {

            //transeq 생성해서 저장
            String tranSeq = mybatisServiceImpl.getTranSeq();
            orderDto.setTranSeq(tranSeq);

            //주문일 주문시간설정
            orderDto.setOrderDate(datestr);
            orderDto.setOrderTime(timestr);
            orderDto.setTranType("10");
            orderDto.setCurrencyType("KRW");
            orderDto.setPayMethod(PayMethodEnum.CARD);

            if (orderDto.getProductType()==null || orderDto.getProductType()=="") {
                orderDto.setProductType("0");
            }

            try {
                //회원정보 가져오기
                UserDto userDto = mybatisServiceImpl.findUserInfo(orderDto.getCustId());
                orderDto.setUserCate(userDto.getUserCate());

                PgMerchNo = userDto.getPgMerchNo();
                PgTid = userDto.getPgTid();

                orderDto.setPgMerchNo(PgMerchNo);
                orderDto.setPgTid(PgTid);
                log.info("PgMerchNo=============" + PgMerchNo);
                log.info("PgTid=============" + PgTid);
                PayTidInfo payTidInfo = mybatisServiceImpl.findPgTidInfo(orderDto);
                orderDto.setPayMtdSeq(payTidInfo.getPayMtdSeq());

                //터미널 정보가져오기
                PayTerminalInfo payTerminalInfo = mybatisServiceImpl.findTerminal(orderDto);
                orderDto.setStoreId(payTerminalInfo.getTerminalNo());

                orderDto.setPgMerchNo(PgMerchNo);
                orderDto.setPgTid(PgTid);

            } catch (NullPointerException e) {
                ResultCode = "9999";
                ResultMessage = "정상적인 회원정보가 아닙니다";
                log.error("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
            }

            //클라이언트 아이피정보 설정
            String cip;
            try {
                cip = Inet4Address.getLocalHost().getHostAddress();
                orderDto.setCustIp(cip);
            } catch (UnknownHostException e) {
                ResultCode = "9999";
                ResultMessage = "error : " + e.getMessage();
                log.debug("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
            }

            //가맹점 주문정보 tb_approval 저장
            mybatisServiceImpl.addOrder(orderDto);

            //결과코드 , 메시지 설정
            orderDto.setResultCode(ResultCode);
            orderDto.setResultMessage(ResultMessage);

            //기본 response처리
            OrderResponse response = OrderResponse.newBuilder()
                    .setResultCode(orderDto.getResultCode())
                    .setResultMessage(orderDto.getResultMessage())
                    .setTranSeq(tranSeq)
                    .setPgTid(PgTid)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            Calendar cal2 = Calendar.getInstance();
            SimpleDateFormat sdf11 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf12 = new SimpleDateFormat("HH:mm:ss");
            String datestr1 = sdf11.format(cal2.getTime());
            String timestr1 = sdf12.format(cal2.getTime());
            log.debug("###인증결제 주문처리끝 ({} {})", datestr1, timestr1);
        }

    }

    //승인처리 서비스
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public void paymentCall(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {

        //기본 시작날짜시간 설정
        Calendar cal            = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2   = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdf3   = new SimpleDateFormat("yyMM");
        String datestr          = sdf.format(cal.getTime());
        String timestr          = sdf2.format(cal.getTime());
        String cardpgdatestr    = sdf3.format(cal.getTime());

        log.debug("###인증결제 승인처리시작 ({} {} {})",datestr,timestr,cardpgdatestr);
        String ResultCode       = "0000";
        String ResultMessage    = "정상적으로 결제가 완료되었습니다.";
        String UserSeq;

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PayDto payDto           = modelMapper.map(request, PayDto.class);

        //중복승인인지 확인함 refresh 되는 상황을 처리하기 위해
        int tranSeqCount = mybatisServiceImpl.countApprovalTranSeq(payDto.getTranSeq());
        if (tranSeqCount>0) {
            ResultCode = "9000";
            ResultMessage = "이미 결제된 거래입니다.";
            log.debug("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        }

        payDto.setTradeDate(datestr.replace("-","").replace(" ",""));
        payDto.setTradeTime(timestr.replace(":","").replace(" ",""));
        payDto.setTableYd(cardpgdatestr);

        /**
         * 기본 값이 필요한 필드설정
         */
        payDto.setNotiTryCnt(0);
        payDto.setNotiStatus("00");
        payDto.setTranChkFlag(0);
        payDto.setAcqrChkFlag(0);
        payDto.setBillkeyYn("N");
        payDto.setAcqrStatus("00");
        payDto.setPayChnCate("002");
        payDto.setDpstStatus("N");
        payDto.setStlmStatus("N");
        payDto.setResultStatus("00");
        payDto.setCurrencyType("KRW");
        payDto.setMerchantNo("ksnet");
        ApprDto apprDto = mybatisServiceImpl.findApprovalTranSeq(payDto.getTranSeq());
        payDto.setTranStatus(TranStatusEnum.APPROVAL.code());
        payDto.setTranCate(TranTypeEnum.APPROVAL_REQUEST.code());
        payDto.setTranStep(TranStepEnum.TRAN_COMPLETE.code());
        payDto.setPayMethod(PayMethodEnum.CARD);

        /**
         * 주문정보값으로 payDto 설정
         */
        payDto.setCardNo(payDto.getCardNo().substring(0,6));
        Map<String, String> cardIss = mybatisServiceImpl.findCardIssCdByCardNoString(payDto);
        payDto.setIssCode(cardIss.get("ISS_CD"));
        payDto.setCardIssuNm(cardIss.get("CODE_NM"));
        payDto.setOrderDate(apprDto.getOrderDate().replace("-","").replace(" ",""));
        payDto.setOrderTime(apprDto.getOrderTime().replace(":",""));
        payDto.setGoodsCode(apprDto.getGoodsCode());
        payDto.setGoodsName(apprDto.getGoodsName());
        payDto.setCustName(apprDto.getCustName());
        payDto.setCustPhone(apprDto.getCustPhone());
        payDto.setCustIp(apprDto.getCustIp());
        payDto.setOrderSeq(apprDto.getOrderSeq());
        payDto.setPgMerchNo(apprDto.getPgMerchNo());
        payDto.setPgTid(apprDto.getPgTid());
        payDto.setStatus(apprDto.getStatus());
        payDto.setTranType(apprDto.getTranType());
        payDto.setCustId(apprDto.getCustId());
        payDto.setStoreId(apprDto.getStoreId());
        payDto.setTotAmt(apprDto.getTotAmt());

        /**
        * 클라이언트 아이피정보 설정
        **/
        String cip              = null;
        try {
            cip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ResultCode = "9000";
            ResultMessage = "error : "+e.getMessage();
            log.error("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        }
        payDto.setIpAddr(cip);

        /**
         * 회원정보 validation
         **/
        try {
            UserDto userDto = mybatisServiceImpl.findUserInfo(apprDto.getCustId());
            payDto.setUserCate(userDto.getUserCate());
            payDto.setCustName(userDto.getUserNm());

            UserSeq = userDto.getUserSeq();
            payDto.setUserSeq(UserSeq);
        } catch(NullPointerException e){
            ResultCode = "9000";
            ResultMessage = "회원정보를 확인해주세요";
            log.error("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        }

        /**
         * 결제 전 validation
         **/
        String ResultMessage1;
        String ResultMessage2;
        String ResultMessage3;
        try {
            //금액계산
            ResultMessage1 = validationService.setAmt(payDto);
            //한도체크
            ResultMessage2 = validationService.payLimitAmtValidation(payDto);
            //최대 할부개월수체크
            ResultMessage3 = validationService.payInstallmentValidation(payDto);

            if (!ResultMessage1.equals("")){
                ResultCode = "9000";
                ResultMessage = ResultMessage1;
            } else if (!ResultMessage2.equals("")){
                ResultCode = "9000";
                ResultMessage = ResultMessage2;
            } else if (!ResultMessage3.equals("")){
                ResultCode = "9000";
                ResultMessage = ResultMessage3;
            }

        } catch(NullPointerException e){
            ResultCode = "9000";
            ResultMessage = "정상적으로 결제가 완료되지 않았습니다.";
            log.error("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        }

        /**
         * 승인완료 후 승인정보받아서 tb_approval 업데이트
         **/
        mybatisServiceImpl.addApproval(payDto);

        payDto.setApprovalCode(ResultCode);
        payDto.setApprovalMsg(ResultMessage);

        if(ResultCode=="0000") {
            ResultMessage= "정상승인이 완료되었습니다.";
            log.debug("인증결제성공({}) 결제결과 = code: {} msg: {}",payDto.getTranSeq(),ResultCode,ResultMessage);

            //승인정보 tb_transaction 저장
            mybatisServiceImpl.addTransaction(payDto);
            //승인정보 tb_transaction_card 저장
            mybatisServiceImpl.addTransactionCard(payDto);
            //승인정보 tb_tran_cardpg 저장
            mybatisServiceImpl.addTransactionCardPg(payDto);

        } else if(ResultCode=="9000") {
            log.debug("인증결제({}) PG내부오류 = code: {} msg: {}",payDto.getTranSeq(),ResultCode,ResultMessage);
        } else {
            log.debug("인증결제실패({}) 결제결과 = code: {} msg: {}",payDto.getTranSeq(),ResultCode,ResultMessage);
            //승인정보 tb_transaction_error 저장(승인실패시)
            mybatisServiceImpl.addTransactionError(payDto);
        }

        /**
         * 기본 response처리
         **/
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

        Calendar cal2           = Calendar.getInstance();
        SimpleDateFormat sdf11  = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf12  = new SimpleDateFormat("HH:mm:ss");
        String datestr1         = sdf11.format(cal2.getTime());
        String timestr1         = sdf12.format(cal2.getTime());
        log.debug("###인증결제 승인처리끝 ({} {})",datestr1,timestr1);
    }

    public PaymentServiceImpl(MybatisServiceImpl mybatisServiceImpl, ValidationServiceImpl validationService) {
        this.mybatisServiceImpl = mybatisServiceImpl;
        this.validationService = validationService;
    }

}
