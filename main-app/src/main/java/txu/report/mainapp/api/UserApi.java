package txu.report.mainapp.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.service.AccountService;
import txu.report.mainapp.util.JwtUtils;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class UserApi extends AbstractApi {

    private final AccountService accountService;

    @PostMapping(value = "current-user")
//    public AccountEntity me(HttpServletRequest request) throws Exception {
    public Map<String, Object> me(HttpServletRequest request) throws Exception {

        String authHeader = request.getHeader("Authorization");

        String token = authHeader.replace("Bearer ", "");

        Map<String, Object> claims = JwtUtils.decode(token);

        AccountEntity account = accountService.getByUsername(claims.get("preferred_username").toString());

        return Map.of(
                "username", claims.get("preferred_username"),
                "email", claims.get("email"),
                "realm_access", claims.get("realm_access"),
                "department", account.getDepartment(),
                "lastName", account.getLastName(),
                "firstName", account.getFirstName(),
                "phoneNumber", account.getPhoneNumber(),
                "avatarUrl", account.getAvatarUrl() != null ? account.getAvatarUrl() : "",
                "avatarFilename", account.getAvatarFilename() != null ? account.getAvatarFilename() : "",
                "createdAt", account.getCreatedAt()
        );
    }


    @GetMapping(value = "/user/test")
    public String test() {
        return "This API allows calls from users with both administrator and regular user roles.";
    }


}
