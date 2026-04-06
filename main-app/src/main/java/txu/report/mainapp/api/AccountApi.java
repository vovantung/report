package txu.report.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.AccountDto;
import txu.report.mainapp.dto.DepartmentDto;
import txu.report.mainapp.dto.LimitRequest;
import txu.report.mainapp.dto.UsernameRequest;
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
    public AccountEntity createOrUpdate(@RequestBody AccountEntity accountEntity) {
        return accountService.createOrUpdate(accountEntity);
    }

    @PostMapping(value = "get-limit")
    public List<AccountEntity> getLimit(@RequestBody LimitRequest request) {
        return accountService.getWithLimit(request.getLimit());
    }

    @DeleteMapping(value = "remove")
    public boolean removeByUsername(@RequestBody UsernameRequest request) {
        return accountService.removeByUsername(request.getUsername());
    }

    @PostMapping(value = "get-by-username")
    public AccountEntity getByUsername(@RequestBody UsernameRequest request) {
        return accountService.getByUsername(request.getUsername());
    }

    @PostMapping(value = "/get-paging")
    public List<AccountDto> getPaging(@RequestBody AccountRequest accountRequest) {
        return accountService.getPaging(accountRequest.getKeyOffset(), accountRequest.getLimit(), accountRequest.getKeySearch());
    }
}
