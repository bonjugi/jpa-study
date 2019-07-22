package dev.bonjugi.jpastudy.member.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(false)
public class MemberTest {

	@PersistenceContext
	private EntityManager em;

	@Test
	public void 등록후_조회(){

		// given
		Member bonjugi = new Member("bonjugi");
		em.persist(bonjugi);

		// when
		Member find = em.find(Member.class, bonjugi.getId());

		// then
		assertThat(find).isEqualTo(bonjugi);
	}
}