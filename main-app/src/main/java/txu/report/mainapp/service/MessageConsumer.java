package txu.report.mainapp.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import txu.report.mainapp.dto.DeleteUserCommand;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.DepartmentEntity;
import txu.common.saga.contract.command.CreateHRUserCommand;
import txu.common.saga.contract.command.CreateKeycloakUserCommand;
import txu.common.saga.contract.command.SagaReplyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class MessageConsumer {
    private final JmsTemplate jmsTemplate;
    private final KeycloakService keycloakService;
    private final AccountService accountService;

    @JmsListener(destination = "keycloak.create.user.queue")
    public void createKeycloakUser(CreateKeycloakUserCommand cmd) {

        try {
            String keycloakUserId = keycloakService.createKeycloakUser(cmd.getUsername(), cmd.getEmail(), cmd.getLastName(), cmd.getFirstName());
            List<String> roles = new ArrayList<>();
            roles.add("be-admin");
            keycloakService.assignRealmRolesToUser(keycloakUserId,roles);

            log.info("Tạo KeycloakUser thành công");

            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("KEYCLOAK_CREATE");
            event.setSuccess(true);
            event.setPayload(
                    Map.of("keycloakUserId", keycloakUserId, "lastName", cmd.getLastName(), "firstName", cmd.getFirstName(), "email", cmd.getEmail(),"username", cmd.getUsername())
            );

            jmsTemplate.convertAndSend("saga.reply.queue", event, message -> {
                message.setStringProperty("_type", SagaReplyEvent.class.getName());
                return message;
            });

        } catch (Exception ex) {
            log.info("Xảy ra lỗi khi tạo KeycloakUser");
            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("KEYCLOAK_CREATE");
            event.setSuccess(false);
            event.setError(ex.getMessage());

            jmsTemplate.convertAndSend("saga.reply.queue", event, message -> {
                message.setStringProperty("_type", SagaReplyEvent.class.getName());
                return message;
            });

        }
    }



    @JmsListener(destination = "hr.create.user.queue")
    public void createHRUser(CreateHRUserCommand cmd) {
        try {
            log.info("Thông tin trước khi tạo HR User: user " + cmd.getUsername() + ", email " + cmd.getEmail());
//            hrUserRepository.createUser(cmd.getKeycloakUserId());
            AccountEntity accountEntity = new AccountEntity();

            accountEntity.setFirstName(cmd.getFirstName());
            accountEntity.setLastName(cmd.getLastName());
            accountEntity.setEmail(cmd.getEmail());
            accountEntity.setUsername(cmd.getUsername());
            accountEntity.setPassword("123");
            DepartmentEntity departmentEntity = new DepartmentEntity();
            departmentEntity.setId(1L);

            accountEntity.setDepartment(departmentEntity);

            accountService.createOrUpdate(accountEntity);
            log.info("Tạo HRUser thành công");
            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("HR_CREATE");
            event.setSuccess(true);

            jmsTemplate.convertAndSend("saga.reply.queue", event, message -> {
                message.setStringProperty("_type", SagaReplyEvent.class.getName());
                return message;
            });

        } catch (Exception ex) {
            log.info("Xảy ra lỗi khi tạo HR User");
            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("HR_CREATE");
            event.setSuccess(false);
            event.setError(ex.getMessage());
            jmsTemplate.convertAndSend("saga.reply.queue", event, message -> {
                message.setStringProperty("_type", SagaReplyEvent.class.getName());
                return message;
            });
        }
    }

    @JmsListener(destination = "keycloak.delete.user.queue")
    public void handleDeleteUser(DeleteUserCommand cmd) {
//        keycloakService.deleteUser(cmd.getUserId());
//        sendSuccess(cmd.getSagaId(), "KEYCLOAK_DELETE");
    }

}

