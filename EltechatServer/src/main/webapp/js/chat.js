var socketUrl = '192.168.137.1';
var port = '8080';
var webSocket;
var name;
var username;
var password;

$(document).ready(function () {
    username = sessionStorage.getItem('username');
    password = sessionStorage.getItem('password');

    openSocket();
    $('#messageForm').submit(function (event) {
        event.preventDefault();
    })
});

function openSocket() {
    if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED)
        return;

    if (password == null)
        webSocket = new WebSocket("ws://" + socketUrl + ":" + port + "/chat?name=" + username);
    else
        webSocket = new WebSocket("ws://" + socketUrl + ":" + port + "/chat?name=" + username + "&password=" + password);


    webSocket.onopen = function (event) {
    };

    webSocket.onmessage = function (event) {
        parseJSON(event.data);
    };

    webSocket.onclose = function (event) {
    };
}

function parseJSON(message) {
    var jsonObject = $.parseJSON(message);
    switch (jsonObject.flag) {
        case 'loginSuccess':
            name = username;
            $('#user').text(name);
            break;
        case 'loginFailureNickname':
            closeChat();
            document.location.href = "index.html";
            alert("Пользователь с таким именем уже существует");
            break;
        case 'loginServerFailure':
            closeChat();
            alert("Произошла ошибка сервера");
            break;
        case 'newUserConnect':
            $('#online').text(jsonObject.online);
            var li = '<li>' + jsonObject.name + ': Подключился к чату </li>';
            $('#message').append(li);
            break;
        case 'userDisconnect':
            $('#online').text(jsonObject.online);
            var li = '<li>' + jsonObject.name + ': Покинул чат </li>';
            $('#message').append(li);
            break;
        case 'message':
            var li = '<li id="' + jsonObject.messageId + '">' + jsonObject.name + ': ' + jsonObject.message + '</li>'
            $('#message').append(li);
            break;
        case 'kick':
            if (jsonObject.name == name) {
                closeChat();
                alert('Вы были исключены из чата');
            }
            break;
        case 'mute':
            if (jsonObject.name == name) {
                var li = '<li>Вам запретили писать в чат</li>';
                $('#message').append(li);
                $('#inputMessage').val('');
            }
            break;
        case 'deleteMessage':
            var li = document.getElementById(jsonObject.messageId);
            li.textContent = li.textContent.split(":")[0] + ': <cообщение удалено>';
            break;
    }
}

function sendMessage() {
    var message = $('#inputMessage').val().trim();
    if (message.length > 0)
        if (!checkCommand(message))
            sendMessageToServer('message', message);
        else
            alert('empty message');
    $('#inputMessage').val('');
}

function closeChat() {
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('password');
    document.location.href = "index.html";
}

function sendMessageToServer(flag, message) {
    var json = '{""}';
    var myObject = new Object();

    myObject.flag = flag;
    myObject.message = message;
    json = JSON.stringify(myObject);
    webSocket.send(json);
}

function checkCommand(message) {
    var pattern = /^(\.kick) ([A-Za-z0-9]{1,255})/;
    var match = pattern.exec(message);
    if (match != null) {
        json = '{""}';
        var myObject = new Object();

        myObject.flag = 'kick';
        myObject.name = match[2];
        json = JSON.stringify(myObject);
        webSocket.send(json);
        return true;
    }

    var pattern = /^(\.mute) ([A-Za-z0-9]{1,255})/;
    var match = pattern.exec(message);
    if (match != null) {
        json = '{""}';
        var myObject = new Object();

        myObject.flag = 'mute';
        myObject.name = match[2];
        json = JSON.stringify(myObject);
        webSocket.send(json);
        return true;
    }

    var pattern = /^(\.unmute) ([A-Za-z0-9]{1,255})/;
    var match = pattern.exec(message);
    if (match != null) {
        json = '{""}';
        var myObject = new Object();

        myObject.flag = 'unmute';
        myObject.name = match[2];
        json = JSON.stringify(myObject);
        webSocket.send(json);
        return true;
    }

    var pattern = /^(\.delmsg) ([0-9]{1,255})/;
    var match = pattern.exec(message);
    if (match != null) {
        json = '{""}';
        var myObject = new Object();

        myObject.flag = 'deleteMessage';
        myObject.messageId = match[2];
        json = JSON.stringify(myObject);
        webSocket.send(json);
        return true;
    }
    return false;
}