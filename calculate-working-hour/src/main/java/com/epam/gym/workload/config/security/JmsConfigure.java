package com.epam.gym.workload.config.security;

import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

@Slf4j
@EnableJms
@Configuration
@RequiredArgsConstructor
public class JmsConfigure {


    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String user;

    @Value("${spring.activemq.password}")
    private String password;

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setTypeIdMappings(Map.of(
                "TrainerWorkloadRequest", TrainerWorkloadRequest.class
        ));

        return converter;
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(user, password, brokerUrl);
        factory.getRedeliveryPolicy().setMaximumRedeliveries(3);
        factory.getRedeliveryPolicy().setInitialRedeliveryDelay(1000);
        factory.getRedeliveryPolicy().setBackOffMultiplier(2);
        factory.getRedeliveryPolicy().setUseExponentialBackOff(true);
        CachingConnectionFactory cachingFactory = new CachingConnectionFactory(factory);
        cachingFactory.setSessionCacheSize(50);
        factory.setClientID("workload");
        return cachingFactory;
    }


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jacksonJmsMessageConverter());

        factory.setConcurrency("1-1");
        factory.setSessionTransacted(false);
        factory.setErrorHandler(t ->
                log.error("Error while processing JMS message" + t.getMessage())
        );

        return factory;
    }
}
