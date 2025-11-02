package main.java.ru.otus;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KafkaProducer {
    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger(KafkaProducer.class);


        Map<String, Object> producerConfigParametersMap = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactionId"
        );

        try {
            try (var kafkaProducer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(producerConfigParametersMap)) {

                kafkaProducer.initTransactions();
                kafkaProducer.beginTransaction();

                for (int i = 0; i < 5; i++) {

                    log.info("отправка сообшения id {} в topic1", i);
                    kafkaProducer.send(new ProducerRecord<>("topic1", "topic1-всем привет" + i));
                    kafkaProducer.send(new ProducerRecord<>("topic2", "topic2-всем привет" + i));

                }

                kafkaProducer.commitTransaction();
                log.info("коммитим транзакцию");

            }
            catch (RuntimeException e){
                throw new RuntimeException(e);
            }

            try (var producer2 = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(producerConfigParametersMap)) {

                producer2.initTransactions();
                producer2.beginTransaction();

                for (int i = 0; i < 2; i++) {

                    log.info("Отсылаем сообщение id {} в топики и далее откатим их", i);
                    producer2.send(new ProducerRecord<>("topic1", "topic1 message-откатим транзакцию" + i));
                    producer2.send(new ProducerRecord<>("topic2", "topic2 message-откатим транзакцию" + i));
                }


                producer2.flush();

                producer2.abortTransaction();
                log.info("send и далее abort abortTransaction");

            }

        } catch (ProducerFencedException e) {
            throw new RuntimeException(e);
        }
    }

}
