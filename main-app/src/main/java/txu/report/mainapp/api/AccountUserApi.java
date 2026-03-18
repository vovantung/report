package txu.report.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.LinkDto;
import txu.report.mainapp.dto.LinkRequest;
import txu.report.mainapp.dto.UpdateAvatarRequest;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.service.AccountUserService;
//import txu.user.mainapp.base.AbstractApi;
//import txu.user.mainapp.dto.LinkDto;
//import txu.user.mainapp.dto.LinkRequest;
//import txu.user.mainapp.dto.UpdateAvatarRequest;
//import txu.user.mainapp.entity.AccountEntity;
//import txu.user.mainapp.service.AccountService;

@Slf4j
@RestController
@RequestMapping("/report/user/account")
@RequiredArgsConstructor
public class AccountUserApi extends AbstractApi {

    private final AccountUserService accountUserService;

    @PostMapping("/update-avatar")
    public AccountEntity updateAvatar(@RequestBody UpdateAvatarRequest request) {
        return accountUserService.updateAvatar(request.getFilename(), request.getUsername(), request.getPassword(),
                request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhoneNumber());
    }

    @PostMapping("/get-presignedurl-for-put")
    public LinkDto getPreSignedUrlForPut(@RequestBody LinkRequest request) {
        LinkDto linkDto = new LinkDto();
        try {
            return accountUserService.getPreSignedUrlForPut(request.getFilename());
        } catch (Exception e) {

        }
        return linkDto;
    }
}
