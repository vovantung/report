package txu.report.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.Department1Dto;
import txu.report.mainapp.dto.IdRequest;
import txu.report.mainapp.dto.LimitRequest;
import txu.report.mainapp.dto.request.DepartmentRequest;
import txu.report.mainapp.entity.DepartmentEntity;
import txu.report.mainapp.service.DepartmentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/report/admin/department")
@RequiredArgsConstructor
public class DepartmentApi extends AbstractApi {

    private final DepartmentService departmentService;

    @PostMapping(value = "create-or-update")
    public DepartmentEntity createOrUpdate(@RequestBody DepartmentEntity department){
        return departmentService.createOrUpdate(department);
    }

    @PostMapping(value = "get-by-id")
//    @Cacheable(value = "department", key = "#request.id")
    public DepartmentEntity getById(@RequestBody IdRequest request){
        return  departmentService.getById(request.getId());
    }

    @DeleteMapping(value = "remove")
    public boolean removeById(@RequestBody IdRequest request){
        return departmentService.removeById(request.getId());
    }

    @PostMapping(value = "/get-paging")
    public List<Department1Dto> getPaging(@RequestBody DepartmentRequest departmentRequest) {
        return departmentService.getPaging(departmentRequest.getKeyOffset(), departmentRequest.getLimit(), departmentRequest.getKeySearch());
    }

}
