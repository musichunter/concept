package com.github.linyuzai.connection.loadbalance.core.extension;

import com.github.linyuzai.connection.loadbalance.core.concept.AbstractConnection;
import com.github.linyuzai.connection.loadbalance.core.concept.ConnectionLoadBalanceConcept;
import com.github.linyuzai.connection.loadbalance.core.message.PingMessage;
import com.github.linyuzai.connection.loadbalance.core.message.PongMessage;
import lombok.Getter;

@Getter
public class IdConnection extends AbstractConnection {

    private final Object id;

    public IdConnection(Object id, String type, ConnectionLoadBalanceConcept concept) {
        super(type);
        setConcept(concept);
        this.id = id;
    }

    @Override
    public void ping(PingMessage ping) {

    }

    @Override
    public void pong(PongMessage pong) {

    }

    @Override
    public void doSend(Object message) {

    }

    @Override
    public void close(String reason) {

    }

    @Override
    public void close(int code, String reason) {

    }
}
