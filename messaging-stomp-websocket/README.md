# WebSocket Chat Demo

You can checkout the live version of the application at https://spring-ws-chat.herokuapp.com/

## WebSocket

WebSocket 协议是基于 TCP 的一种网络协议，它实现了浏览器与服务器全双工（Full-duplex）通信——允许服务器主动发送信息给客户端。

HTML 5 定义了 WebSocket 协议，能更好得节省服务器资源和带宽，并且能够更实时地进行通讯，现在主流的浏览器都已经支持。

它的最大特点就是，服务器可以主动向客户端推送信息，客户端也可以主动向服务器发送信息，是真正的双向平等对话，属于服务器推送技术的一种。在 WebSocket API 中，浏览器和服务器只需要完成一次握手，两者之间就直接可以创建持久性的连接，并进行双向数据传输。

**优点**

- 较少的控制开销。在连接创建后，服务器和客户端之间交换数据时，用于协议控制的数据包头部相对较小。在不包含扩展的情况下，对于服务器到客户端的内容，此头部大小只有 2 至 10 字节（和数据包长度有关）；对于客户端到服务器的内容，此头部还需要加上额外的 4 字节的掩码。相对于 HTTP 请求每次都要携带完整的头部，此项开销显著减少了。
- 更强的实时性。由于协议是全双工的，所以服务器可以随时主动给客户端下发数据。相对于 HTTP 请求需要等待客户端发起请求服务端才能响应，延迟明显更少；即使是和 Comet 等类似的长轮询比较，其也能在短时间内更多次地传递数据。
  保持连接状态。与 HTTP 不同的是，Websocket 需要先创建连接，这就使得其成为一种有状态的协议，之后通信时可以省略部分状态信息，而 HTTP 请求可能需要在每个请求都携带状态信息（如身份认证等）。
- 更好的二进制支持。Websocket 定义了二进制帧，相对 HTTP，可以更轻松地处理二进制内容。 可以支持扩展。Websocket 定义了扩展，用户可以扩展协议、实现部分自定义的子协议。如部分浏览器支持压缩等。
- 更好的压缩效果。相对于 HTTP 压缩，Websocket 在适当的扩展支持下，可以沿用之前内容的上下文，在传递类似的数据时，可以显著地提高压缩率。

WebSocket 在握手之后便直接基于 TCP 进行消息通信，但 WebSocket 只是 TCP 上面非常轻的一层，它仅仅将 TCP 的字节流转换成消息流（文本或二进制），至于怎么解析这些消息的内容完全依赖于应用本身。

因此为了协助 Client 与 Server 进行消息格式的协商，WebSocket 在握手的时候保留了一个子协议字段。

## Stomp 和 WebSocket

STOMP 即 Simple（or Streaming）Text Orientated Messaging Protocol，简单（流）文本定向消息协议，它提供了一个可互操作的连接格式，允许 STOMP 客户端与任意 STOMP 消息代理（Broker）进行交互。STOMP 协议由于设计简单，易于开发客户端，因此在多种语言和多种平台上得到了广泛的应用。

STOMP 协议并不是为 Websocket 设计的，它是属于消息队列的一种协议，它和 Amqp、Jms 平级。只不过由于它的简单性恰巧可以用于定义 Websocket 的消息体格式。可以这么理解，Websocket 结合 Stomp 子协议段，来让客户端和服务器在通信上定义的消息内容达成一致。

STOMP 协议分为客户端和服务端。

### STOMP 服务端

STOMP 服务端被设计为客户端可以向其发送消息的一组目标地址。STOMP 协议并没有规定目标地址的格式，它由使用协议的应用自己来定义。例如，/topic/a、/queue/a、queue-a 对于 STOMP 协议来说都是正确的。应用可以自己规定不同的格式以此来表明不同格式代表的含义。比如应用自己可以定义以 /topic 打头的为发布订阅模式，消息会被所有消费者客户端收到，以 /user 开头的为点对点模式，只会被一个消费者客户端收到。

### STOMP 客户端

对于 STOMP 协议来说，客户端会扮演下列两种角色的任意一种：

