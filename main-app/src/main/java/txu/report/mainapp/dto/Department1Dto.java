package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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

    public Department1Dto(Integer id, String name, String description, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account1Dto account) {
        this.accounts.add(account);
    }
}
