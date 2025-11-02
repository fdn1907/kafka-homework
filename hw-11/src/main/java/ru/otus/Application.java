package main.java.ru.otus;

public class Application {

    public static void main(String[] args) throws InterruptedException {
        KafkaProducer.main(args);
        KafkaConsumer.consume();
    }
}