package txu.report.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.IdRequest;
import txu.report.mainapp.dto.LimitRequest;
import txu.report.mainapp.entity.RoleEntity;
import txu.report.mainapp.service.RoleService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/report/admin/role")
@RequiredArgsConstructor
public class RoleApi extends AbstractApi {

    private final RoleService roleService;

    @PostMapping(value = "create-or-update")
    public RoleEntity createOrUpdate(@RequestBody RoleEntity role){
        return roleService.createOrUpdate(role);
    }

    @PostMapping(value = "get-limit")
    public List<RoleEntity> getLimit(@RequestBody LimitRequest request){
        return roleService.getWithLimit(request.getLimit());
    }

    @PostMapping(value = "get-by-id")
    public RoleEntity getById(@RequestBody IdRequest request){
        return  roleService.getById(request.getId());
    }

    @DeleteMapping(value = "remove")
    public boolean removeById(@RequestBody IdRequest request){
        return roleService.removeById(request.getId());
    }

}
