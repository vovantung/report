package txu.report.mainapp.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.TaskDto;
import txu.report.mainapp.dto.TaskExtend;
import txu.report.mainapp.dto.TaskRequest;
import txu.report.mainapp.service.TaskService;
import txu.report.mainapp.util.JwtUtils;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class TaskApi extends AbstractApi {

    private final TaskService taskService;

//    @PostMapping(value = "/get-by-id", consumes = "application/json")
//    public List<TaskExtend> getById(@RequestBody TaskRequest request) {
//        return taskService.getByAssigned(request.getAssigneeId());
//    }

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
        return taskService.getById(request.getTaskId(), claims.get("preferred_username").toString());
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
}
