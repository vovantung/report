package txu.report.mainapp.dao;

import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.entity.AccountEntity;

import java.util.List;

@Repository
public class AccountDao extends AbstractDao<AccountEntity> {
    @Transactional
    public AccountEntity save(AccountEntity accountEntity) {
        if (accountEntity.getId() == null || accountEntity.getId() == 0) {
            persist(accountEntity);
            return accountEntity;
        } else {
            return merge(accountEntity);
        }
    }

    @Override
    public AccountEntity findById(Object Id) {
        return super.findById(Id);
    }

    @Transactional
    public void remove(AccountEntity accountEntity) {
        accountEntity = merge(accountEntity);
        getEntityManager().remove(accountEntity);
    }

    public AccountEntity getByUsername(String username) {
        StringBuilder queryString = new StringBuilder("SELECT A FROM AccountEntity AS A WHERE username=:username");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setParameter("username", username);
        return getSingle(query);
    }

    public AccountEntity getByEmail(String email) {
        StringBuilder queryString = new StringBuilder("SELECT A FROM AccountEntity AS A WHERE email=:email");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setParameter("email", email);
        return getSingle(query);
    }

    public List<AccountEntity> getWithLimit(int limit) {
        StringBuilder queryString = new StringBuilder("SELECT A FROM AccountEntity AS A ORDER BY A.createdAt DESC");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setMaxResults(limit);
        return getRessultList(query);

    }

}
