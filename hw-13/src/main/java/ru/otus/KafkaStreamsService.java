package main.java.ru.otus;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class KafkaStreamsService {

    public void run() {

        Logger log = LoggerFactory.getLogger("KafkaStreamsApplication");
        Serde<String> stringSerde = Serdes.String();
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> originalStream = builder.stream("events", Consumed.with(stringSerde, stringSerde));

        var newTable = originalStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
                .count();

        newTable.toStream().print(Printed.<Windowed<String>, Long>toSysOut().withLabel("*** Count for 5 minutes:"));

        originalStream.print(Printed.<String, String>toSysOut().withLabel("From my application"));


        Map<String, Object> streamsConfig = Map.of(
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                StreamsConfig.APPLICATION_ID_CONFIG, "applicationId1"
        );

        log.info("{}", builder.build().describe());

        var kafkaStreams = new KafkaStreams(builder.build(), new StreamsConfig(streamsConfig));

        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                kafkaStreams.close();
                latch.countDown();
            }
        });

        try {
            log.info("Application Started");
            kafkaStreams.start();
            latch.await();
            log.info("Application shutting down");
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