作为生产者，通过 SEND 帧发送消息到指定的地址；
作为消费者，通过发送 SUBSCRIBE 帧到已知地址来进行消息订阅，而当生产者发送消息到这个订阅地址后，订阅该地址的其他消费者会受到通过 MESSAGE 帧收到该消息。
实际上，WebSocket 结合 STOMP 相当于构建了一个消息分发队列，客户端可以在上述两个角色间转换，订阅机制保证了一个客户端消息可以通过服务器广播到多个其他客户端，作为生产者，又可以通过服务器来发送点对点消息。

### STOMP 帧结构

```bash
COMMAND
header1:value1
header2:value2
Body^@
```

其中，^@ 表示行结束符。

一个 STOMP 帧由三部分组成：命令、Header（头信息）、Body（消息体）。

命令使用 UTF-8 编码格式，命令有 SEND、SUBSCRIBE、MESSAGE、CONNECT、CONNECTED 等。
Header 也使用 UTF-8 编码格式，它类似 HTTP 的 Header，有 content-length、content-type 等。
Body 可以是二进制也可以是文本，注意 Body 与 Header 间通过一个空行（EOL）来分隔。

来看一个实际的帧例子：

```
SEND
destination:/broker/roomId/1
content-length:57

{“type":"OUT","content":"ossxxxxx-wq-yyyyyyyy"}
```

- 第 1 行：表明此帧为 SEND 帧，是 COMMAND 字段。
- 第 2 行：Header 字段，消息要发送的目的地址，是相对地址。
- 第 3 行：Header 字段，消息体字符长度。
- 第 4 行：空行，间隔 Header 与 Body。
- 第 5 行：消息体，为自定义的 JSON 结构。

