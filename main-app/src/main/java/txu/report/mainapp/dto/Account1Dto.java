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
    private Integer departmentId;

    public Account1Dto(Long id, String firstName, String lastName, Integer departmentId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.departmentId = departmentId;
    }
}
