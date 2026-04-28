package txu.report.mainapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import txu.common.exception.NotFoundException;
import txu.report.mainapp.dao.AccountDao;
import txu.report.mainapp.dao.TaskDao;
import txu.report.mainapp.dao.WorkflowLevelDao;
import txu.report.mainapp.dto.TaskDto;
import txu.report.mainapp.dto.TaskExtend;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.TaskEntity;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskDao taskDao;
    private final WorkflowLevelDao workflowLevelDao;
    private final AccountDao accountDao;

    public TaskExtend getById(Long taskId, String username) {

        TaskExtend task = taskDao.getById(taskId, username);
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


    public boolean submitTask(Long taskId, String username) {



        AccountEntity user = accountDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }

        TaskEntity task = taskDao.validateForSubmitTask(taskId, user.getId());
        if (task == null) {
            // Không tồn tại task, hoặc task hiện tại không được assigned cho người dùng hiện tại,
            // hoặc task hiện tại không phải task để người dùng hiện tại submit(có thể là quyền approve)
            // hoặc người dùng hiện tại không thuộc workflow được gắn cho task hiện tại
            return false;
        }

        if (task.getCurrentLevel() > 2) {
            AccountEntity assigneeNext = taskDao.getUserInWorkflowLevel(taskId, task.getCurrentLevel());
            if (assigneeNext == null) {
                return false;
            }
            task.setAssignee(assigneeNext);
            task.setStatus("PENDING");
            taskDao.save(task);
        } else {
            AccountEntity assigneeNext = taskDao.getUserInWorkflowLevel(taskId, 2);
            if (assigneeNext == null) {
                return false;
            }
            task.setAssignee(assigneeNext);
            task.setStatus("PENDING");
            taskDao.save(task);
        }
        return true;
    }

    @Transactional
    public boolean approveTask(Long taskId, String username) {
        AccountEntity user = accountDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }


        TaskEntity task = taskDao.validateForApproveOrReject(taskId, user.getId());
        if (task == null) {
            // Không tồn tại task, hoặc task hiện tại không được assigned cho người dùng hiện tại,
            // hoặc task hiện tại không phải task để người dùng hiện tại approve hoăck reject(có thể thuộc task để submit)
            // hoặc người dùng hiện tại không thuộc workflow được gắn cho task hiện tại
            return false;
        }

        int level = taskDao.getLevelNumberOfAssignedInTask(taskId, user.getId());
//        int countMembers = taskDao.countMemberInTask(taskId, userDetails.getId());
        int countMembers = workflowLevelDao.countContinuousLevels(taskId);

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
