package pl.stud.Codebox.service;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.stud.Codebox.config.RabbitConfig;
import pl.stud.Codebox.dto.SubmissionMessage;
import pl.stud.Codebox.models.Language;

@Component
@RequiredArgsConstructor
public class Sender {

    private final RabbitTemplate rabbitTemplate;

    public void send(Long submissionId, Language language) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, language.toString().toLowerCase(), new SubmissionMessage(submissionId));
    }
}
