package txu.report.mainapp.dao;


import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.entity.WorkFlowEntity;


import java.util.List;

@Repository
public class WorkflowDao extends AbstractDao<WorkFlowEntity> {

    @Transactional
    public WorkFlowEntity save(WorkFlowEntity workFlow) {
        if (workFlow.getId() == null || workFlow.getId() == 0) {
            persist(workFlow);
            return workFlow;
        } else {
            return merge(workFlow);
        }
    }

    @Override
    public WorkFlowEntity findById(Object Id) {
        return super.findById(Id);
    }

    @Transactional
    public void remove(WorkFlowEntity workFlow) {
        workFlow = merge(workFlow);
        getEntityManager().remove(workFlow);
    }

    public List<WorkFlowEntity> getWithLimit(int limit) {
        StringBuilder queryString = new StringBuilder("SELECT W FROM WeeklyReportEntity AS W ORDER BY W.uploadedAt DESC");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setMaxResults(limit);
        return getRessultList(query);

    }
}
