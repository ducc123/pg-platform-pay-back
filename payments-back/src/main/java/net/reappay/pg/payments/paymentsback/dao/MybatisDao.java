package net.reappay.pg.payments.paymentsback.dao;

import net.reappay.pg.payments.paymentsback.dto.OrderDto;
import net.reappay.pg.payments.paymentsback.dto.PayDto;
import net.reappay.pg.payments.paymentsback.dto.ApprDto;
import net.reappay.pg.payments.paymentsback.dto.UserDto;
import net.reappay.pg.payments.paymentsback.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
@Repository
public interface MybatisDao {

    String getTranSeq() throws DataAccessException;

    int findInstallmentMonthByNo(PayDto payDto) throws DataAccessException;

    /**
     * 회원정보 가져오기
     */
    UserDto findUserInfo(String CustId) throws DataAccessException;

    /**
     * 회원별 한도 가져오기
     * */
    String limitAmtCheck(PayDto payDto) throws DataAccessException;

    Map<String, String> findCardIssCdByCardNoString(PayDto payDto) throws DataAccessException;
    /**
     * 주문정보 가지고 오기
     * @param TranSeq
     */
    ApprDto findApprovalTranSeq(String TranSeq) throws DataAccessException;

    int countApprovalTranSeq(String TranSeq) throws DataAccessException;
    int countTranCardPgTranSeq(PayDto payDto) throws DataAccessException;

    PayTidInfo findPgTidInfo(OrderDto orderDto) throws DataAccessException;
    PayTidInfo findPgTidInfo2(PayDto payDto) throws DataAccessException;
    PayTerminalInfo findTerminal(OrderDto orderDto) throws DataAccessException;
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
