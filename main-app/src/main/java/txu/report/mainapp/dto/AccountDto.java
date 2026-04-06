package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AccountDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Date createdAt;
    private Date updatedAt;
    private DepartmentDto department;

    public AccountDto(Long id, String username, String firstName, String lastName, Date createdAt, Date updatedAt, DepartmentDto department) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.department = department;
    }
}
