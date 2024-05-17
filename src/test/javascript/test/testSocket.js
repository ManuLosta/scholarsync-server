const { Client } = require('@stomp/stompjs');
const WebSocket = require('ws');

const client = new Client({
    connectHeaders: {
        Authorization: 'Bearer a24327f0-dece-4801-946e-a13cbf461ef9'
    },
    brokerURL:'ws://localhost:8080/message-broker',
    webSocketFactory: () => new WebSocket('ws://localhost:8080/message-broker'),
    onConnect: (frame) => {
        console.log('Connected: ' + frame);
    },
    onDisconnect: (frame) => {
        console.log('Disconnected: ' + frame);
    },
    onStompError: (frame) => {
        console.log('Broker reported error: ' + frame.headers['message']);
        console.log('Additional details: ' + frame.body);
    },
    onWebSocketError: (event) => {
        console.log('WebSocket error: ', event);
    },
});

client.activate();