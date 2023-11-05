package com.bupt.memes.model.ws;

import com.bupt.memes.util.Utils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.messaging.support.GenericMessage;

@Getter
@Setter
public class WSPacket<T> extends GenericMessage<T> {

    WSPacketType type;

    String sessionId;

    public WSPacket(T payload, WSPacketType type) {
        super(payload);
        this.type = type;
    }

    @Override
    @NonNull
    public String toString() {
        return Utils.toJson(this);
    }
}
