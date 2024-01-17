package study.querydsl.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach //개별 테스트 실행 전 해당 함수 호출
    public void before() {
        queryFactory = new JPAQueryFactory(em); //필드 level로 가져가도 괜찮다.

        Team teamA = Team.builder().name("teamA").build();
        Team teamB = Team.builder().name("teamB").build();

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = Member.builder().age(10).username("바닐라라떼").team(teamA).build();
        Member member2 = Member.builder().age(20).username("초코라떼").team(teamA).build();

        Member member3 = Member.builder().age(30).username("토피넛라떼").team(teamB).build();
        Member member4 = Member.builder().age(40).username("딸기라떼").team(teamB).build();

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL() {
        //find member1
        String qlString = "select m from Member m where m.username = :username";
        Member findJPQL = em.createQuery(qlString, Member.class)
                .setParameter("username", "바닐라라떼")
                .getSingleResult();
        assertThat(findJPQL.getUsername()).isEqualTo("바닐라라떼");
    }

    @Test
    public void startQuerydsl() {
//        QMember m = new QMember("m"); //방법 1: 별칭 지정 *** 같은 table을 join해서 사용해야하는 경우에 사용
//        QMember member = QMember.member; //방법 2: QMember에서 생성해놓은 member 사용

        Member findDSL = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("바닐라라떼"))
                .fetchOne();
        assertThat(findDSL.getUsername()).isEqualTo("바닐라라떼");
    }

    //검색 조건 쿼리
    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("바닐라라떼")
                        .and(member.age.eq(10)))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("바닐라라떼");

        Member fetchOne = queryFactory
                .selectFrom(member)
                .where(member.username.like("바닐라%"))
                .fetchOne();

        assertThat(fetchOne.getUsername()).isEqualTo("바닐라라떼");

    }
    @Test
    public void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("바닐라라떼"),
                        member.age.eq(10))
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("바닐라라떼");
    }

//    결과 조회
    @Test
    public void resultFetch() {
        //== 리스트 조회 ==//
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        //== 단건 조회 ==//
        Member fetchOne = queryFactory
                .selectFrom(member)
                .where(member.age.eq(10))
                .fetchOne();

        Member fetchFirst = queryFactory
                .selectFrom(member)
//                .limit(1).fetchOne(); // fetchFirst와 동일
                .fetchFirst();

        //== 페이징 정보를 포함한 결과 반환, count 쿼리가 추가 실행됨 ==//
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        results.getTotal();
        List<Member> content = results.getResults();

        //== count 쿼리로 count 수 조회 ==//
        long count = queryFactory
                .selectFrom(member)
                .fetchCount();


    }

}
