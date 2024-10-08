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


    @Test
    public void paging() throws Exception
    {
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",10));
        memberJpaRepository.save(new Member("member3",10));
        memberJpaRepository.save(new Member("member4",10));
        memberJpaRepository.save(new Member("member5",10));
        memberJpaRepository.save(new Member("member6",10));
        memberJpaRepository.save(new Member("member7",10));
        memberJpaRepository.save(new Member("member8",10));
        memberJpaRepository.save(new Member("member9",10));
        memberJpaRepository.save(new Member("member10",10));


        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByAge(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(10);

    }

    @Test
    public void bulkUpdate() throws Exception
    {
        // given
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",19));
        memberJpaRepository.save(new Member("member3",20));
        memberJpaRepository.save(new Member("member4",21));
        memberJpaRepository.save(new Member("member5",40));
        // when


        int resultCount = memberJpaRepository.bulkAgePlus(20);
        // then

        assertThat(resultCount).isEqualTo(3);
    }

}