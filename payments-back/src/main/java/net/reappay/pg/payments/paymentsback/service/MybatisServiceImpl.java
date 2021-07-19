package net.reappay.pg.payments.paymentsback.service;

import lombok.RequiredArgsConstructor;
import net.reappay.pg.payments.paymentsback.dao.MybatisDao;
import net.reappay.pg.payments.paymentsback.dto.ApprDto;
import net.reappay.pg.payments.paymentsback.dto.OrderDto;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import net.reappay.pg.payments.paymentsback.dto.UserDto;
import net.reappay.pg.payments.paymentsback.entity.PayTerminalInfo;
import net.reappay.pg.payments.paymentsback.entity.PayTidInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MybatisServiceImpl {

    private final MybatisDao mybatisDao;

    public String getTranSeq() {
        String tranSeq = mybatisDao.getTranSeq();
        return tranSeq;
    }

    public String limitAmtCheck(OrderDto orderDto) {
        String limitAmt = mybatisDao.limitAmtCheck(orderDto);
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

    public PayTidInfo findPgTidInfo(OrderDto orderDto){
        PayTidInfo payTidInfo= mybatisDao.findPgTidInfo(orderDto);
        return payTidInfo;
    }

    public PayTerminalInfo findTerminal(OrderDto orderDto){
        PayTerminalInfo payTerminalInfo= mybatisDao.findTerminal(orderDto);
        return payTerminalInfo;
    }

    public void addOrder(OrderDto orderDto) {
        mybatisDao.addOrder(orderDto);
    }
    public void addApproval(PayDto payDto) {
        mybatisDao.addApproval(payDto);
    }
    public void addTransaction(PayDto payDto) { mybatisDao.addTransaction(payDto); }
    public void addTransactionCard(PayDto payDto) {
        //mybatisDao.addApproval(payDto);
    }
    public void addTransactionError(PayDto payDto) {
        //mybatisDao.addApproval(payDto);
    }
    public void addTransactionCardPg(PayDto payDto) {
        //mybatisDao.addApproval(payDto);
    }


}
