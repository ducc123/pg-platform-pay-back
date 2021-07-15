package net.reappay.pg.payments.paymentsback.service;

import lombok.RequiredArgsConstructor;
import net.reappay.pg.payments.paymentsback.dao.MybatisDao;
import net.reappay.pg.payments.paymentsback.dto.OrderDto;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MybatisServiceImpl implements MybatisService{

    private final MybatisDao mybatisDao;

    public String getTranSeq() {
        String tranSeq = mybatisDao.getTranSeq();
        return tranSeq;
    }


    public String findPgMerchNoUserId(String CustId){
        String pgMerchNo = mybatisDao.findPgMerchNoUserId(CustId);
        return pgMerchNo;
    }

    public String findPgTidUserId(String CustId){
        String PgTid = mybatisDao.findPgTidUserId(CustId);
        return PgTid;
    }

    public void addOrder(OrderDto orderDto) {
        mybatisDao.addOrder(orderDto);
    }
    public void addApproval(PayDto payDto) {
        mybatisDao.addApproval(payDto);
    }
    public void addTransaction(PayDto payDto) {
        mybatisDao.addApproval(payDto);
    }
    public void addTransactionCard(PayDto payDto) {
        mybatisDao.addApproval(payDto);
    }
    public void addTransactionError(PayDto payDto) {
        mybatisDao.addApproval(payDto);
    }
    public void addTransactionCardPg(PayDto payDto) { mybatisDao.addApproval(payDto); }


}
