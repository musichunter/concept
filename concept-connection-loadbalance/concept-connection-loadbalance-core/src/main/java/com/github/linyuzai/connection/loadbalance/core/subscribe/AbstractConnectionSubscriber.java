package com.github.linyuzai.connection.loadbalance.core.subscribe;

import com.github.linyuzai.connection.loadbalance.core.concept.Connection;
import com.github.linyuzai.connection.loadbalance.core.concept.ConnectionLoadBalanceConcept;
import com.github.linyuzai.connection.loadbalance.core.message.MessageIdempotentVerifier;
import com.github.linyuzai.connection.loadbalance.core.server.ConnectionServer;

import java.util.UUID;

public abstract class AbstractConnectionSubscriber implements ConnectionSubscriber {

    public static final String DELIMITER = "@";

    public static final String PREFIX = "ConceptConnectionLB";

    @Override
    public synchronized void subscribe(ConnectionLoadBalanceConcept concept) {
        String topic = getTopic(concept);
        String from = getFrom(concept);
        Connection connection = create(topic, concept);
        connection.getMessageSendInterceptors().add((message, con) -> {
            message.setId(getMessageId());
            message.setFrom(from);
            return true;
        });
        concept.onEstablish(connection);
    }

    protected abstract Connection create(String topic, ConnectionLoadBalanceConcept concept);

    protected abstract String getExtension();

    protected void onMessage(Connection connection, Object message) {
        connection.getConcept().onMessage(connection, message, msg -> {
            ConnectionLoadBalanceConcept concept = connection.getConcept();
            return !getFrom(concept).equals(msg.getFrom()) &&
                    getMessageIdempotentVerifier(concept).verify(msg);
        });
    }

    protected MessageIdempotentVerifier getMessageIdempotentVerifier(ConnectionLoadBalanceConcept concept) {
        return concept.getMessageIdempotentVerifier();
    }

    protected String getMessageId() {
        return UUID.randomUUID().toString();
    }

    protected String getFrom(ConnectionLoadBalanceConcept concept) {
        ConnectionServer local = getLocal(concept);
        return local == null ? useUnknownIfNull(null) : useUnknownIfNull(local.getInstanceId());
    }

    protected String getTopic(ConnectionLoadBalanceConcept concept) {
        ConnectionServer local = getLocal(concept);
        return PREFIX + DELIMITER + getExtension() + DELIMITER +
                (local == null ? useUnknownIfNull(null) : useUnknownIfNull(local.getServiceId()));
    }

    protected ConnectionServer getLocal(ConnectionLoadBalanceConcept concept) {
        return concept.getConnectionServerManager().getLocal();
    }

    protected String useUnknownIfNull(String s) {
        if (s == null) {
            return "Unknown";
        }
        return s;
    }
}
