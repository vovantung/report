package txu.report.mainapp.mq;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.DepartmentEntity;
import txu.common.saga.contract.command.CreateHRUserCommand;
import txu.common.saga.contract.command.SagaReplyEvent;
import txu.report.mainapp.service.AccountService;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class MessageConsumer {

    private final JmsTemplate jmsTemplate;
    private final AccountService accountService;

    @JmsListener(destination = "hr.create.user.queue")
    public void createHRUser(CreateHRUserCommand cmd) {
        try {
            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setFirstName(cmd.getFirstName());
            accountEntity.setLastName(cmd.getLastName());
            accountEntity.setEmail(cmd.getEmail());
            accountEntity.setUsername(cmd.getUsername());
            accountEntity.setPassword("123");
            DepartmentEntity departmentEntity = new DepartmentEntity();
            departmentEntity.setId(cmd.getDepartmentId());
            accountEntity.setDepartment(departmentEntity);
            accountService.createOrUpdate(accountEntity);
            log.info("Tạo hr user thành công!, sagaId: {}", cmd.getSagaId());
            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("HR_CREATE");
            event.setSuccess(true);
            event.setPayload(Map.of("sagaId", cmd.getSagaId()));
            jmsTemplate.convertAndSend("saga.reply.queue", event, message -> {
                message.setStringProperty("_type", SagaReplyEvent.class.getName());
                return message;
            });
        } catch (Exception ex) {
            log.info("Lỗi khi tạo hr user: " + ex.getMessage());
            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("HR_CREATE");
            event.setSuccess(false);
            event.setError(ex.getMessage());
            event.setPayload(Map.of("sagaId", cmd.getSagaId(), "keycloakUserId", cmd.getKeycloakUserId())
            );
            jmsTemplate.convertAndSend("saga.reply.queue", event, message -> {
                message.setStringProperty("_type", SagaReplyEvent.class.getName());
                return message;
            });
        }
    }
}

