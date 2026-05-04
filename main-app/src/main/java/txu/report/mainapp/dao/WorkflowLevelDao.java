package txu.report.mainapp.dao;


import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.entity.WorkFlowLevelEntity;

import java.util.List;

@Repository
public class WorkflowLevelDao extends AbstractDao<WorkFlowLevelEntity> {

    @Transactional
    public WorkFlowLevelEntity save(WorkFlowLevelEntity workFlowLevel) {
        if (workFlowLevel.getId() == null || workFlowLevel.getId() == 0) {
            persist(workFlowLevel);
            return workFlowLevel;
        } else {
            return merge(workFlowLevel);
        }
    }

    @Override
    public WorkFlowLevelEntity findById(Object Id) {
        return super.findById(Id);
    }

    @Transactional
    public void remove(WorkFlowLevelEntity workFlowLevel) {
        workFlowLevel = merge(workFlowLevel);
        getEntityManager().remove(workFlowLevel);
    }
    
    public List<WorkFlowLevelEntity> getByWorkflowId(Long id) {
        Query query = getEntityManager().createQuery("SELECT WL FROM WorkFlowLevelEntity AS WL WHERE  WL.workflow.id =: id ORDER BY WL.levelNumber DESC");
        query.setParameter("id", id);
        return getRessultList(query);

    }

    @Transactional(readOnly = true)
    public int countContinuousLevels(Long taskId) {
        String sql = """
        select count(*) 
        from (
            select wl.level_number, 
                   row_number() over (order by wl.level_number) as rn
            from WORKFLOW_LEVEL wl
            join WORKFLOW_CONFIG wc on wl.workflow_id = wc.id
            join task t on t.workflow_id = wc.id
            where t.id = :taskId
        ) tmp
        where tmp.level_number = tmp.rn
    """;

        Query query = getEntityManager().createNativeQuery(sql);
        query.setParameter("taskId", taskId);

        Number result = (Number) query.getSingleResult();
        return result.intValue();
    }

    public int countContinuousLevels_(Long workflowConfigId) {
        String sql = """
        select count(*)
        from (
            select wl.level_number,
                   row_number() over (order by wl.level_number) as rn
            from WORKFLOW_LEVEL wl
            where wl.workflow_id = :workflowConfigId
        ) tmp
        where tmp.level_number = tmp.rn
        """;

        Query query = getEntityManager().createNativeQuery(sql);
        query.setParameter("workflowConfigId", workflowConfigId);

        Number result = (Number) query.getSingleResult();
        return result.intValue();
    }
}
