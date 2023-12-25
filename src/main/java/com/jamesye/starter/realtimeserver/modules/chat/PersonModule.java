package com.jamesye.starter.realtimeserver.modules.chat;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonModule {

    private static final Logger log = LoggerFactory.getLogger(PersonModule.class);

    private final SocketIONamespace namespace;


    @Autowired
    public PersonModule(SocketIOServer server) {
        this.namespace = server.addNamespace("/person");
        this.namespace.addConnectListener(onConnected());
        this.namespace.addDisconnectListener(onDisconnected());
        this.namespace.addEventListener("person", ChatMessage.class, onChatReceived());
    }

    private DataListener<ChatMessage> onChatReceived() {
        return (client, data, ackSender) ->
        {
            log.debug(" - Client[{}] - Received person message '{}'", client.getSessionId().toString(), data);
            namespace.getBroadcastOperations().sendEvent("person", data);
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            HandshakeData handshakeData = client.getHandshakeData();
            log.debug("Client[{}] - Connected to person module through '{}'", client.getSessionId().toString(), handshakeData.getUrl());
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.debug("Client[{}] - Disconnected from person module.", client.getSessionId().toString());
        };
    }

}
