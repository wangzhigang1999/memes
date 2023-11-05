package com.bupt.memes.ws;


import com.bupt.memes.model.ws.WSPacket;
import com.bupt.memes.model.ws.WSPacketType;
import com.bupt.memes.util.Utils;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/ws")
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketEndpoint {
    private static final Map<String, WebSocketEndpoint> webSocketMap = new LinkedHashMap<>();

    @Getter
    private static AtomicInteger count = new AtomicInteger(0);

    private Session session;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketMap.put(session.getId(), this);
        count.incrementAndGet();
        sendMessage(new WSPacket<>(session.getId(), WSPacketType.SESSION_RESPONSE));
        log.info("新客户端{}连接，当前在线人数：{}", session.getId(), count.get());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        WSPacket<String> wsPacket = Utils.fromJson(message, WSPacket.class);
        switch (wsPacket.getType()) {
            case WHISPER -> {
                wsPacket.setSessionId(session.getId());
                broadcast(wsPacket);
            }
            case SESSION_REQUEST -> sendMessage(new WSPacket<>(session.getId(), WSPacketType.SESSION_RESPONSE));

            default -> log.warn("客户端{}发送了一个未知的消息：{}", session.getId(), message);
        }
    }

    @OnError
    public void onError(Throwable error, Session session) {
        log.error("发生错误：{}，Session ID： {}", error.getMessage(), session.getId());
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(this.session.getId());
        count.decrementAndGet();
        log.info("客户端{}断开连接", this.session.getId());
    }

    @SneakyThrows
    public void sendMessage(WSPacket<?> message) {
        this.session.getBasicRemote().sendText(String.valueOf(message));
    }

    @SneakyThrows
    public static void broadcast(WSPacket<?> message) {
        for (WebSocketEndpoint webSocketEndpoint : webSocketMap.values()) {
            webSocketEndpoint.sendMessage(message);
        }
    }

}
