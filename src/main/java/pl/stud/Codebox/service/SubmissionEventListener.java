package pl.stud.Codebox.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.stud.Codebox.dto.SubmissionCreatedEvent;

@Component
@RequiredArgsConstructor
public class SubmissionEventListener {
    private final Sender sender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSubmissionCreated(SubmissionCreatedEvent event) {
        sender.send(event.submissionId(), event.language());
    }
}
