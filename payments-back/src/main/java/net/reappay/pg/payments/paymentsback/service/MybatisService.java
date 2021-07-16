package net.reappay.pg.payments.paymentsback.service;

import net.reappay.pg.payments.paymentsback.dto.OrderDto;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import org.springframework.dao.DataAccessException;

public interface MybatisService {

    /**
     * 거래 번호 생성
     *
     * @return
     * @throws Exception
     */
    String getTranSeq() throws DataAccessException;

    /**
     * PGMerchNO , PgTid가져오기
     *
     * @return
     * @throws Exception
     */
    String findPgMerchNoUserId(String CustId) throws DataAccessException;
    String findPgTidUserId(String CustId) throws DataAccessException;
    String findUserSeqUserId(String CustId) throws DataAccessException;

    void addOrder(OrderDto orderDto) throws DataAccessException;
    void addApproval(PayDto payDto) throws DataAccessException;
    void addTransaction(PayDto payDto) throws DataAccessException;
    void addTransactionCard(PayDto payDto) throws DataAccessException;
    void addTransactionError(PayDto payDto) throws DataAccessException;
    void addTransactionCardPg(PayDto payDto) throws DataAccessException;}

