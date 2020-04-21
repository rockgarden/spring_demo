'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var stompClientA = null;
var username = null;
var subscriberPublic = null;
var socket = null;
const wsHost = "/websocket/ws";

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
    $("#leave").click(function () {
        leaveChatRoom();
    });
    $("#join").click(function () {
        joinChatRoom();
    });
});

// The connect function uses SockJS and stomp.js to open a connection to /ws, which is where our SockJS server waits for connections. 
function connect() {
    if (stompClientA == null) {
        // Establish a connection object
        socket = new SockJS(wsHost);
        // Get the client object of the STOMP subprotocol.
        stompClientA = Stomp.over(socket);
        // Initiate a websocket connection to the server and send a CONNECT frame.
        stompClientA.connect({}, function (frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            subsceibeGreet();
        }, onError);
    } else {
        setConnected(true);
        subsceibeGreet();
    }
}

function subsceibeGreet() {
    // Upon a successful connection, the client subscribes to the /topic/greetings destination, where the server will publish greeting messages. 
    // When a greeting is received on that destination, it will append a paragraph element to the DOM to display the greeting message.
    stompClientA.subscribe('/topic/greetings', function (greeting) {
        showGreeting(JSON.parse(greeting.body).content);
    });
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function disconnect() {
    if (stompClientA !== null) {
        stompClientA.disconnect();
    }
    setConnected(false);
    socket.close();
    console.log("Socket:", socket);
    console.log("Disconnected:", stompClientA);
}

// Retrieves the name entered by the user and uses the STOMP client to send it to the /app/hello destination (where GreetingController.greeting() will receive it).
function sendName() {
    stompClientA.send("/app/hello", {}, JSON.stringify({
        'name': $("#nameGreet").val()
    }));
}

// Uses SockJS and stomp client to connect to the /ws endpoint that we configured in Spring Boot.
function joinChatRoom(event) {
    username = document.querySelector('#name').value.trim();
    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        console.log('stompClient: ' + stompClient);
        if (stompClient == null) {
            socket = new SockJS(wsHost);
            stompClient = Stomp.over(socket);
            stompClient.connect({}, subscribe, onError);
        } else {
            subscribe();
        }
    }
}

function subscribe() {
    console.log('subscriber: ' + subscriberPublic);
    if (subscriberPublic == null) {
        // client subscribes to /topic/public destination. 
        // Callback method which is called whenever a message arrives on the subscribed topic.
        subscriberPublic = stompClient.subscribe('/topic/public', onMessageReceived);
    }
    sendJoinMessage();
}

// Upon successful connection
function sendJoinMessage() {
    console.log('stompClient: ' + stompClient);
    // Tell your username to the server by sending a message to the /app/chat.addUser destination.
    stompClient.send("/app/chat.addUser", {},
        JSON.stringify({
            sender: username,
            type: 'JOIN'
        })
    )
    connectingElement.classList.add('hidden');
    event.preventDefault();
}
// Append a paragraph element to the DOM to display the message.
function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');
    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
        subscriberPublic.unsubscribe();
        subscriberPublic = null;
    } else {
        messageElement.classList.add('chat-message');
        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);
        messageElement.appendChild(avatarElement);
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }
    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function leaveChatRoom(event) {
    if (stompClient) {
        stompClient.send("/app/chat.sendMessage", {},
            JSON.stringify({
                sender: username,
                type: 'LEAVE'
            })
        )
    }
    usernamePage.classList.remove('hidden');
    chatPage.classList.add('hidden');
}


messageForm.addEventListener('submit', sendMessage, true)

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}