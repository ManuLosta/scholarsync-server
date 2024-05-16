const { Client } = require('@stomp/stompjs');
const WebSocket = require('ws');

var client = new Client({
    connectHeaders: {
        Authorization: 'Bearer fcbb4e43-5919-40cc-93f9-a314cf7cca32'
    },
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
});

client.activate();