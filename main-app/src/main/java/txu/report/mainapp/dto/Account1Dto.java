package txu.report.mainapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account1Dto {
    private Long id;
    private String firstName;
    private String lastName;
    @JsonIgnore
    private Long departmentId;
    public Account1Dto() {}

    public Account1Dto(Long id, String firstName, String lastName, Long departmentId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.departmentId = departmentId;
    }
}
