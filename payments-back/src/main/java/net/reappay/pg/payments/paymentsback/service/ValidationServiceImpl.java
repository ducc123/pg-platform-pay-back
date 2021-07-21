package net.reappay.pg.payments.paymentsback.service;

import lombok.extern.slf4j.Slf4j;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import net.reappay.pg.payments.paymentsback.entity.PayTidInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service("ValidationService")
public class ValidationServiceImpl {

    @Resource(name="MybatisService")
    private final MybatisServiceImpl mybatisServiceImpl;

    /**
     * [Approval] 결제금액 validation
     *
     * payDto
     */
    @Transactional
    public String setAmt(PayDto payDto){

        String ResultCode   = "";
        String ResultMessage= "";

        BigDecimal totBd    = payDto.getTotAmt();
        BigDecimal vatBd    = totBd.divide(new BigDecimal("11"), 0, RoundingMode.DOWN);
        BigDecimal splAmt   = totBd.subtract(vatBd);
        BigDecimal svcAmt   = totBd.subtract(splAmt.add(vatBd));

        payDto.setLimitTotAmt(payDto.getTotAmt().toString());
        payDto.setVatAmt(vatBd);                // 부가세 금액 입력
        payDto.setSvcAmt(svcAmt);                // 과세 금액
        payDto.setSplAmt(splAmt);               // 공급가액

        if (payDto.getTotAmt().intValue() <= 0 || payDto.getTotAmt().intValue() != (splAmt.intValue() + vatBd.intValue())) {
            ResultCode = "3001";
            ResultMessage = "결제금액을 입력해주세요";
            log.debug("ResultCode : {} , ResultMessage : {}",ResultCode,ResultMessage);
        }

        return ResultMessage;

    }

    /**
     * [Approval] 한도 validation
     *
     * payDto
     */
    @Transactional
    public String payLimitAmtValidation(PayDto payDto) {
        String limitAmt = mybatisServiceImpl.limitAmtCheck(payDto);
        String ResultMessage = "";

        log.debug("### 한도 Validation");
        log.debug("=======> limitAmt : {}", limitAmt);

        // 한도금액
        if (limitAmt.equals("0")) {
            PayTidInfo payTidInfo = mybatisServiceImpl.findPgTidInfo2(payDto);

            // 건별 결제 한도
            int perLimitAmt = Integer.parseInt(payTidInfo.getAppreqChkAmt());

            log.debug("=======> perLimitAmt : {}", perLimitAmt);

            // 건별 한도가 0이 아니면 비교 (0인경우 한도x)
            if (perLimitAmt > 0) {
                int payAmt = payDto.getTotAmt().intValue();

                // 결제 금액이 건 별 결제 한도보다 많은 경우
                if (payAmt > perLimitAmt) {
                    log.error("### "+3003 + " : 건별 결제한도를 초과하였습니다.");
                    ResultMessage = "건별 결제한도를 초과하였습니다";
                }
                return ResultMessage;
            }
        } else {

            if (limitAmt.equals("1")) {
                log.error("### "+3004 + " : 월 결제한도를 초과하였습니다");
                ResultMessage = "월 결제한도를 초과하였습니다";
            } else if (limitAmt.equals("2")) {
                log.error("### "+3005 + " : 년 결제한도를 초과하였습니다.");
                ResultMessage = "년 결제한도를 초과하였습니다";
            } else {
                log.error("### "+3006 + " : 결제한도를 초과하였습니다.");
                ResultMessage = "결제한도를 초과하였습니다.";
            }

        }

        return ResultMessage;
    }

    /**
     * [Approval] 할부 개월 validation
     *
     * payDto
     */
    @Transactional
    public String payInstallmentValidation(PayDto payDto) {
        // 요청 할부 개월
        String reqInstallment = payDto.getInstallment();
        String ResultMessage = "";

        log.debug("### 결제 할부 개월 Validation");

        // 최대 할부 개월
        int maxInstallment = mybatisServiceImpl.findInstallmentMonthByNo(payDto);
        log.debug("최대할부 개월수 : {}개월",maxInstallment);

        // 할부(x)
        if (reqInstallment != null && !reqInstallment.equals("00")) {

            // 할부가 불가능한 경우
            if (maxInstallment == 0) {
                log.error("### "+6002 + " : 할부가 불가능 합니다.");
                ResultMessage = "할부가 불가능 합니다";

            } else {
                int reqInstall = Integer.parseInt(reqInstallment);

                // 요청 할부 개월이 가능한 할부 개월 수 보다 큰 경우
                if (reqInstall > maxInstallment) {
                    StringBuffer sb = new StringBuffer("최대 할부개월를 초과했습니다. 결제요청 할부개월수는 ");
                    sb.append(reqInstall);
                    sb.append("개월 입니다.");
                    log.error("### "+6003 + " : "+sb);

                    ResultMessage = sb.toString();

                }
            }
        }
        return ResultMessage;
    }

    public ValidationServiceImpl(MybatisServiceImpl mybatisServiceImpl) {
        this.mybatisServiceImpl = mybatisServiceImpl;
    }
}
