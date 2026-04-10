package txu.report.mainapp.dao;

import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.dto.Account1Dto;
import txu.report.mainapp.dto.Account2Dto;
import txu.report.mainapp.dto.DepartmentDto;
import txu.report.mainapp.entity.AccountEntity;

import java.util.Date;
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


//    @Transactional
    public AccountEntity getById(Object id) {
        Query query = getEntityManager().createQuery("SELECT A FROM AccountEntity AS A WHERE id=:id");
        query.setParameter("id", id);
        return getSingle(query);

    }
//
//    @Override
//    public AccountEntity findById(Object Id) {
//        return super.findById(Id);
//    }

    @Transactional
    public void delete(AccountEntity accountEntity) {
        remove(accountEntity);
    }

    public Account2Dto getByUsername(String username_) {
        Query query = getEntityManager().createQuery("SELECT " +
                " A.id,A.username,A.password,A.lastName,A.firstName,A.phoneNumber,A.avatarUrl,A.avatarFilename,A.email, A.createdAt, A.updatedAt," +
                " D.id, D.name" +
                " FROM AccountEntity AS A JOIN A.department D WHERE A.username=:username");
        query.setParameter("username", username_);
        Object[] rs = getSingleObject(query);

        Account2Dto account2Dto = new Account2Dto();

        Long accountId = ((Number) rs[0]).longValue();
        String username = (String) rs[1];
        String password = (String) rs[2];
        String lastName = (String) rs[3];
        String firstName = (String) rs[4];
        String phoneNumber = (String) rs[5];
        String avatarUrl = (String) rs[6];
        String avatarFilename = (String) rs[7];
        String email = (String) rs[8];
        Date createdAt = (Date) rs[9];
        Date updatedAt = (Date) rs[10];
        Long departmentId = ((Number)rs[11]).longValue();
        String departmentName = (String) rs[12];

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(departmentId);
        departmentDto.setName(departmentName);
        account2Dto.setDepartment(departmentDto);
        account2Dto.setId(accountId);
        account2Dto.setUsername(username);
        account2Dto.setPassword(password);
        account2Dto.setLastName(lastName);
        account2Dto.setFirstName(firstName);
        account2Dto.setPhoneNumber(phoneNumber);
        account2Dto.setAvatarUrl(avatarUrl);
        account2Dto.setAvatarFilename(avatarFilename);
        account2Dto.setEmail(email);
        account2Dto.setCreatedAt(createdAt);
        account2Dto.setUpdatedAt(updatedAt);
        return account2Dto;
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

    public List<Account1Dto> getByIds(List<Long> deptIds) {
        String queryString = "SELECT new txu.report.mainapp.dto.Account1Dto(A.id, A.firstName, A.lastName, A.department.id)" +
                " FROM AccountEntity A" +
                " WHERE A.department.id IN :deptIds";
        Query query = getEntityManager().createQuery(queryString);
        query.setParameter("deptIds", deptIds);
        return query.getResultList();
    }

}
