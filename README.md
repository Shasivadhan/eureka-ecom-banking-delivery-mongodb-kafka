E-Commerce → Delivery (Kafka) — Microservices Demo

Event-driven checkout: when a user purchases in E-Commerce, a DeliveryEvent is published to Kafka. The Delivery service consumes the event and writes a Delivery record to MongoDB. Optional Bank service simulates payment debit. Eureka (optional) for service discovery.

Stack: Spring Boot, Spring Cloud OpenFeign, Spring Kafka, Spring Data MongoDB, Kafka, ZooKeeper (legacy), MongoDB.

🔭 Architecture
flowchart LR
  A[Client / Swagger / Postman] -->|Purchase| B(E-Commerce Service:8080)
  B -->|Feign Debit| C(Bank Service:8082)
  B -->|Kafka publish DeliveryEvent| D[(Kafka:9092)]
  D -->|@KafkaListener| E(Delivery Service:8083)
  B -->|MongoDB (Orders)| M1[(Mongo ecomdb)]
  E -->|MongoDB (Deliveries)| M2[(Mongo deliverydb)]
  F(Eureka:8761) --- B
  F --- C
  F --- E


Topic: delivery-requests
Consumer Group: delivery-service-group
Shared DTO: com.ecommerce.kafka.DeliveryEvent (same package in both services)

🧩 Services

Eureka Server (optional): http://localhost:8761

Bank Service: http://localhost:8082/banking

E-Commerce Service (Producer): http://localhost:8080

Delivery Service (Consumer): http://localhost:8083

Kafka: localhost:9092 (ZooKeeper localhost:2181 if using ZK mode)

MongoDB: localhost:27017

ecomdb → orders, order_items

deliverydb → delivery

✅ Version Compatibility

Java: 21+

Spring Boot: 3.2.x or 3.3.x (stay in this range for Spring Cloud compatibility)

Spring Cloud: matching Release Train for your Boot version

Kafka: 3.x

MongoDB Java Driver: from spring-boot-starter-data-mongodb

If you see Spring Cloud compatibility-verifier errors, align Boot/Cloud versions accordingly.

📦 Dependencies (POM)
Common (per service)
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka</artifactId>
</dependency>

E-Commerce (adds Feign)
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<!-- optional if you use discovery -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>


In the E-Commerce main application class: @EnableFeignClients(basePackages = "com.ecommerce.feign")

⚙️ Configuration (application.properties)
E-Commerce (Producer, 8080)
server.port=8080

# Mongo (orders)
spring.data.mongodb.uri=mongodb://localhost:27017/ecomdb
spring.data.mongodb.database=ecomdb

# Kafka producer
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer

# Eureka (optional)
# eureka.client.service-url.defaultZone=http://localhost:8761/eureka

Bank (8082)
server.port=8082
# add your own DB if needed, or keep it stateless for demo

Delivery (Consumer, 8083)
server.port=8083
spring.application.name=DeliveryService

# Mongo (deliveries)
spring.data.mongodb.uri=mongodb://localhost:27017/deliverydb
spring.data.mongodb.database=deliverydb

# Kafka consumer
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=delivery-service-group
spring.kafka.consumer.auto-offset-reset=earliest

Eureka Server (8761)
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

🧱 Shared Event Model (Critical)

Both services must contain the same class in the same package to let Spring Kafka auto-deserialize with default type headers.

src/main/java/com/ecommerce/kafka/DeliveryEvent.java

package com.ecommerce.kafka;

import java.math.BigDecimal;
import java.util.List;

public class DeliveryEvent {
    private String orderId;
    private String userId;
    private List<String> productNames;
    private BigDecimal totalAmount;
    private String deliveryDate; // e.g. 2025-09-04

    // getters/setters/toString
}


Why? Spring Kafka’s JsonSerializer adds a header @class=com.ecommerce.kafka.DeliveryEvent.
If consumer doesn’t have that exact class, you’ll get ClassNotFoundException.

Alternative (decouple packages): set JsonSerializer.ADD_TYPE_INFO_HEADERS=false on producer and configure consumer with:

spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.value.default.type=com.ecommerce.kafka.DeliveryEvent
spring.kafka.consumer.properties.spring.json.trusted.packages=*

📨 Producer Code (E-Commerce)

Kafka config

@Configuration
public class KafkaProducerConfig {
  @Bean
  public ProducerFactory<String, DeliveryEvent> producerFactory() {
    Map<String, Object> cfg = new HashMap<>();
    cfg.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(cfg);
  }
  @Bean
  public KafkaTemplate<String, DeliveryEvent> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}


Publish in order flow

