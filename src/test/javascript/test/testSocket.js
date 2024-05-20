const StompJs = require("@stomp/stompjs");
const WebSocket = require('ws');
const process = require('process');

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
            showMessage(messageContent);
        } else {
            console.log('Received message: ', message);
            showMessage(JSON.parse(message.body).content);
        }
    });
};

stompClient.onWebSocketClose = (event) => {
    setConnected(false);
    console.log('Disconnected: '+ event.code + ' ' + event.reason);
    process.exit(0);

}

function showMessage(message) {
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