package txu.report.mainapp.dao;


import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import txu.report.mainapp.base.AbstractDao;

import txu.report.mainapp.dto.Department1Dto;
import txu.report.mainapp.entity.DepartmentEntity;

import java.util.List;

@Slf4j
@Repository
public class DepartmentDao extends AbstractDao<DepartmentEntity> {

    @Transactional
    public DepartmentEntity save(DepartmentEntity departmentEntity) {
        if (departmentEntity.getId() == null || departmentEntity.getId() == 0) {
            persist(departmentEntity);
            return departmentEntity;
        } else {
            return merge(departmentEntity);
        }
    }

    @Override
    public DepartmentEntity findById(Object Id) {
        return super.findById(Id);
    }

    @Transactional
    public void remove(DepartmentEntity departmentEntity) {
        departmentEntity = merge(departmentEntity);
        getEntityManager().remove(departmentEntity);
    }

//    public DepartmentEntity getByUsername(String username) {
//        StringBuilder queryString = new StringBuilder("SELECT A FROM AccountEntity AS A WHERE username=:username");
//        Query query = getEntityManager().createQuery(queryString.toString());
//        query.setParameter("username", username);
//        return getSingle(query);
//    }

    public List<DepartmentEntity> getWithLimit(int limit) {
        StringBuilder queryString = new StringBuilder("SELECT D FROM DepartmentEntity AS D ORDER BY D.createdAt DESC");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setMaxResults(limit);
        return getRessultList(query);

    }

    public List<Department1Dto> getPaging(long keyOffset, int limit, String keySearch) {
        // Không tra EXISTS, tương tự dùng LEFT JOIN cho Department (vì lấy cả Department không có Account nào trong nó).
        StringBuilder queryString = new StringBuilder("SELECT new txu.report.mainapp.dto.Department1Dto(D.id, D.name, D.description, D.createdAt, D.updatedAt)" +
                " FROM DepartmentEntity D" +
                " WHERE D.id >= : keyOffset");
        if(keySearch != null && !keySearch.isEmpty()) {
            queryString.append(" AND D.name LIKE :keySearch");
        }
        queryString.append(" ORDER BY D.id DESC");

        Query query = getEntityManager().createQuery(queryString.toString());
        query.setParameter("keyOffset", keyOffset);
        if(keySearch != null && !keySearch.isEmpty()) {
            query.setParameter("keySearch", "%" + keySearch + "%");
        }
        query.setMaxResults(limit);
        return query.getResultList();
    }

}
