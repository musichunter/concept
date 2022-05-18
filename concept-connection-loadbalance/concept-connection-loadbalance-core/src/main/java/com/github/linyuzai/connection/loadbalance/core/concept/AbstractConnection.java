package com.github.linyuzai.connection.loadbalance.core.concept;

import com.github.linyuzai.connection.loadbalance.core.message.Message;
import com.github.linyuzai.connection.loadbalance.core.message.PingMessage;
import com.github.linyuzai.connection.loadbalance.core.message.PongMessage;
import com.github.linyuzai.connection.loadbalance.core.message.decode.MessageDecoder;
import com.github.linyuzai.connection.loadbalance.core.message.encode.MessageEncoder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public abstract class AbstractConnection implements Connection {

    protected final Map<Object, Object> metadata = new LinkedHashMap<>();

    protected String type;

    @NonNull
    protected MessageEncoder messageEncoder;

    @NonNull
    protected MessageDecoder messageDecoder;

    @NonNull
    protected ConnectionLoadBalanceConcept concept;

    protected boolean alive;

    protected long lastHeartbeat;

    public AbstractConnection(String type) {
        this(type, null);
    }

    public AbstractConnection(String type, Map<Object, Object> metadata) {
        this.type = type;
        if (metadata != null) {
            this.metadata.putAll(metadata);
        }
        this.alive = true;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    @Override
    public void send(@NonNull Message message) {
        if (!isAlive()) {
            return;
        }
        if (message instanceof PingMessage) {
            ping((PingMessage) message);
        } else if (message instanceof PongMessage) {
            pong((PongMessage) message);
        } else {
            MessageEncoder encoder = getMessageEncoder();
            Object encode = encoder.encode(message);
            doSend(encode);
        }
    }

    public abstract void ping(PingMessage ping);

    public abstract void pong(PongMessage pong);

    @Override
    public void close() {
        close("");
    }

    @Override
    public void close(String reason) {
        close(1000, reason);
    }

    public abstract void doSend(Object message);
}
