package txu.report.mainapp.dao;

import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.TaskTimelineEntity;

import java.util.List;

@Repository
public class TaskTimelineDao extends AbstractDao<TaskTimelineEntity> {
    @Transactional
    public TaskTimelineEntity save(TaskTimelineEntity taskTimelineEntity) {
        if (taskTimelineEntity.getId() == null || taskTimelineEntity.getId() <= 0) {
            persist(taskTimelineEntity);
            return taskTimelineEntity;
        } else {
            return merge(taskTimelineEntity);
        }
    }

    public List<TaskTimelineEntity> getBytaskId(Object taskId) {
        Query query = getEntityManager().createQuery("SELECT TT FROM TaskTimelineEntity AS TT WHERE taskId=:taskId");
        query.setParameter("taskId", taskId);
        return query.getResultList();
//        return getSingle(query);
    }
}
