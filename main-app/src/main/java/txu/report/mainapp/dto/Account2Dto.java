package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;
import txu.report.mainapp.entity.DepartmentEntity;
import java.util.Date;

@Getter
@Setter
public class Account2Dto {
    private Long id;
    private String username;
    private String password;
    private String lastName;
    private String firstName;
    private String phoneNumber;
    private String avatarUrl;
    private String avatarFilename;
    private String email;
    private DepartmentDto department;
    private Date createdAt;
    private Date updatedAt;
}
