package com.github.linyuzai.connection.loadbalance.websocket.concept;

import com.github.linyuzai.connection.loadbalance.core.intercept.ConnectionRequest;
import org.springframework.http.HttpRequest;

import java.net.InetSocketAddress;

public interface WebSocketRequest extends ConnectionRequest, HttpRequest {

    /**
     * Return the address on which the request was received.
     */
    InetSocketAddress getLocalAddress();

    /**
     * Return the address of the remote client.
     */
    InetSocketAddress getRemoteAddress();
}
