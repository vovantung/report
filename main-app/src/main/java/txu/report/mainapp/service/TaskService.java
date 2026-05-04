package txu.report.mainapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import txu.common.exception.NotFoundException;
import txu.report.mainapp.dao.AccountDao;
import txu.report.mainapp.dao.TaskDao;
import txu.report.mainapp.dao.WorkflowDao;
import txu.report.mainapp.dao.WorkflowLevelDao;
import txu.report.mainapp.dto.TaskDto;
import txu.report.mainapp.dto.TaskExtend;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.TaskEntity;
import txu.report.mainapp.entity.WorkFlowEntity;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskDao taskDao;
    private final WorkflowLevelDao workflowLevelDao;
    private final WorkflowDao workflowDao;
    private final AccountDao accountDao;

    public TaskExtend getById_(Long taskId, String username) {

        TaskExtend task = taskDao.getById_(taskId, username);
        if (task == null) {
            throw new NotFoundException("User is not found");
        }
        return task;
    }


    public List<TaskDto> getRelated(String username) {

        AccountEntity user = accountDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }

        List<TaskDto> tasks = taskDao.getRelated(user.getId());
        if (tasks == null) {
            throw new NotFoundException("User is not found");
        }
        return tasks;
    }

    @Transactional
    public void add(String username, Long workflowId, String title, String description) {

        AccountEntity user = accountDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }

        WorkFlowEntity workFlow = workflowDao.findById(workflowId);
        if (workFlow == null) {
            throw new NotFoundException("Workflow is not found");
        }

        int totalStep = workflowLevelDao.countContinuousLevels_(workFlow.getId());

        AccountEntity asigneer = workflowDao.getUserInWorkflowLevel(workflowId, 1);

        TaskEntity task = new TaskEntity();
        task.setWorkflow(workFlow);
        task.setCreateBy(user);
        task.setTitle(title);
        task.setDescription(description);
        task.setTotalStep(totalStep);
        task.setAssignee(asigneer);
        task.setStatus("PENDING");
        task.setCurrentLevel(1);
        taskDao.save(task);
    }

    public boolean submitTask(Long taskId, String username) {
        AccountEntity user = accountDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }

        int level = taskDao.getLevelNumberOfAssignedInTask(taskId, user.getId());

        if (level != 1) {
            return false;
        }

        AccountEntity assigneeNext = taskDao.getUserInWorkflowLevel(taskId, 2);
        if (assigneeNext == null) {
            return false;
        }
        TaskEntity task = taskDao.getById(taskId);
        task.setAssignee(assigneeNext);
        task.setCurrentLevel(2);
        task.setStatus("PENDING");
        taskDao.save(task);
        return true;
    }

    @Transactional
    public boolean approveTask(Long taskId, String username) {
        AccountEntity user = accountDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }

        int level = taskDao.getLevelNumberOfAssignedInTask(taskId, user.getId());

        if (level < 2) {
            return false;
        }

        int countMembers = workflowLevelDao.countContinuousLevels(taskId);

        TaskEntity task = taskDao.getById(taskId);

        if (level < countMembers) {
            // Người approve trung gian, chuyển lên cấp trên
            AccountEntity assigneeNext = taskDao.getUserInWorkflowLevel(taskId, level + 1);

            if (assigneeNext == null) {
                return false;
            }
            task.setAssignee(assigneeNext);
            task.setCurrentLevel(level + 1);
            task.setStatus("APPROVED");
            taskDao.save(task);
        } else {
            task.setStatus("DONE");
            task.setAssignee(null);
            taskDao.save(task);
            // Người approve cuối cùng trong workflow -> task Done
        }
        return true;
    }
}
