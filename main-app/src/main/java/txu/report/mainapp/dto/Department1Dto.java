package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Department1Dto {
    private Integer id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private List<Account1Dto> accounts;
    public Department1Dto(){}

}
