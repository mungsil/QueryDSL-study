package study.querydsl.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
class MemberTest {
    @Autowired
    EntityManager em;

    @Test
    public void testEntity() {
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

        //초기화
        em.flush(); //영속성 컨텍스트의 변경 내용을 DB에 반영하기 위해 실제 쿼리를 만들어서 DB에 날리게됨
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();
        for (Member m : members) {
            System.out.println("member = " + m);
            System.out.println("-> member.team = "+m.getTeam());
        }

        Assertions.assertThat(member1.getTeam()).isEqualTo(teamA);
    }
}