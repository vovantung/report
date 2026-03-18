package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DeleteUserCommand implements Serializable {
    private String sagaId;
    private String userId;
}

