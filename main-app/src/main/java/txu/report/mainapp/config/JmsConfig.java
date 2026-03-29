package txu.report.mainapp.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;

@Configuration
@EnableJms
public class JmsConfig {

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.1.99:61616");
        factory.setUserName("admin");
        factory.setPassword("Phan@123");
        return factory;
    }
//    @Bean
//    public JmsTemplate jmsTemplate(ConnectionFactory cf) {
//        return new JmsTemplate(cf);
//    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MappingJackson2MessageConverter converter) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
