package txu.report.mainapp.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import txu.common.exception.NotFoundException;
import txu.common.exception.TxException;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.*;
import txu.report.mainapp.dto.request.AccountRequest;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.service.AccountService;
import txu.report.mainapp.util.JwtUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class AccountApi extends AbstractApi {

    private final AccountService accountService;

    @PostMapping(value = "/current-user")
    public Map<String, Object> currentUser(HttpServletRequest request) throws Exception {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> claims = JwtUtils.decode(token);
        AccountEntity account = accountService.getByUsername(claims.get("preferred_username").toString());
        return Map.of(
                "username", claims.get("preferred_username"),
                "email", claims.get("email"),
                "realm_access", claims.get("realm_access"),
                "department", account.getDepartment(),
                "lastName", account.getLastName() != null ? account.getLastName() : "",
                "firstName", account.getFirstName() != null ? account.getFirstName() : "",
                "phoneNumber", account.getPhoneNumber() != null ? account.getPhoneNumber() : "",
                "avatarUrl", account.getAvatarUrl() != null ? account.getAvatarUrl() : "",
                "avatarFilename", account.getAvatarFilename() != null ? account.getAvatarFilename() : "",
                "createdAt", account.getCreatedAt()
        );
    }

    // Admin
//    @PostMapping(value = "/admin/account/create-or-update")
//    public Account2Dto createOrUpdate(@RequestBody AccountEntity accountEntity) throws NoSuchMethodException {
//        AccountEntity  rs = accountService.createOrUpdate(accountEntity);
//        Account2Dto account = new Account2Dto();
//        account.setId(rs.getId());
//        account.setUsername(rs.getUsername());
//        account.setPassword(rs.getPassword());
//        account.setEmail(rs.getEmail());
//        account.setCreatedAt(rs.getCreatedAt());
//        account.setUpdatedAt(rs.getUpdatedAt());
//        account.setAvatarUrl(rs.getAvatarUrl());
//        account.setAvatarFilename(rs.getAvatarFilename());
//        account.setFirstName(rs.getFirstName());
//        account.setLastName(rs.getLastName());
//        DepartmentDto department = new DepartmentDto();
//        department.setId(rs.getDepartment().getId());
//        department.setName(rs.getDepartment().getName());
//        account.setDepartment(department);
//        return account;
//    }

    @DeleteMapping(value = "/admin/account/remove")
    public boolean removeByUsername(@RequestBody UsernameRequest request) {
        return accountService.removeByUsername(request.getUsername());
    }

    @PostMapping(value = "/admin/account/get-by-username")
    public Map<String, Object> getByUsername(@RequestBody UsernameRequest request) {
        AccountEntity account = accountService.getByUsername(request.getUsername());
        return Map.of(
                "id", account.getId(),
                "username", account.getUsername()!= null? account.getUsername():"",
                "email", account.getEmail() != null ? account.getEmail():"",
                "department", account.getDepartment(),
                "lastName", account.getLastName() != null ? account.getLastName() : "",
                "firstName", account.getFirstName() != null ? account.getFirstName() : "",
                "phoneNumber", account.getPhoneNumber() != null ? account.getPhoneNumber() : "",
                "avatarUrl", account.getAvatarUrl() != null ? account.getAvatarUrl() : "",
                "avatarFilename", account.getAvatarFilename() != null ? account.getAvatarFilename() : "",
                "createdAt", account.getCreatedAt()
        );
    }

    @PostMapping(value = "/admin/account/get-paging")
    public List<AccountDto> getPaging(@RequestBody AccountRequest accountRequest) {
//        throw new NotFoundException("abcd");
        return accountService.getPaging(accountRequest.getKeyOffset(), accountRequest.getLimit(), accountRequest.getKeySearch());
    }

    // User
    @PostMapping("/user/account/update-avatar")
    public Account2Dto updateAvatar(@RequestBody UpdateAvatarRequest request) throws NoSuchMethodException {
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

    @PostMapping("/user/account/get-presignedurl-for-put")
    public LinkDto getPreSignedUrlForPut(@RequestBody LinkRequest request) {
        LinkDto linkDto = new LinkDto();
        try {
            return accountService.getPreSignedUrlForPut(request.getFilename());
        } catch (Exception e) {

        }
        return linkDto;
    }
}
