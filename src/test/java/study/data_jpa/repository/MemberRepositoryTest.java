package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired
    EntityManager em;

    @Test
    public void basicCRUD() throws Exception
    {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);


        //단건 조회 검
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);


        //리스트 조회 검
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        //카운트 검
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

//        // 변겸감지 검
//        findMember1.setUsername("newMember");


        //삭제 검
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByNameAndAgeGreaterThenTest() throws Exception
    {
        // given
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("member", 10);

        System.out.println("member.name = " + members.get(0).getUsername() + ", member.age = " + members.get(0).getAge());

        // then
        assertThat(members.get(0).getUsername()).isEqualTo("member");
        assertThat(members.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testQuery() throws Exception
    {
        // given
        Member member1 = new Member("member", 10);
        Member member2 = new Member("member", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> findMember = memberRepository.findUser("member", 10);

        // then
        assertThat(findMember.get(0)).isEqualTo(member2);
    }
    
    
    @Test
    public void findMemberDtoTest() throws Exception
    {
        // given
        Team team1 = new Team("team1");
        teamRepository.save(team1);

        Member member1 = new Member("member1", 10);
        member1.setTeam(team1);
        memberRepository.save(member1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }
    
    @Test
    public void findByNames() throws Exception
    {
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);


        List<Member> byNames = memberRepository.findByNames(Arrays.asList("member1", "member2"));
        for (Member byName : byNames) {
            System.out.println("byName = " + byName);
        }
    }

    @Test
    public void returnType() throws Exception
    {
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> List = memberRepository.findListByUsername("member1");
        Member Member = memberRepository.findMemberByUsername("member1");
        Optional<Member> optional = memberRepository.findOptionalByUsername("member1");

        System.out.println("List = " + List);
        System.out.println("Member = " + Member);
        System.out.println("optional = " + optional);

    }


    @Test
    public void paging() throws Exception
    {
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));


        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }

        System.out.println("totalElements = " + totalElements);


        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() throws Exception
    {
        // given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));
        // when


        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> result = memberRepository.findListByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        // then

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

//        Member findMember = memberRepository.findByUsername("member1");

//        List<Member> members = memberRepository.findMemberFetchJoin();

        List<Member> all = memberRepository.findAll();

        for (Member member : all) {
            System.out.println("member = " + member.getTeam().getClass());
        }

//        System.out.println("username = " + findMember.getUsername());
//        System.out.println("age = " + findMember.getAge());
//        System.out.println("team name = " + findMember.getTeam().getName());
    }

    @Test
    public void queryHint() throws Exception
    {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        // then
        em.flush();
    }

    @Test
    public void lock() throws Exception
    {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");

        // then

    }

}
