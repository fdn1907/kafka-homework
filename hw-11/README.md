# kafka-homework

## Текст задания
Описание/Пошаговая инструкция выполнения домашнего задания:

    Описание/Пошаговая инструкция выполнения домашнего задания:
        Запустить Kafka
        Создать два топика: topic1 и topic2
        Разработать приложение, которое:
        открывает транзакцию
        отправляет по 5 сообщений в каждый топик
        подтверждает транзакцию
        открывает другую транзакцию
        отправляет по 2 сообщения в каждый топик
        отменяет транзакцию
        Разработать приложение, которое будет читать сообщения из топиков topic1 и topic2 так, чтобы сообщения из подтверждённой транзакции были выведены, а из неподтверждённой - нет.


## Домашнее задание к уроку 11

Запускаем 1 экземпляр kafka на порту 9092 и для контроля kafdrop на порту 9005
Создаем 2 топика

![img1.png](img1.png)


Создаем продюсер в отдельном классе KafkaProducer, в настройках для транзакций добавляем `TRANSACTIONAL_ID_CONFIG`

```java
        Map<String, Object> producerConfig = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactionId"
        );
```

Отправляем сообщения в оба топика и коммитим их.
Далее отправляем сообщения в оба топика и отказываем транзакцию.
В итоге видим только сообщения, которые прошли с коммитом, а те, которые были откачены в транзакции - не видим.
topic-1
![img2.png](img2.png)
topic-2
![img3.png](img3.png)
