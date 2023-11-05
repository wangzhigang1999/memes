package com.bupt.memes.model.ws;

import com.bupt.memes.util.Utils;
import lombok.NonNull;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;


public class WSPacket<T> extends GenericMessage<T> {


    public WSPacket(T payload, WSPacketType type) {
        super(payload, Map.of("type", type));
    }

    public WSPacket(T payload, Map<String, Object> headers) {
        super(payload, headers);
    }

    @Override
    @NonNull
    public String toString() {
        return Utils.toJson(this);
    }
}
