package main.java.ru.otus;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class KafkaConsumer {

    public static void consume() {

        Logger log = LoggerFactory.getLogger(KafkaConsumer.class);


        Map<String, Object> consumerConfigurationParametersMap = Map.of(
                ConsumerConfig.GROUP_ID_CONFIG, "cool_group",
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed",
                ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 2000
        );


        org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(consumerConfigurationParametersMap);

        consumer.subscribe(Arrays.asList("topic1", "topic2"));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records)
                    log.info("Получено сообщение: {} Key: {} ", record.value(), record.key());
            }
        } finally {
            consumer.close();
        }

    }

}
