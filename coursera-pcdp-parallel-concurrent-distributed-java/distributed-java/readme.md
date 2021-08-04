## week 2

### protobuf (IDL - interface definition language)

https://www.baeldung.com/google-protocol-buffer

https://developers.google.com/protocol-buffers/docs/javatutorial

first install protoc

```bash
brew install protobuf
```

```bash
➜  protobuf git:(master) ✗ protoc -I=. --java_out=. addressbook.proto                
[libprotobuf WARNING google/protobuf/compiler/parser.cc:651] No syntax specified for the proto file: addressbook.proto. Please use 'syntax = "proto2";' or 'syntax = "proto3";' to specify a syntax version. (Defaulted to proto2 syntax.)
```

### kafka

## Spring Kafka

This module contains articles about Spring with Kafka

### Relevant articles

- [Intro to Apache Kafka with Spring](https://www.baeldung.com/spring-kafka)
- [Testing Kafka and Spring Boot](https://www.baeldung.com/spring-boot-kafka-testing)
- [Monitor the Consumer Lag in Apache Kafka](https://www.baeldung.com/java-kafka-consumer-lag)
- [Send Large Messages With Kafka](https://www.baeldung.com/java-kafka-send-large-message)
- [Confluent Docker Running Kafka](https://kafka-tutorials.confluent.io/creating-first-apache-kafka-producer-application/kafka.html)