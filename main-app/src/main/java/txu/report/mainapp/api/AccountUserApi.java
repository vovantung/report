package txu.report.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.*;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.service.AccountService;

@Slf4j
@RestController
@RequestMapping("/report/user/account")
@RequiredArgsConstructor
public class AccountUserApi extends AbstractApi {

    private final AccountService accountService;

    @PostMapping("/update-avatar")
    public Account2Dto updateAvatar(@RequestBody UpdateAvatarRequest request) {
        AccountEntity rs = accountService.updateAvatar(request.getFilename(), request.getUsername(), request.getPassword(),
                request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhoneNumber());
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

    @PostMapping("/get-presignedurl-for-put")
    public LinkDto getPreSignedUrlForPut(@RequestBody LinkRequest request) {
        LinkDto linkDto = new LinkDto();
        try {
            return accountService.getPreSignedUrlForPut(request.getFilename());
        } catch (Exception e) {

        }
        return linkDto;
    }
}
