package txu.report.mainapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import txu.report.mainapp.dto.TaskDto;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter

//@SqlResultSetMapping(
//        name = "TaskWithVaiTroMapping",
//        entities = @EntityResult(entityClass = TaskEntity.class),
//        columns = {
//                @ColumnResult(name = "vaitro", type = Integer.class)
//        }
//)

@SqlResultSetMapping(
        name = "TaskDtoMapping",
        classes = @ConstructorResult(
                targetClass = TaskDto.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "title", type = String.class),
                        @ColumnResult(name = "description", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                        @ColumnResult(name = "priority", type = String.class),
                        @ColumnResult(name = "deadline", type = Date.class),
                        @ColumnResult(name = "assignee_id", type = Long.class),
                        @ColumnResult(name = "workflow_id", type = Long.class),
                        @ColumnResult(name = "current_level", type = Integer.class),
                        @ColumnResult(name = "created_at", type = Date.class),
                        @ColumnResult(name = "updated_at", type = Date.class),
                        @ColumnResult(name = "vaitro", type = String.class),
                }
        )
)

@Table(name = "TASK")
@Entity
public class TaskEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS")
    private String status;


    @Column(name = "PRIORITY")
    private String priority;


    @Column(name = "DEADLINE")
    private Date deadline;


    @ManyToOne
    @JoinColumn(name = "ASSIGNEE_ID")
//    @JsonIgnore
    private AccountEntity assignee;

    @ManyToOne
    @JoinColumn(name = "CREATED_BY")
    private AccountEntity createBy;

    @ManyToOne
    @JoinColumn(name = "WORKFLOW_ID")
    private WorkFlowEntity workflow;

    @Column(name = "CURRENT_LEVEL")
    private int currentLevel;

    @Getter
    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Getter
    @Column(name = "UPDATED_AT")
    private Date updateAt;
}



