package txu.report.mainapp.dao;


import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.entity.RoleEntity;

import java.util.List;

@Repository
public class RoleDao extends AbstractDao<RoleEntity> {

    @Transactional
    public RoleEntity save(RoleEntity roleEntity) {
        if (roleEntity.getId() == null || roleEntity.getId() == 0) {
            persist(roleEntity);
            return roleEntity;
        } else {
            return merge(roleEntity);
        }
    }

    @Override
    public RoleEntity findById(Object Id) {
        return super.findById(Id);
    }

    @Transactional
    public void remove(RoleEntity roleEntity) {
        roleEntity = merge(roleEntity);
        getEntityManager().remove(roleEntity);
    }

    public List<RoleEntity> getWithLimit(int limit) {
        StringBuilder queryString = new StringBuilder("SELECT R FROM RoleEntity AS R ORDER BY R.createdAt DESC");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setMaxResults(limit);
        return getRessultList(query);

    }

}
