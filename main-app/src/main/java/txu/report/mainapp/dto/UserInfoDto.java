package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;
import txu.report.mainapp.entity.AccountEntity;

@Getter
@Setter
public class UserInfoDto {
    private String role;
    private AccountEntity account;
}