@Autowired private KafkaTemplate<String, DeliveryEvent> kafkaTemplate;
private static final String DELIVERY_TOPIC = "delivery-requests";

// after saving order + items + bank debit
DeliveryEvent event = new DeliveryEvent();
event.setOrderId(order.getId());
event.setUserId(userId);
event.setProductNames(orderItems.stream().map(oi -> oi.getProduct().getName()).toList());
event.setTotalAmount(totalAmount);
event.setDeliveryDate(LocalDate.now().plusDays(3).toString());

kafkaTemplate.send(DELIVERY_TOPIC, event);


Feign to Bank

@FeignClient(name="bank-service", url="http://localhost:8082/banking")
public interface BankClient {
  @PostMapping("/api/account/debit")
  String debitAccount(@RequestBody DebitRequest req);
}

📥 Consumer Code (Delivery)

Kafka listener

@Service
public class DeliveryConsumer {
  private final DeliveryRepository repo;
  public DeliveryConsumer(DeliveryRepository repo) { this.repo = repo; }

  @KafkaListener(topics = "delivery-requests", groupId = "delivery-service-group")
  public void consume(com.ecommerce.kafka.DeliveryEvent event) {
    Delivery d = new Delivery();
    d.setOrderId(event.getOrderId());
    d.setUserId(event.getUserId());
    d.setProductNames(event.getProductNames());
    d.setTotalAmount(event.getTotalAmount().doubleValue());
    d.setDeliveryDate(LocalDateTime.now().plusDays(3)); // or parse event.getDeliveryDate()
    d.setStatus("PENDING");
    repo.save(d);
  }
}


Mongo repository

public interface DeliveryRepository extends MongoRepository<Delivery, String> {
  Optional<Delivery> findByOrderId(String orderId);
}


REST verification

GET http://localhost:8083/api/deliveries

GET http://localhost:8083/api/deliveries/{orderId}

🧪 End-to-End Test

Start infra

Kafka (and ZooKeeper if using ZK mode)

# Windows PowerShell
cd C:\kafka\bin\windows
.\zookeeper-server-start.bat ..\..\config\zookeeper.properties
.\kafka-server-start.bat ..\..\config\server.properties
.\kafka-topics.bat --list --bootstrap-server localhost:9092


If PowerShell can’t run .bat: prefix with .\
If wmic error in .bat, hardcode heap in the script: set KAFKA_HEAP_OPTS=-Xmx1G -Xms1G and remove the wmic lines.

MongoDB running on 27017

Start services (recommended order)

Bank → E-Commerce → Delivery → (Eureka if used)

Create product

curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"id":"p1","name":"Tablet","price":20000}'


Add to cart

curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{"userId":"u1","productId":"p1","quantity":1}'


Purchase (triggers Kafka)

curl -X POST http://localhost:8080/api/orders/purchase \
  -H "Content-Type: application/json" \
  -d '{"userId":"u1","accountNumber":"1234567890","description":"Demo Order"}'


Verify Delivery

curl http://localhost:8083/api/deliveries
curl http://localhost:8083/api/deliveries/<orderId>


Check Mongo (optional)

// mongo shell
use deliverydb
db.delivery.find().pretty()

🧰 Troubleshooting

Hangs on purchase:

Bank service down, Kafka not listening on 9092, cart empty, or missing product.

Kafka consumer ClassNotFoundException com.ecommerce.kafka.DeliveryEvent:

Ensure both services have identical DeliveryEvent class in package com.ecommerce.kafka.

Or disable type headers and set consumer default type as shown above.

Spring Cloud compatibility error:

Align Spring Boot 3.2/3.3 with a matching Spring Cloud Release Train.

Mongo annotations not found:

Ensure spring-boot-starter-data-mongodb is present and Maven reimported.

Windows Kafka wmic error:

Edit .bat to remove wmic usage and set KAFKA_HEAP_OPTS manually.

📁 Project Structure (suggested)
/eureka-server
  └─ src/main/java/... (optional)
/bank-service
  └─ src/main/java/com/bank/...
/ecommerce-service
  └─ src/main/java/com/ecommerce/...
      ├─ feign/BankClient.java
      ├─ kafka/KafkaProducerConfig.java
      ├─ kafka/DeliveryEvent.java
      └─ service/impl/OrderServiceImpl.java
/delivery-service
  └─ src/main/java/com/delivery/...
      ├─ controller/DeliveryController.java
      ├─ entity/Delivery.java
      ├─ repository/DeliveryRepository.java
      └─ kafka/DeliveryConsumer.java
  └─ src/main/java/com/ecommerce/kafka/DeliveryEvent.java  <-- same as producer
