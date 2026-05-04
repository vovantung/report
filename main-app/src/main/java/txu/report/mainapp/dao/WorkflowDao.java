package txu.report.mainapp.dao;


import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.WorkFlowEntity;


import java.util.List;
import java.util.Optional;

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

    public List<WorkFlowEntity> get(int limit) {
        StringBuilder queryString = new StringBuilder("SELECT W FROM WorkFlowEntity AS W ORDER BY W.createdAt DESC");
        Query query = getEntityManager().createQuery(queryString.toString());
        query.setMaxResults(limit);
        return getRessultList(query);

    }

    public AccountEntity getUserInWorkflowLevel(Long workflowId, int levelNumber) {
        List<AccountEntity> result = getEntityManager().createQuery(
                        "SELECT wl.user " +
                                "FROM WorkFlowLevelEntity wl " +
                                "WHERE wl.workflow.id = :workflowId " +
                                "AND wl.levelNumber = :levelNumber",
                        AccountEntity.class
                )
                .setParameter("workflowId", workflowId)
                .setParameter("levelNumber", levelNumber)
                .getResultList();

        return result.size() > 0 ? result.get(0) : null;
    }
}
