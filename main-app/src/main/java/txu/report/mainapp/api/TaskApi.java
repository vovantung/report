package txu.report.mainapp.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import txu.common.exception.NotFoundException;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dao.AccountDao;
import txu.report.mainapp.dao.TaskDao;
import txu.report.mainapp.dto.TaskDto;
import txu.report.mainapp.dto.TaskExtend;
import txu.report.mainapp.dto.TaskRequest;
import txu.report.mainapp.dto.TaskWorkflowRequest;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.TaskEntity;
import txu.report.mainapp.entity.WorkFlowEntity;
import txu.report.mainapp.service.TaskService;
import txu.report.mainapp.service.WorkflowService;
import txu.report.mainapp.util.JwtUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class TaskApi extends AbstractApi {

    private final TaskService taskService;
    private final WorkflowService workflowService;
    private final TaskDao taskDao;
    private final AccountDao accountDao;

//    @PostMapping(value = "/get-by-id", consumes = "application/json")
//    public List<TaskExtend> getById(@RequestBody TaskRequest request) {
//        return taskService.getByAssigned(request.getAssigneeId());
//    }

    @PostMapping(value = "/user/workflows", consumes = "application/json")
    public List<WorkFlowEntity> getWorkflows( HttpServletRequest httpServletRequest) {
       return workflowService.get();
    }

    @PostMapping(value = "/user/task/add", consumes = "application/json")
    public void add(@RequestBody TaskWorkflowRequest request,HttpServletRequest httpServletRequest) throws Exception {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> claims = JwtUtils.decode(token);
         taskService.add(claims.get("preferred_username").toString(), request.getWorkflowId(), request.getTitle(), request.getDescription());
    }

    @PostMapping(value = "/user/task/get-related", consumes = "application/json")
    public List<TaskDto> getRelated( HttpServletRequest httpServletRequest) throws Exception {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> claims = JwtUtils.decode(token);
        return taskService.getRelated(claims.get("preferred_username").toString());
    }

    @PostMapping(value = "/user/task/get-by-id", consumes = "application/json")
    public TaskExtend getById(@RequestBody TaskRequest request, HttpServletRequest httpServletRequest) throws Exception {

        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> claims = JwtUtils.decode(token);
        return taskService.getById_(request.getTaskId(), claims.get("preferred_username").toString());
    }

    @PostMapping(value = "/user/task/submit-task", consumes = "application/json")
    public boolean submitTask(@RequestBody TaskRequest request, HttpServletRequest httpServletRequest) throws Exception {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> claims = JwtUtils.decode(token);
        return taskService.submitTask(request.getTaskId(), claims.get("preferred_username").toString());
    }

    @PostMapping(value = "/user/task/approve-task", consumes = "application/json")
    public boolean approveTask(@RequestBody TaskRequest request, HttpServletRequest httpServletRequest) throws Exception {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> claims = JwtUtils.decode(token);
        return taskService.approveTask(request.getTaskId(),claims.get("preferred_username").toString() );
    }

    @PostMapping(value = "/user/task/test", consumes = "application/json")
    public Integer taskTest(@RequestBody TaskRequest request) throws Exception {

        return taskDao.getLevelNumberOfAssignedInTask(request.getTaskId(), 2L);
    }

    @PostMapping(value = "/user/task/test1", consumes = "application/json")
    public Map<String, Object> taskTest1(@RequestBody TaskRequest request) throws Exception {

        AccountEntity account =  taskDao.getUserInWorkflowLevel(request.getTaskId(), 2);

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("userId", account.getId());
        result.put("username", account.getUsername());
        result.put("email", account.getEmail());
//        result.put("realm_access", claims.get("realm_access"));
//        result.put("department", account.getDepartment());
        result.put("lastName", account.getLastName() != null ? account.getLastName() : "");
        result.put("firstName", account.getFirstName() != null ? account.getFirstName() : "");
        result.put("phoneNumber", account.getPhoneNumber() != null ? account.getPhoneNumber() : "");
        result.put("avatarUrl", account.getAvatarUrl() != null ? account.getAvatarUrl() : "");
        result.put("avatarFilename", account.getAvatarFilename() != null ? account.getAvatarFilename() : "");
        result.put("createdAt", account.getCreatedAt());
        result.put("id", account.getId());
        return result;
    }


}
