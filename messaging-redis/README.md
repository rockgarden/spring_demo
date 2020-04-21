ou will build an application that uses StringRedisTemplate to publish a string message and has a POJO subscribe for the message by using MessageListenerAdapter.

It may sound strange to be using Spring Data Redis as the means to publish messages, but, as you will discover, Redis provides not only a NoSQL data store but a messaging system as well.

使用Spring Data Redis作为发布消息的手段听起来很奇怪，但是，正如您将发现的那样，Redis不仅提供了NoSQL数据存储，还提供了消息传递系统。

## Create a Redis Message Receiver
In any messaging-based application, there are message publishers and messaging receivers. To create the message receiver, implement a receiver with a method to respond to messages.

The Receiver is a POJO that defines a method for receiving messages. When you register the Receiver as a message listener, you can name the message-handling method whatever you want.

For demonstration purposes, the receiver is counting the messages received. That way, it can signal when it has received a message.

## Register the Listener and Send a Message
Spring Data Redis provides all the components you need to send and receive messages with Redis. Specifically, you need to configure:

* A connection factory
* A message listener container
* A Redis template

You will use the Redis template to send messages, and you will register the Receiver with the message listener container so that it will receive messages. The connection factory drives both the template and the message listener container, letting them connect to the Redis server.

This example uses Spring Boot’s default RedisConnectionFactory, an instance of JedisConnectionFactory that is based on the [Jedis](https://github.com/xetorthio/jedis) Redis library. The connection factory is injected into both the message listener container and the Redis template.