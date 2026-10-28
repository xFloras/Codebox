package pl.stud.Codebox.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

@Configurable
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "submissions";
    public static final String DLX_NAME = "submissions.dlx";


    @Bean
    DirectExchange submissionsExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }
    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_NAME);
    }

    @Bean
    Queue javaQueue() {
        return QueueBuilder.durable("submissions.java")
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", "java")
                .build();
    }

    @Bean
    Queue javaDlq() {
        return QueueBuilder.durable("submissions.java.dlq")
                .build();
    }

    @Bean
    Binding javaBinding() {
        return BindingBuilder.bind(javaQueue()).to(submissionsExchange()).with("java");
    }
    @Bean
    Binding javaDlqBinding() {
        return BindingBuilder.bind(javaDlq()).to(deadLetterExchange()).with("java");
    }

    @Bean
    MessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter();
    }



}
