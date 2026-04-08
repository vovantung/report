package txu.report.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.*;
import txu.report.mainapp.dto.request.AccountRequest;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.service.AccountService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/report/admin/account")
@RequiredArgsConstructor
public class AccountApi extends AbstractApi {

    private final AccountService accountService;

    @PostMapping(value = "create-or-update")
    public Account2Dto createOrUpdate(@RequestBody AccountEntity accountEntity) {
        AccountEntity  rs = accountService.createOrUpdate(accountEntity);
        Account2Dto account = new Account2Dto();
        account.setId(rs.getId());
        account.setUsername(rs.getUsername());
        account.setPassword(rs.getPassword());
        account.setEmail(rs.getEmail());
        account.setCreatedAt(rs.getCreatedAt());
        account.setUpdatedAt(rs.getUpdatedAt());
        account.setAvatarUrl(rs.getAvatarUrl());
        account.setAvatarFilename(rs.getAvatarFilename());
        account.setFirstName(rs.getFirstName());
        account.setLastName(rs.getLastName());
        DepartmentDto department = new DepartmentDto();
        department.setId(rs.getDepartment().getId());
        department.setName(rs.getDepartment().getName());
        account.setDepartment(department);
        return account;
    }

    @DeleteMapping(value = "remove")
    public boolean removeByUsername(@RequestBody UsernameRequest request) {
        return accountService.removeByUsername(request.getUsername());
    }

    @PostMapping(value = "get-by-username")
    public Account2Dto getByUsername(@RequestBody UsernameRequest request) {
        return accountService.getByUsername(request.getUsername());
    }

    @PostMapping(value = "/get-paging")
    public List<AccountDto> getPaging(@RequestBody AccountRequest accountRequest) {
        return accountService.getPaging(accountRequest.getKeyOffset(), accountRequest.getLimit(), accountRequest.getKeySearch());
    }
}
