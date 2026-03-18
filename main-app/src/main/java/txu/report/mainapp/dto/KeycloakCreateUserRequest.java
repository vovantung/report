package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeycloakCreateUserRequest {
    private String username;
    private Boolean enabled;
    private String email;
    private String firstName;
    private String lastName;
}
