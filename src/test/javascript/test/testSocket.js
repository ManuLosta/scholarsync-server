const StompJs = require("@stomp/stompjs");
const WebSocket = require('ws');

// rest of your code
const stompClient = new StompJs.Client({
    webSocketFactory: () => new WebSocket('ws://localhost:8080/message-broker')
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/global/ping', (message) => {
        if (message.isBinaryBody) {
            const textDecoder = new TextDecoder();
            const messageContent = textDecoder.decode(message.binaryBody);
            console.log('Received message: ', messageContent);
            showMessage(messageContent);
        } else {
            console.log('Received message: ', message);
            showMessage(JSON.parse(message.body).content);
        }
    });
};

function showMessage(message) {
    console.log('Message content: ', message);
    console.log(`Received message: ${message}`);
}

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    // Implement your logic here
    console.log(`Connection status: ${connected}`);
}

function showMessage(message) {
    console.log('Message content: ', message);
    console.log(`Received message: ${message}`);
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    stompClient.publish({
        destination: "/app/message",
        body: JSON.stringify({'content': document.getElementById("message").value})
    });
}

// Call the connect function to initiate the WebSocket connection
connect();