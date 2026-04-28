package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
public class DepartmentDto {
    Integer id;
    String name;

    public DepartmentDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public DepartmentDto() {

    }
}
