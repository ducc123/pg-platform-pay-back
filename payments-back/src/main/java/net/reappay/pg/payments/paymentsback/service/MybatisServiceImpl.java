package net.reappay.pg.payments.paymentsback.service;

import net.reappay.pg.payments.paymentsback.dao.MybatisDao;
import net.reappay.pg.payments.paymentsback.dto.ApprDto;
import net.reappay.pg.payments.paymentsback.dto.OrderDto;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import net.reappay.pg.payments.paymentsback.dto.UserDto;
import net.reappay.pg.payments.paymentsback.entity.PayTerminalInfo;
import net.reappay.pg.payments.paymentsback.entity.PayTidInfo;

import lombok.RequiredArgsConstructor;
import net.reappay.pg.payments.paymentsback.entity.PgTidCommission;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.Map;

@RequiredArgsConstructor
@Service("MybatisService")
public class MybatisServiceImpl {

    private final MybatisDao mybatisDao;

    public String getTranSeq() {
        String tranSeq = mybatisDao.getTranSeq();
        return tranSeq;
    }

    public String limitAmtCheck(PayDto payDto) {
        String limitAmt = mybatisDao.limitAmtCheck(payDto);
        return limitAmt;
    }

    public UserDto findUserInfo(String CustId){
        UserDto userDto = mybatisDao.findUserInfo(CustId);
        return userDto;
    }

    public ApprDto findApprovalTranSeq(String TranSeq){
        ApprDto apprDto= mybatisDao.findApprovalTranSeq(TranSeq);
        return apprDto;
    }

    public int countApprovalTranSeq(String TranSeq){
        int TranSeqCount= mybatisDao.countApprovalTranSeq(TranSeq);
        return TranSeqCount;
    }

    public PayTidInfo findPgTidInfo(OrderDto orderDto){
        PayTidInfo payTidInfo= mybatisDao.findPgTidInfo(orderDto);
        return payTidInfo;
    }

    public PayTidInfo findPgTidInfo2(PayDto payDto){
        PayTidInfo payTidInfo= mybatisDao.findPgTidInfo2(payDto);
        return payTidInfo;
    }

    public PayTerminalInfo findTerminal(OrderDto orderDto){
        PayTerminalInfo payTerminalInfo= mybatisDao.findTerminal(orderDto);
        return payTerminalInfo;
    }

    public Map<String, String> findCardIssCdByCardNoString(PayDto payDto) {
        Map<String, String> cardIss = mybatisDao.findCardIssCdByCardNoString(payDto);
        return cardIss;
    }

    public int findInstallmentMonthByNo(PayDto payDto) {
        int installmentMax= mybatisDao.findInstallmentMonthByNo(payDto);
        return installmentMax;
    }

    public PgTidCommission findCommissionByPgTid(PayDto payDto) {
        PgTidCommission pgTidCommission = mybatisDao.findCommissionByPgTid(payDto);
        return pgTidCommission;
    }

    public void addOrder(OrderDto orderDto) { mybatisDao.addOrder(orderDto); }
    public void addApproval(PayDto payDto) {
        mybatisDao.addApproval(payDto);
    }
    public void addTransaction(PayDto payDto) { mybatisDao.addTransaction(payDto); }
    public void addTransactionCard(PayDto payDto) {
        mybatisDao.addTransactionCard(payDto);
    }
    public void addTransactionError(PayDto payDto) { mybatisDao.addTransactionError(payDto);     }
    public void addTransactionCardPg(PayDto payDto) { mybatisDao.addTransactionCardPg(payDto);    }

}
