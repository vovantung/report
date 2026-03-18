package txu.report.mainapp.service;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class MessageProducer {

    private final JmsTemplate jmsTemplate;

    public void send(String msg) {
        jmsTemplate.convertAndSend("test.queue", msg);
    }

}
