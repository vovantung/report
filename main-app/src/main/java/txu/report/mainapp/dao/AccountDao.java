package txu.report.mainapp.dao;

import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.dto.Account1Dto;
import txu.report.mainapp.entity.AccountEntity;

import java.util.List;

@Repository
public class AccountDao extends AbstractDao<AccountEntity> {
    @Transactional
    public AccountEntity save(AccountEntity accountEntity) {
        if (accountEntity.getId() == null || accountEntity.getId() <= 0) {
            persist(accountEntity);
            return accountEntity;
        } else {
            return merge(accountEntity);
        }
    }

    public AccountEntity getById(Object Id) {
        return findById(Id);
    }

    @Transactional
    public void delete(AccountEntity accountEntity) {
        remove(accountEntity);
    }

    public AccountEntity getByUsername(String username) {
        Query query = getEntityManager().createQuery("SELECT A FROM AccountEntity AS A WHERE username=:username");
        query.setParameter("username", username);
        return getSingle(query);
    }

    public AccountEntity getByEmail(String email) {
        Query query = getEntityManager().createQuery("SELECT A FROM AccountEntity AS A WHERE email=:email");
        query.setParameter("email", email);
        return getSingle(query);
    }

    public List<Object[]> getPaging(long keyOffset, int limit, String keySearch) {
        StringBuilder queryString = new StringBuilder("SELECT A.id, A.username, A.firstName, A.lastName, A.createdAt, A.updatedAt, D.id, D.name" +
                " FROM AccountEntity A" +
                " LEFT JOIN A.department D " +
                " WHERE A.id >= : keyOffset");

        if (keySearch != null && !keySearch.isEmpty()) {
            queryString.append(" AND (A.firstName LIKE :keySearch OR A.lastName LIKE :keySearch)");
        }
        queryString.append(" ORDER BY A.id DESC");

        Query query = getEntityManager().createQuery(queryString.toString());
        query.setParameter("keyOffset", keyOffset);
        if (keySearch != null && !keySearch.isEmpty()) {
            query.setParameter("keySearch", "%" + keySearch + "%");
        }
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public List<Account1Dto> getByIds(List<Integer> deptIds) {
        String queryString = "SELECT new txu.report.mainapp.dto.Account1Dto(A.id, A.firstName, A.lastName, A.department.id)" +
                " FROM AccountEntity A" +
                " WHERE A.department.id IN :deptIds";
        Query query = getEntityManager().createQuery(queryString);
        query.setParameter("deptIds", deptIds);
        return query.getResultList();
    }

}