更多 STOMP 协议细节，可以参考 [STOMP 官网](http://stomp.github.io/index.html)。

## WebSocket 事件

Websocket 使用 ws 或 wss 的统一资源标志符，类似于 HTTPS，其中 wss 表示在 TLS 之上的 Websocket。例如：

```
ws://example.com/wsapi
wss://secure.example.com/
```

Websocket 使用和 HTTP 相同的 TCP 端口，可以绕过大多数防火墙的限制。默认情况下，Websocket 协议使用 80 端口；运行在 TLS 之上时，默认使用 443 端口。

```
事件	事件处理程序	描述
open	Sokcket onopen	连接建立时触发
message	Sokcket onopen	客户端接收服务端数据时触发
error	Sokcket onerror	通讯发生错误时触发
close	Sokcket onclose	链接关闭时触发
```

下面是一个页面使用 Websocket 的示例：

```JS
var ws = new WebSocket("ws://localhost:8080");

ws.onopen = function(evt) {
  console.log("Connection open ...");
  ws.send("Hello WebSockets!");
};

ws.onmessage = function(evt) {
  console.log( "Received Message: " + evt.data);
  ws.close();
};

ws.onclose = function(evt) {
  console.log("Connection closed.");
};
```

Spring Boot 提供了 Websocket 组件 spring-boot-starter-websocket，用来支持在 Spring Boot 环境下对 Websocket 的使用。

### Websocket 聊天室

Websocket 双相通讯的特性非常适合开发在线聊天室，这里以在线多人聊天室为示例，演示 Spring Boot Websocket 的使用。

首先我们梳理一下聊天室都有什么功能：

- 支持用户加入聊天室，对应到 Websocket 技术就是建立连接 onopen
- 支持用户退出聊天室，对应到 Websocket 技术就是关闭连接 onclose
- 支持用户在聊天室发送消息，对应到 Websocket 技术就是调用 onmessage 发送消息
- 支持异常时提示，对应到 Websocket 技术 onerror

## 代码说明

### 服务端

#### WebsocketDemoApplication

使用`@EnableWebSocket`注释启用 WebSocket。

@EnableWebSocket 启用纯 Web 套接字，如果您想使用 stomp，则需要一个代理 broker，需要@EnableWebSocketMessageBroker 来申明。

#### WebSocketConfig

配置 websocket 端点和消息代理。

使用`@EnableWebSocketMessageBroker`注释启用 WebSocketBroker-消息代理用来使用 stomp。

@EnableWebSocketMessageBroker 为交换的消息提供了更好的处理方法，易于有兴趣的客户收到广播；否则，必须跟踪会话并遍历会话，才可以确保将消息发送给每个有兴趣的客户。
MessageBroker开箱即用，提供确认标志，这些标志将在客户端和服务器之间互换，以确保消息的传输和拦截。
注意：注释@EnableWebSocketMessageBroker默认情况下不会添加基础的全功能代理，而是添加“简单的代理”。
这个简单版本：
- supports subset of STOMP: SUBSCRIBE, UNSUBSCRIBE, MESSAGE
- no acks, receipts, transactions
- simple message sending loop
- not suitable for clustering

reference:[The MessageBroker WebSocket Subprotocol](https://tools.ietf.org/id/draft-hapner-hybi-messagebroker-subprotocol-00.html)

#### ChatMessage

ChatMessage 模型是将在客户端和服务器之间交换的消息有效负载。

#### ChatController

控制器定义消息处理方法。这些方法将负责从一个客户端接收消息，然后将其广播给其他客户端。

如果您从 websocket 配置中调用，从客户端发送的所有以/ app 开头的消息将被路由到这些消息处理方法，这些方法以@MessageMapping 注释。

#### WebSocketEventListener

使用事件监听器来监听套接字连接和断开事件，以便我们可以记录这些事件，并在用户加入或离开聊天室时广播它们。

我们已经在 ChatController 中定义的 addUser（）方法中广播了用户加入事件。 因此，我们无需在 SessionConnected 事件中进行任何操作。

在 SessionDisconnect 事件中，我们编写了代码以从 websocket 会话中提取用户名，并将用户离开事件广播到所有连接的客户端。

### front-end

#### index.html

HTML 文件包含用于显示聊天消息的用户界面。 它包括 sockjs 和 stomp javascript 库。

SockJS 是一个 WebSocket 客户端，它尝试使用本机 WebSocket，并为不支持 WebSocket 的旧版浏览器提供智能后备选项。

STOMP JS 是 javascript 的临时客户端。

#### main.js

添加连接到 websocket 端点以及发送和接收消息所需的 javascript。

connect（）函数使用 SockJS 和 stomp 客户端连接到我们在 Spring Boot 中配置的/ ws 端点。

成功建立连接后，客户端将订阅/ topic / public 目标，并通过向/app/chat.addUser 目标发送消息来向服务器告知用户的名称。

stompClient.subscribe（）函数采用一种回调方法，只要有消息到达订阅的主题，就会调用该方法。

## Requirements

1. Java - 1.8.x

2. Maven - 3.x.x

## Steps to Setup

**1. Build and run the app using maven**

```bash
cd spring-boot-websocket-chat-demo
mvn package
java -jar target/websocket-demo-0.0.1-SNAPSHOT.jar
```

Alternatively, you can run the app directly without packaging it like so -

```bash
mvn spring-boot:run
```

## Learn More

https://github.com/sockjs/sockjs-client


# spring-boot-demo-websocket

> 演示了后端主动往前端推送服务器状态信息。前端页面基于vue和element-ui实现。

## 2. 运行方式

1. 启动 `SpringBootDemoWebsocketApplication.java`
2. 访问 http://localhost:8080/websocket/server.html

## 3. 参考

### 3.1. 后端

1. Spring Boot 整合 Websocket 官方文档：https://docs.spring.io/spring/docs/5.1.2.RELEASE/spring-framework-reference/web.html#websocket
2. 服务器信息采集 oshi 使用：https://github.com/oshi/oshi

### 3.2. 前端

1. vue.js 语法：https://cn.vuejs.org/v2/guide/
2. element-ui 用法：http://element-cn.eleme.io/#/zh-CN
3. stomp.js 用法：https://github.com/jmesnil/stomp-websocket
4. sockjs 用法：https://github.com/sockjs/sockjs-client
5. axios.js 用法：https://github.com/axios/axios#example