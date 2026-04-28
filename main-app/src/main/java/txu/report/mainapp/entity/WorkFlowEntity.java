package txu.report.mainapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Table(name = "WORKFLOW_CONFIG")
@Entity
public class WorkFlowEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TYPE")
    private String type;

    @Getter
    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Getter
    @Column(name = "UPDATED_AT")
    private Date updateAt;
}



