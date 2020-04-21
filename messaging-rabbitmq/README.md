## Set up the RabbitMQ Broker
Before you can build your messaging application, you need to set up a server to handle receiving and sending messages.

RabbitMQ is an AMQP server. The server is freely available at https://www.rabbitmq.com/download.html. 

## Create a RabbitMQ Message Receiver
With any messaging-based application, you need to create a receiver that responds to published messages.

The Receiver is a POJO(Plain Ordinary Java Object) that defines a method for receiving messages. When you register it to receive messages, you can name it anything you want.

For convenience, this POJO also has a CountDownLatch. This lets it signal that the message has been received. This is something you are not likely to implement in a production application.

## Register the Listener and Send a Message
Spring AMQP’s RabbitTemplate provides everything you need to send and receive messages with RabbitMQ. However, you need to:

* Configure a message listener container.

* Declare the queue, the exchange, and the binding between them.

* Configure a component to send some messages to test the listener.

Spring Boot automatically creates a connection factory and a RabbitTemplate, reducing the amount of code you have to write.
You will use RabbitTemplate to send messages, and you will register a Receiver with the message listener container to receive messages. The connection factory drives both, letting them connect to the RabbitMQ server.

The bean defined in the listenerAdapter() method is registered as a message listener in the container (defined in container()). It listens for messages on the spring-boot queue. Because the Receiver class is a POJO, it needs to be wrapped in the MessageListenerAdapter, where you specify that it invokes receiveMessage.

JMS queues and AMQP queues have different semantics. For example, JMS sends queued messages to only one consumer. While AMQP queues do the same thing, AMQP producers do not send messages directly to queues. Instead, a message is sent to an exchange, which can go to a single queue or fan out to multiple queues, emulating the concept of JMS topics.
The message listener container and receiver beans are all you need to listen for messages. To send a message, you also need a Rabbit template.

The queue() method creates an AMQP(Advanced Message Queuing Protocol) queue. The exchange() method creates a topic exchange. The binding() method binds these two together, defining the behavior that occurs when RabbitTemplate publishes to an exchange.

Spring AMQP requires that the Queue, the TopicExchange, and the Binding be declared as top-level Spring beans in order to be set up properly.
In this case, we use a topic exchange, and the queue is bound with a routing key of foo.bar.#, which means that any messages sent with a routing key that begins with foo.bar. are routed to the queue.

## Send a Test Message
In this sample, test messages are sent by a CommandLineRunner, which also waits for the latch in the receiver and closes the application context.

The template routes the message to the exchange with a routing key of foo.bar.baz, which matches the binding.