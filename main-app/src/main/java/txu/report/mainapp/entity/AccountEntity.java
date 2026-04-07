package txu.report.mainapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "ACCOUNT")
public class AccountEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Getter
    @Column(name = "USERNAME")
    private String username;

    @Getter
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "LASTNAME")
    @Getter
    private String lastName;

    @Column(name = "FIRSTNAME")
    @Getter
    private String firstName;

    @Column(name = "PHONENUMBER")
    @Getter
    private String phoneNumber;

    @Column(name = "AVATAR_URL")
    @Getter
    private String avatarUrl;

    @Column(name = "AVATAR_FILENAME")
    @Getter
    private String avatarFilename;

    @Column(name = "EMAIL")
    @Getter
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    @JsonIgnore
    private DepartmentEntity department;


    @Getter
    @Column(name = "CREATED_AT")
    private Date createdAt;
//    public String getCreatedAt() {
////        return createdAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy] HH:mm:ss"));
//        return createdAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy]"));
//    }

    @Getter
    @Column(name = "UPDATED_AT")
    private Date updatedAt;
//    public String getUpdateAt() {
////        return updateAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy] HH:mm:ss"));
//        return updateAt.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("[dd/MM/yyyy]"));
//    }

}
