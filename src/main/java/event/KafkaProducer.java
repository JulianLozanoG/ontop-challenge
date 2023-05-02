package event;

import model.transaction.TransactionEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ListenableFuture<SendResult<String, TransactionEvent>> sendMessage(String topic, String key, TransactionEvent event) {
        ProducerRecord<String, TransactionEvent> record = new ProducerRecord<>(topic, key, event);
        ListenableFuture<SendResult<String, TransactionEvent>> future = (ListenableFuture<SendResult<String, TransactionEvent>>) kafkaTemplate.send(record);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, TransactionEvent> result) {
                System.out.println("Sent message=[{}] with offset=[{}]" + result.getProducerRecord().value() + result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=[{}] due to : {}" + event + ex.getMessage());
            }
        });

        return future;
    }
}