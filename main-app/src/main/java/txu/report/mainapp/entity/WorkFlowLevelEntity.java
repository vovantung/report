package txu.report.mainapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Table(name = "WORKFLOW_LEVEL")
@Entity
public class WorkFlowLevelEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "WORKFLOW_ID")
//    @JsonIgnore
    private WorkFlowEntity workflow;


    @Column(name = "LEVEL_NUMBER")
     private int  levelNumber;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
//    @JsonIgnore
    private AccountEntity  user;


    @Getter
    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Getter
    @Column(name = "UPDATED_AT")
    private Date updateAt;
}



