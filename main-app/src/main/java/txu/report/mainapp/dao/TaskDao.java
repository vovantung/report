package txu.report.mainapp.dao;

import jakarta.persistence.ParameterMode;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.base.AbstractDao;
import txu.report.mainapp.dto.TaskDto;
import txu.report.mainapp.dto.TaskExtend;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.TaskEntity;


import java.util.List;

@Repository
public class TaskDao extends AbstractDao<TaskEntity> {

    @Transactional
    public TaskEntity save(TaskEntity task) {
        if (task.getId() == null || task.getId() == 0) {
            persist(task);
            return task;
        } else {
            return merge(task);
        }
    }

    @Override
    public TaskEntity findById(Object Id) {
        return super.findById(Id);
    }

    @Transactional
    public void remove(TaskEntity task) {
        task = merge(task);
        getEntityManager().remove(task);
    }


    public TaskExtend getById(Long taskId, String username) {

        Query query = getEntityManager().createQuery(
                "SELECT new txu.report.mainapp.dto.TaskExtend( " +
                        "t, CASE WHEN wl.levelNumber = 1 THEN '0' ELSE '1' END) " +
                        "FROM TaskEntity t " +
                        "JOIN WorkFlowLevelEntity wl " +
                        "ON t.workflow.id = wl.workflow.id " +
                        "WHERE t.id =:taskId AND wl.user.username = :username"
        );
        query.setParameter("username", username);
        query.setParameter("taskId", taskId);
        return (TaskExtend) query.getSingleResult();
    }


    @Transactional(readOnly = true)
    public List<TaskDto> getRelated_(Long userId) {
        String sql = """
                SELECT t.*,
                       TO_CHAR(CASE WHEN wl.level_number = 1 THEN 0 ELSE 1 END) AS vaitro
                FROM task t
                JOIN (
                    -- chỉ giữ các level thuộc prefix liên tục 1..k
                    SELECT wl2.workflow_id,
                           wl2.user_id,
                           wl2.level_number,
                           ROW_NUMBER() OVER (PARTITION BY wl2.workflow_id ORDER BY wl2.level_number) rn
                    FROM workflow_level wl2
                ) wl
                  ON wl.workflow_id = t.workflow_id
                 AND wl.level_number = wl.rn   -- chỉ giữ prefix
                WHERE wl.user_id = :userId
                """;

        Query query = getEntityManager().createNativeQuery(sql, "TaskDtoMapping");
        query.setParameter("userId", userId);
        List<TaskDto> results = query.getResultList();
        return results;
    }


    @Transactional(readOnly = true)
    public List<TaskDto> getRelated(Long userId) {
        StoredProcedureQuery spQuery = getEntityManager()
                .createStoredProcedureQuery("get_related_tasks", "TaskDtoMapping");
        // Đăng ký tham số
        spQuery.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        spQuery.registerStoredProcedureParameter("p_result", void.class, ParameterMode.REF_CURSOR);
        // Gán giá trị
        spQuery.setParameter("p_user_id", userId);
        // Gọi procedure
        spQuery.execute();
        List<TaskDto> results = spQuery.getResultList();
        return results;

    }

    public TaskEntity validateForSubmitTask(Long taskId, Long assigneeId) {
        Query query = getEntityManager().createQuery(
                "SELECT T " +
                        "FROM TaskEntity T " +
                        "JOIN WorkFlowLevelEntity WL " +
                        "ON T.workflow.id = WL.workflow.id " +
                        "WHERE T.id = :taskId AND T.assignee.id = :assigneeId AND T.assignee.id = WL.user.id AND WL.levelNumber = 1"

        );
        // Xác minh task hiện tại có phải được assigned cho người dùng hiện tại không,
        // và người dùng hiện tại có thuộc workflow và là người thực hiện chính của task không
        query.setParameter("assigneeId", assigneeId);
        query.setParameter("taskId", taskId);
        return getSingle(query);
    }

    public TaskEntity validateForApproveOrReject(Long taskId, Long assigneeId) {
        Query query = getEntityManager().createQuery(
                "SELECT T " +
                        "FROM TaskEntity T " +
                        "JOIN WorkFlowLevelEntity WL " +
                        "ON T.workflow.id = WL.workflow.id " +
                        "WHERE T.id = :taskId AND T.assignee.id = :assigneeId AND T.assignee.id = WL.user.id AND WL.levelNumber > 1"

        );
        // Xác minh task hiện tại có phải được assigned cho người dùng hiện tại không,
        // và người dùng hiện tại có thuộc workflow và là người thực hiện chính của task không
        query.setParameter("assigneeId", assigneeId);
        query.setParameter("taskId", taskId);
        return getSingle(query);
    }


    public int getLevelNumberOfAssignedInTask(Long taskId, Long assigneeId) {
        Query query = getEntityManager().createQuery(
                "SELECT WL.levelNumber " +
                        "FROM TaskEntity T " +
                        "JOIN WorkFlowLevelEntity WL " +
                        "ON T.workflow.id = WL.workflow.id " +
                        "WHERE T.id = :taskId AND T.assignee.id = :assigneeId AND T.assignee.id = WL.user.id"

        );
        query.setParameter("assigneeId", assigneeId);
        query.setParameter("taskId", taskId);
        // Lấy kết quả duy nhất
        Integer levelNumber = (Integer) query.getSingleResult();
        // Nếu bạn cần primitive int
        return (levelNumber != null) ? levelNumber : -1; // -1 để fallback nếu null
    }

    @Transactional
    public int countMemberInTask(Long taskId, Long assigneeId) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(T) " +
                        "FROM TaskEntity T " +
                        "JOIN WorkFlowLevelEntity WL " +
                        "ON T.workflow.id = WL.workflow.id " +
                        "WHERE T.id = :taskId AND T.assignee.id = :assigneeId"
        );
        query.setParameter("assigneeId", assigneeId);
        query.setParameter("taskId", taskId);

        int m;
        if (query.getSingleResult() != null) {
            m = ((Long) query.getSingleResult()).intValue();
        } else {
            m = 0;
        }
        return m;
    }

    public AccountEntity getUserInWorkflowLevel(Long taskId, int levelNumber) {

        Query query = getEntityManager().createQuery(
                "SELECT A " +
                        "FROM TaskEntity T " +
                        "JOIN WorkFlowLevelEntity WL " +
                        "ON T.workflow.id = WL.workflow.id " +
                        "JOIN AccountEntity A " +
                        "ON A.id = WL.user.id " +
                        "WHERE T.id = :taskId AND WL.levelNumber = :levelNumber"
        );
        query.setParameter("levelNumber", levelNumber);
        query.setParameter("taskId", taskId);
        return (AccountEntity) query.getResultList().get(0);
    }
}
