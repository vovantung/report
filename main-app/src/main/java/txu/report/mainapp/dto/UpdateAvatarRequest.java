package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAvatarRequest {
    String filename;
    String username;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String password;
}
