package study.data_jpa.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.data_jpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public Member find(Long memberId) {
        return em.find(Member.class,memberId);
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
        return members;
    }

    public long count() {
        return em.createQuery("select count(m) from Member m",Long.class).getSingleResult();
    }


    public List<Member> findByNameAndAgeGreaterThan(String username,int age) {
        return em.createQuery("select m from Member m where m.username = :username and age > :age",Member.class)
                .setParameter("username",username)
                .setParameter("age",age)
                .getResultList();
    }

    public List<Member> findByAge(int age, int offset, int limit) {
         return em.createQuery("select m from Member m " +
                 "where m.age = :age " +
                 "order by m.username desc",Member.class)
                 .setParameter("age", age)
                 .setFirstResult(offset)
                 .setMaxResults(limit)
                 .getResultList();
    }

    public long totalCount(int age) {
         return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                 .setParameter("age", age)
                 .getSingleResult();
    }

    public int bulkAgePlus(int age) {
        int resultCount = em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
        return resultCount;
    }
}
