package com.epam.gym.workload.config.security;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JmsConfigureTest {

    private JmsConfigure jmsConfigure;

    @BeforeEach
    void setUp() {

        jmsConfigure = new JmsConfigure();

        ReflectionTestUtils.setField(
                jmsConfigure,
                "brokerUrl",
                "tcp://localhost:61616"
        );

        ReflectionTestUtils.setField(
                jmsConfigure,
                "user",
                "admin"
        );

        ReflectionTestUtils.setField(
                jmsConfigure,
                "password",
                "admin"
        );
    }

    @Test
    void jacksonJmsMessageConverter_shouldReturnConfiguredConverter() {

        MessageConverter converter =
                jmsConfigure.jacksonJmsMessageConverter();

        assertNotNull(converter);

        assertInstanceOf(
                JacksonJsonMessageConverter.class,
                converter
        );

        JacksonJsonMessageConverter jsonConverter =
                (JacksonJsonMessageConverter) converter;

        MessageType targetType =
                (MessageType) ReflectionTestUtils.getField(
                        jsonConverter,
                        "targetType"
                );

        String typeIdPropertyName =
                (String) ReflectionTestUtils.getField(
                        jsonConverter,
                        "typeIdPropertyName"
                );

        assertEquals(
                MessageType.TEXT,
                targetType
        );

        assertEquals(
                "_type",
                typeIdPropertyName
        );
    }

    @Test
    void connectionFactory_shouldReturnConfiguredCachingConnectionFactory() {

        CachingConnectionFactory cachingFactory =
                jmsConfigure.connectionFactory();

        assertNotNull(cachingFactory);

        assertEquals(
                50,
                cachingFactory.getSessionCacheSize()
        );

        assertNotNull(cachingFactory.getTargetConnectionFactory());

        assertInstanceOf(
                ActiveMQConnectionFactory.class,
                cachingFactory.getTargetConnectionFactory()
        );

        ActiveMQConnectionFactory activeMQConnectionFactory =
                (ActiveMQConnectionFactory)
                        cachingFactory.getTargetConnectionFactory();

        assertEquals(
                "tcp://localhost:61616",
                activeMQConnectionFactory.getBrokerURL()
        );

        assertEquals(
                "workload",
                activeMQConnectionFactory.getClientID()
        );

        assertEquals(
                3,
                activeMQConnectionFactory
                        .getRedeliveryPolicy()
                        .getMaximumRedeliveries()
        );

        assertEquals(
                1000,
                activeMQConnectionFactory
                        .getRedeliveryPolicy()
                        .getInitialRedeliveryDelay()
        );

        assertEquals(
                2.0,
                activeMQConnectionFactory
                        .getRedeliveryPolicy()
                        .getBackOffMultiplier()
        );

        assertTrue(
                activeMQConnectionFactory
                        .getRedeliveryPolicy()
                        .isUseExponentialBackOff()
        );
    }

    @Test
    void jmsListenerContainerFactory_shouldReturnConfiguredFactory() {

        DefaultJmsListenerContainerFactory factory =
                jmsConfigure.jmsListenerContainerFactory();

        assertNotNull(factory);

        assertNotNull(
                ReflectionTestUtils.getField(
                        factory,
                        "connectionFactory"
                )
        );

        assertNotNull(
                ReflectionTestUtils.getField(
                        factory,
                        "messageConverter"
                )
        );

        assertEquals(
                "1-1",
                ReflectionTestUtils.getField(
                        factory,
                        "concurrency"
                )
        );
    }
}