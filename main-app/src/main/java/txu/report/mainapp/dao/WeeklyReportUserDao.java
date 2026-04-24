package txu.report.mainapp.dao;


import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.entity.WeeklyReportEntity;
//import txu.user.mainapp.base.AbstractDao;
//import txu.user.mainapp.entity.WeeklyReportEntity;

import java.util.Date;
import java.util.List;

@Repository
public class WeeklyReportUserDao extends AbstractDao<WeeklyReportEntity> {

    @Transactional
    public WeeklyReportEntity save(WeeklyReportEntity weeklyReportEntity) {
        if (weeklyReportEntity.getId() == null || weeklyReportEntity.getId() == 0) {
            persist(weeklyReportEntity);
            return weeklyReportEntity;
        } else {
            return merge(weeklyReportEntity);
        }
    }

    @Override
    public WeeklyReportEntity findById(Object Id) {
        return super.findById(Id);
    }

    @Transactional
    public void remove(WeeklyReportEntity weeklyReportEntity) {
        weeklyReportEntity = merge(weeklyReportEntity);
        getEntityManager().remove(weeklyReportEntity);
    }


    public List<WeeklyReportEntity> getByDepartmentIdFromTo(Date from, Date to, Integer departmentId) {
        Query query = getEntityManager().createQuery("SELECT W FROM WeeklyReportEntity AS W " +
                "JOIN FETCH W.department WHERE W.uploadedAt >=:from AND W.uploadedAt <=: to AND W.department.id =:departmentId  ORDER BY W.uploadedAt DESC");
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("departmentId", departmentId);
        return getRessultList(query);
    }

    public List<Object[]> getByDepartmentIdFromTo_(Date from, Date to, Integer departmentId) {
        StringBuilder queryString = new StringBuilder("SELECT W.id, W.filename, W.originName, W.url, W.uploadedAt,  D.id, D.name" +
                " FROM WeeklyReportEntity W " +
                " LEFT JOIN W.department D " +
                " WHERE W.uploadedAt >=:from AND W.uploadedAt <=: to AND D.id =:departmentId");
        queryString.append(" ORDER BY W.uploadedAt DESC");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("departmentId", departmentId);
        query.setMaxResults(100);
        List<Object[]> rs = query.getResultList();
        return query.getResultList();
    }

    public WeeklyReportEntity getSingleByDepartmentIdFromTo(Date from, Date to, Integer departmentId) {
        Query query = getEntityManager().createQuery("SELECT W FROM WeeklyReportEntity AS W WHERE W.uploadedAt >=:from AND W.uploadedAt <=: to AND W.department.id =:departmentId  ORDER BY W.uploadedAt DESC");
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("departmentId", departmentId);
        return getSingle(query);
    }

    public List<WeeklyReportEntity> getFromTo(Date from, Date to) {
        StringBuilder queryString = new StringBuilder("SELECT W FROM WeeklyReportEntity AS W WHERE W.uploadedAt >=:from AND W.uploadedAt <=: to  ORDER BY W.uploadedAt DESC");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setParameter("from", from);
        query.setParameter("to", to);
        return getRessultList(query);
    }

}
