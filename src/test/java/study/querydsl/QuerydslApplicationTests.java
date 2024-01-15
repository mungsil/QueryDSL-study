package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional //테스트 케이스에 선언 시 테스트 종료 후 롤백
//@Commit //공부할 때만 사용!! DB에 값 어떻게 들어가는지 궁금하니까 (~˘▾˘)~♫•*¨*•.¸¸♪
class QuerydslApplicationTests {

	@Autowired //@PersistenceContext도 가능
	EntityManager em;
	@Test
//	@Rollback(value = false)
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = new QHello("h");
//		QHello qHello = QHello.hello도 가능;

		Hello result = query
				.selectFrom(qHello)
				.fetchOne();

		assertThat(result).isEqualTo(hello);
		assertThat(result.getId()).isEqualTo(hello.getId());
	}



}
