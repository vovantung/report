package txu.report.mainapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Setter
//@Getter
@Table(name = "ROLE")
public class RoleEntity implements Serializable {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Getter
    @Column(name = "NAME")
    private String name;

    @Getter
    @Column(name = "CREATED_AT")
    private Date createdAt;
//    public String getCreatedAt() {
//        return createdAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy]"));
//    }

    @Getter
    @Column(name = "UPDATED_AT")
    private Date updatedAt;
//    public String getUpdateAt() {
//        return updateAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy]"));
//    }

}
