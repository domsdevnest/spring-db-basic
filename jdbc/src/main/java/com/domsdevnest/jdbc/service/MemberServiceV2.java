package com.domsdevnest.jdbc.service;

import com.domsdevnest.jdbc.domain.Member;
import com.domsdevnest.jdbc.repository.MemberRepositoryV1;
import com.domsdevnest.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private  final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try {
            con.setAutoCommit(false);
            bizLogic(con,fromId, toId, money);
            con.commit(); //성공시 커밋
        } catch (Exception e) {
            con.rollback(); //실피시 롤백
            throw new IllegalStateException(e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); //
                    con.close();
                } catch (Exception e) {
                    log.info("error", e);
                }
            }
        }
    }

    private void bizLogic(Connection con,String fromId, String toId, int money ) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
