package com.bupt.memes.ws;


import com.bupt.memes.model.ws.WSPacket;
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
        log.info("有新连接加入：{}，当前在线人数为：{}", session.getId(), count.get());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到客户端{}消息：{}", session.getId(), message);
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
