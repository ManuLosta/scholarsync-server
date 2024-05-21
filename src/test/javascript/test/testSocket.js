const SockJS = require('sockjs-client');
const Stomp = require('stompjs');

const WEBSOCKET_URL = 'http://localhost:8080/message-broker'; // Adjust this URL to your WebSocket endpoint
const USER_SESSION_ID = 'c8380da9-9cb7-43b3-b172-d0f76dd5ae80'; // Replace with the actual session ID

function connect() {
    const socket = new SockJS(WEBSOCKET_URL);
    const stompClient = Stomp.over(socket);

    // Log subscription path

    stompClient.connect({}, function (frame) {
        console.log('Connected2: ' + frame);
        const subscriptionPath = `/individual/${USER_SESSION_ID}/notification`;
        stompClient.subscribe(subscriptionPath, function (message) {
            console.log('received message', '/session/' + USER_SESSION_ID + '/notification', message)
            handleNotification(message);
            // message.ack();
        })
        stompClient.subscribe("/global/ping", function (message) {
            console.log('received message', "/global/ping", message)
            handleNotification(message);
            // message.ack();
        });

        stompClient.debug(function (msg) {
            console.log('debug received message', msg)
        })
    }, function (error) {
        console.error('Connection error: ' + error);
    });

}

function handleNotification(message) {
    showNotification(message.body);
}

function showNotification(message) {
    console.log('Received notification: ', message);
}

connect();
