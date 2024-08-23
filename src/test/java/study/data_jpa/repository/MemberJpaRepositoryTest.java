package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    public void basicCRUD() throws Exception
    {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);


        //단건 조회 검
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);


        //리스트 조회 검
        List<Member> members = memberJpaRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        //카운트 검
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

//        // 변겸감지 검
//        findMember1.setUsername("newMember");


        //삭제 검
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        long deleteCount = memberJpaRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByNameAndAgeGreaterThenTest() throws Exception
    {
        // given
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        List<Member> members = memberJpaRepository.findByNameAndAgeGreaterThan("member", 10);

        System.out.println("member.name = " + members.get(0).getUsername() + ", member.age = " + members.get(0).getAge());

        // then
        assertThat(members.get(0).getUsername()).isEqualTo("member");
        assertThat(members.get(0).getAge()).isEqualTo(20);
    }

}