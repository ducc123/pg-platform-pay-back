package net.reappay.pg.payments.paymentsback.dao;

import net.reappay.pg.payments.paymentsback.dto.OrderDto;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface MybatisDao {

    String getTranSeq() throws DataAccessException;

    /**
     * 회원정보 가져오기
     */
    String findPgMerchNoUserId(String CustId) throws DataAccessException;
    String findPgTidUserId(String CustId) throws DataAccessException;
    String findUserSeqUserId(String CustId) throws DataAccessException;
    /**
     * 주문정보 디비저장
     *
     * @param orderDto
     * @throws DataAccessException
     */
    void addOrder(OrderDto orderDto) throws DataAccessException;

    /**
     * 승인정보 디비저장
     *
     * @param payDto
     * @throws DataAccessException
     */
    void addApproval(PayDto payDto) throws DataAccessException;

    /**
     * 승인정보 디비저장(tb_transaction)
     *
     * @param payDto
     * @throws DataAccessException
     */
    void addTransaction(PayDto payDto) throws DataAccessException;

    /**
     * 승인정보 디비저장(tb_transaction_card)
     *
     * @param payDto
     * @throws DataAccessException
     */
    void addTransactionCard(PayDto payDto) throws DataAccessException;

    /**
     * 승인정보 디비저장(tb_transaction_error)
     *
     * @param payDto
     * @throws DataAccessException
     */
    void addTransactionError(PayDto payDto) throws DataAccessException;

    /**
     * 승인정보 디비저장(tb_tran_cardpg_yyDD)
     *
     * @param payDto
     * @throws DataAccessException
     */
    void addTransactionCardPg(PayDto payDto) throws DataAccessException;

}
