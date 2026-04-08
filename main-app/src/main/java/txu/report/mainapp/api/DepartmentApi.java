package txu.report.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.Account1Dto;
import txu.report.mainapp.dto.Department1Dto;
import txu.report.mainapp.dto.IdRequest;
import txu.report.mainapp.dto.request.DepartmentRequest;
import txu.report.mainapp.entity.DepartmentEntity;
import txu.report.mainapp.service.DepartmentService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/report/admin/department")
@RequiredArgsConstructor
public class DepartmentApi extends AbstractApi {

    private final DepartmentService departmentService;

    @PostMapping(value = "create-or-update")
    public Department1Dto createOrUpdate(@RequestBody DepartmentEntity department){
        DepartmentEntity result = departmentService.createOrUpdate(department);
        Department1Dto department1Dto = new Department1Dto();
        List< Account1Dto> account1DtoList = new ArrayList<>();
        result.getAccounts().forEach(account1Dto -> {
            Account1Dto account1Dto1 = new Account1Dto();
            account1Dto1.setId(account1Dto.getId());
            account1Dto1.setFirstName(account1Dto.getFirstName());
            account1Dto1.setLastName(account1Dto.getLastName());
            account1DtoList.add(account1Dto1);
        });
        department1Dto.setAccounts(account1DtoList);
        department1Dto.setId(result.getId());
        department1Dto.setName(result.getName());
        department1Dto.setDescription(result.getDescription());
        department1Dto.setCreatedAt(result.getCreatedAt());
        department1Dto.setUpdatedAt(result.getUpdatedAt());
        return department1Dto;
    }

    @PostMapping(value = "get-by-id")
//    @Cacheable(value = "department", key = "#request.id")
    public Department1Dto getById(@RequestBody IdRequest request){
        DepartmentEntity result = departmentService.getById(request.getId());
        Department1Dto department1Dto = new Department1Dto();
        List< Account1Dto> account1DtoList = new ArrayList<>();
        result.getAccounts().forEach(account1Dto -> {
            Account1Dto account1Dto1 = new Account1Dto();
            account1Dto1.setId(account1Dto.getId());
            account1Dto1.setFirstName(account1Dto.getFirstName());
            account1Dto1.setLastName(account1Dto.getLastName());
            account1DtoList.add(account1Dto1);
        });
        department1Dto.setAccounts(account1DtoList);
        department1Dto.setId(result.getId());
        department1Dto.setName(result.getName());
        department1Dto.setDescription(result.getDescription());
        department1Dto.setCreatedAt(result.getCreatedAt());
        department1Dto.setUpdatedAt(result.getUpdatedAt());
        return department1Dto;
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
