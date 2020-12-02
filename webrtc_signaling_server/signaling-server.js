var PORT = 8080;

var express = require('express');
var http = require('http');
var bodyParser = require('body-parser')
var main = express()
var server = http.createServer(main)
var io  = require('socket.io').listen(server);

server.listen(PORT, null, function() {
    console.log("Listening on port " + PORT);
});

main.get('/', function(req, res){ res.sendFile(__dirname + '/client.html'); });

var channels = {};
var sockets = {};
var users = {};

io.sockets.on('connection', function (socket) {
    socket.channels = {};
    sockets[socket.id] = socket;

    console.log("["+ socket.id + "] connection accepted");


    socket.on('disconnect', function () {
        for (var channel in socket.channels) {
            part(channel);
        }
        console.log("["+ socket.id + "] disconnected");
        delete sockets[socket.id];
    });

    socket.on('join', function (config) {
        console.log("["+ socket.id + "] join ", config);
        var channel = config.channel;
        var userdata = config.userdata;

        if (channel in socket.channels) {
            console.log("["+ socket.id + "] ERROR: already joined ", channel);
            return;
        }

        if (!(channel in channels)) {
            channels[channel] = {};
        }

        for (id in channels[channel]) {
            channels[channel][id].emit('addPeer', {'peer_id': socket.id, 'should_create_offer': false});
            socket.emit('addPeer', {'peer_id': id, 'should_create_offer': true});
        }

        channels[channel][socket.id] = socket;
        socket.channels[channel] = channel;
    });

    function part(channel) {
        console.log("["+ socket.id + "] part ");

        if (!(channel in socket.channels)) {
            console.log("["+ socket.id + "] ERROR: not in ", channel);
            return;
        }

        delete socket.channels[channel];
        delete channels[channel][socket.id];

        for (id in channels[channel]) {
            channels[channel][id].emit('removePeer', {'peer_id': socket.id});
            socket.emit('removePeer', {'peer_id': id});
        }
    }
    
    socket.on('createUser', function (userId) {
        console.log("["+ socket.id + "] createUser, userId: " +  userId);
        users[userId] = socket.id;

        sockets[socket.id].emit('created', {'socketID': socket.id, 'userID': userId});
    });
    socket.on('createCall', function (params) {
        console.log("createCall users:", users);
        if((params.toUserId in users)){
            var friend_socket_id = users[params.toUserId];
            if(sockets[friend_socket_id]){
                sockets[friend_socket_id].emit('createCall', {'fromUserID': params.fromUserId, 'roomName': params.roomName});
            } else {
                console.log("createCall: friend socket is not exist:" + params.toUserId);
                sockets[socket.id].emit('notRegisterdFriend', {'friendUserId': params.toUserId});
            }
        } else {
            console.log("friendId is not exist:" + params.toUserId);
            sockets[socket.id].emit('notRegisterdFriend', {'friendUserId': params.toUserId});
        }
    });
    socket.on('callOffer', function (params) {
        // console.log("callOffer:", params);
        if((params.toUserId in users)){
            var friend_socket_id = users[params.toUserId];
            if(sockets[friend_socket_id]){
                sockets[friend_socket_id].emit('callOffer', {'fromUserID': params.fromUserId, 'roomName': params.roomName, 'type': params.type, 'sdp': params.sdp});
            } else {
                console.log("callOffer: friend socket is not exist:" + params.toUserId);
            }
        } else {
            console.log("friendId is not exist:" + params.toUserId);
        }
    });
    socket.on('callAnswer', function (params) {
        // console.log("callAnswer:", params);
        if((params.toUserId in users)){
            var friend_socket_id = users[params.toUserId];
            if(sockets[friend_socket_id]){
                sockets[friend_socket_id].emit('callAnswer', {'fromUserID': params.fromUserId, 'roomName': params.roomName, 'type': params.type, 'sdp': params.sdp});
            } else {
                console.log("callAnswer: friend socket is not exist:" + params.toUserId);
            }
        } else {
            console.log("friendId is not exist:" + params.toUserId);
        }
    });
    socket.on('callIceCandidate', function (params) {
        // console.log("callIceCandidate:", params);
        if((params.toUserId in users)){
            var friend_socket_id = users[params.toUserId];
            if(sockets[friend_socket_id]){
                sockets[friend_socket_id].emit('callIceCandidate', {'fromUserID': params.fromUserId, 'id': params.id, 'label': params.label, 'candidate': params.candidate});
            } else {
                console.log("callIceCandidate: friend socket is not exist:" + params.toUserId);
            }
        } else {
            console.log("friendId is not exist:" + params.toUserId);
        }
    });
    socket.on('callEnd', function (params) {
        console.log("callEnd:", params);
        if((params.toUserId in users)){
            var friend_socket_id = users[params.toUserId];
            if(sockets[friend_socket_id]){
                sockets[friend_socket_id].emit('callEnd', {'fromUserID': params.fromUserId, 'roomName': params.roomName});
            } else {
                console.log("callEnd: friend socket is not exist:" + params.toUserId);
            }
        } else {
            console.log("friendId is not exist:" + params.toUserId);
        }
    });
});
