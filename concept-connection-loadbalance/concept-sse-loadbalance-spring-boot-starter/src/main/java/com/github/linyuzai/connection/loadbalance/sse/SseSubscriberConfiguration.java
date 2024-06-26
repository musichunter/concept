package com.github.linyuzai.connection.loadbalance.sse;

import com.github.linyuzai.connection.loadbalance.autoconfigure.ConnectionSubscriberConfiguration;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.kafka.KafkaMessageCodecAdapter;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.kafka.KafkaTopicConnectionSubscriberFactory;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.rabbitmq.RabbitFanoutConnectionSubscriberFactory;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.rabbitmq.RabbitMessageCodecAdapter;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.redis.RedisMessageCodecAdapter;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.redis.RedisTopicConnectionSubscriberFactory;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.redis.reactive.ReactiveRedisMessageCodecAdapter;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.redis.reactive.ReactiveRedisTopicConnectionSubscriberFactory;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.redisson.RedissonTopicConnectionSubscriberFactory;
import com.github.linyuzai.connection.loadbalance.autoconfigure.subscribe.redisson.reactive.ReactiveRedissonTopicConnectionSubscriberFactory;
import com.github.linyuzai.connection.loadbalance.core.concept.ConnectionLoadBalanceConcept;
import com.github.linyuzai.connection.loadbalance.core.monitor.ScheduledConnectionLoadBalanceMonitor;
import com.github.linyuzai.connection.loadbalance.core.subscribe.ProtocolConnectionSubscriberFactory;
import com.github.linyuzai.connection.loadbalance.sse.concept.SseLoadBalanceConcept;
import com.github.linyuzai.connection.loadbalance.sse.concept.SseScoped;
import com.github.linyuzai.connection.loadbalance.sse.concept.SseSubscribeHandler;
import com.github.linyuzai.connection.loadbalance.sse.reactive.DefaultSseClientFactory;
import com.github.linyuzai.connection.loadbalance.sse.reactive.ReactiveSseClientFactory;
import com.github.linyuzai.connection.loadbalance.sse.reactive.ReactiveSseConnectionSubscriberFactory;
import com.github.linyuzai.connection.loadbalance.sse.reactive.ReactiveSseLoadBalanceEndpoint;
import com.github.linyuzai.connection.loadbalance.sse.servlet.DefaultServletSseLoadBalanceRunner;
import com.github.linyuzai.connection.loadbalance.sse.servlet.ServletSseConnectionSubscriberFactory;
import com.github.linyuzai.connection.loadbalance.sse.servlet.ServletSseLoadBalanceEndpoint;
import com.github.linyuzai.connection.loadbalance.sse.servlet.ServletSseLoadBalanceRunner;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;

/**
 * SSE 订阅者配置。
 * <p>
 * SSE subscription configuration.
 */
public class SseSubscriberConfiguration extends ConnectionSubscriberConfiguration {

    public interface SseScopedProvider extends ScopedProvider {

        @Override
        default String getScoped() {
            return SseScoped.NAME;
        }
    }

    public abstract static class RedissonTopicConfiguration
            extends ConnectionSubscriberConfiguration.RedissonTopicConfiguration
            implements SseScopedProvider {

        @Bean
        @ConditionalOnMissingBean(name = "sseRedissonTopicConnectionSubscriberFactory")
        public RedissonTopicConnectionSubscriberFactory sseRedissonTopicConnectionSubscriberFactory(
                RedissonClient redissonClient) {
            return redissonTopicConnectionSubscriberFactory(redissonClient);
        }
    }

    public abstract static class ReactiveRedissonTopicConfiguration
            extends ConnectionSubscriberConfiguration.ReactiveRedissonTopicConfiguration
            implements SseScopedProvider {

        @Bean
        @ConditionalOnMissingBean(name = "sseReactiveRedissonTopicConnectionSubscriberFactory")
        public ReactiveRedissonTopicConnectionSubscriberFactory sseReactiveRedissonTopicConnectionSubscriberFactory(
                RedissonClient redissonClient) {
            return reactiveRedissonTopicConnectionSubscriberFactory(redissonClient);
        }
    }

    public abstract static class RedissonSharedTopicConfiguration
            extends ConnectionSubscriberConfiguration.RedissonSharedTopicConfiguration
            implements SseScopedProvider {

        @Bean
        @ConditionalOnMissingBean(name = "sseRedissonSharedTopicConnectionSubscriberFactory")
        public RedissonTopicConnectionSubscriberFactory sseRedissonSharedTopicConnectionSubscriberFactory(
                RedissonClient redissonClient) {
            return redissonSharedTopicConnectionSubscriberFactory(redissonClient);
        }
    }

    public abstract static class ReactiveRedissonSharedTopicConfiguration
            extends ConnectionSubscriberConfiguration.ReactiveRedissonSharedTopicConfiguration
            implements SseScopedProvider {

        @Bean
        @ConditionalOnMissingBean(name = "sseReactiveRedissonSharedTopicConnectionSubscriberFactory")
        public ReactiveRedissonTopicConnectionSubscriberFactory sseReactiveRedissonSharedTopicConnectionSubscriberFactory(
                RedissonClient redissonClient) {
            return reactiveRedissonTopicConnectionSubscriberFactory(redissonClient);
        }
    }

    public abstract static class RedisTopicConfiguration
            extends ConnectionSubscriberConfiguration.RedisTopicConfiguration
            implements SseScopedProvider {

        @Bean
        @ConditionalOnMissingBean(name = "sseRedisTopicConnectionSubscriberFactory")
        public RedisTopicConnectionSubscriberFactory sseRedisTopicConnectionSubscriberFactory(
                StringRedisTemplate redisTemplate) {
            return redisTopicConnectionSubscriberFactory(redisTemplate);
        }

        @Bean
        @ConditionalOnMissingBean(name = "sseRedisMessageCodecAdapter")
        public RedisMessageCodecAdapter sseRedisMessageCodecAdapter() {
            return redisMessageCodecAdapter();
        }
    }

    public abstract static class ReactiveRedisTopicConfiguration
            extends ConnectionSubscriberConfiguration.ReactiveRedisTopicConfiguration
            implements SseScopedProvider {

        @Bean
        @ConditionalOnMissingBean(name = "sseReactiveRedisTopicConnectionSubscriberFactory")
        public ReactiveRedisTopicConnectionSubscriberFactory sseReactiveRedisTopicConnectionSubscriberFactory(
                ReactiveRedisTemplate<?, Object> reactiveRedisTemplate) {
            return reactiveRedisTopicConnectionSubscriberFactory(reactiveRedisTemplate);
        }

        @Bean
        @ConditionalOnMissingBean(name = "sseReactiveRedisMessageCodecAdapter")
        public ReactiveRedisMessageCodecAdapter sseReactiveRedisMessageCodecAdapter() {
            return reactiveRedisMessageCodecAdapter();
        }
    }

    public abstract static class RabbitFanoutConfiguration
            extends ConnectionSubscriberConfiguration.RabbitFanoutConfiguration
            implements SseScopedProvider {

        @Bean
        @ConditionalOnMissingBean(name = "sseRabbitFanoutConnectionSubscriberFactory")
        public RabbitFanoutConnectionSubscriberFactory sseRabbitFanoutConnectionSubscriberFactory(
                RabbitTemplate rabbitTemplate,
                RabbitListenerContainerFactory<? extends org.springframework.amqp.rabbit.listener.MessageListenerContainer>
                        rabbitListenerContainerFactory) {
            return rabbitFanoutConnectionSubscriberFactory(rabbitTemplate, rabbitListenerContainerFactory);
        }

        @Bean
        @ConditionalOnMissingBean(name = "sseRabbitMessageCodecAdapter")
        public RabbitMessageCodecAdapter sseRabbitMessageCodecAdapter() {
            return rabbitMessageCodecAdapter();
        }
    }

    public abstract static class KafkaTopicConfiguration
            extends ConnectionSubscriberConfiguration.KafkaTopicConfiguration
            implements SseScopedProvider {

        @Bean
        @ConditionalOnMissingBean(name = "sseKafkaTopicConnectionSubscriberFactory")
        public KafkaTopicConnectionSubscriberFactory sseKafkaTopicConnectionSubscriberFactory(
                KafkaTemplate<?, Object> kafkaTemplate,
                KafkaListenerContainerFactory<? extends MessageListenerContainer>
                        kafkaListenerContainerFactory) {
            return kafkaTopicConnectionSubscriberFactory(kafkaTemplate, kafkaListenerContainerFactory);
        }

        @Bean
        @ConditionalOnMissingBean(name = "sseKafkaMessageCodecAdapter")
        public KafkaMessageCodecAdapter sseKafkaMessageCodecAdapter() {
            return kafkaMessageCodecAdapter();
        }
    }

    /*public abstract static class OkHttpSseConfiguration extends OkHttpSseBaseConfiguration {

        @Bean
        public OkHttpSseConnectionSubscriberFactory okHttpSseConnectionSubscriberFactory(
                OkHttpSseClientFactory sseClientFactory) {
            OkHttpSseConnectionSubscriberFactory factory =
                    new OkHttpSseConnectionSubscriberFactory();
            factory.setProtocol("http");
            factory.setSseClientFactory(sseClientFactory);
            return factory;
        }
    }

    public abstract static class OkHttpSseSSLConfiguration extends OkHttpSseBaseConfiguration {

        @Bean
        public OkHttpSseConnectionSubscriberFactory okHttpSseConnectionSubscriberFactory(
                OkHttpSseClientFactory sseClientFactory) {
            OkHttpSseConnectionSubscriberFactory factory =
                    new OkHttpSseConnectionSubscriberFactory();
            factory.setProtocol("https");
            factory.setSseClientFactory(sseClientFactory);
            return factory;
        }
    }

    public abstract static class OkHttpSseBaseConfiguration extends ServletSseBaseConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public OkHttpSseClientFactory okHttpSseClientFactory() {
            return new DefaultOkHttpSseClientFactory();
        }
    }*/

    public abstract static class ServletSseConfiguration extends ServletSseExConfiguration {

        @Bean
        public ServletSseConnectionSubscriberFactory servletSseConnectionSubscriberFactory(
                ServletSseLoadBalanceRunner runner,
                SseLoadBalanceProperties properties) {
            ServletSseConnectionSubscriberFactory factory =
                    new ServletSseConnectionSubscriberFactory();
            factory.setProtocol("http");
            factory.setServletSseLoadBalanceRunner(runner);
            setLoadBalanceEndpoint(factory, properties);
            return factory;
        }
    }

    public abstract static class ServletSseSSLConfiguration extends ServletSseExConfiguration {

        @Bean
        public ServletSseConnectionSubscriberFactory servletSseConnectionSubscriberFactory(
                ServletSseLoadBalanceRunner runner,
                SseLoadBalanceProperties properties) {
            ServletSseConnectionSubscriberFactory factory =
                    new ServletSseConnectionSubscriberFactory();
            factory.setProtocol("https");
            factory.setServletSseLoadBalanceRunner(runner);
            setLoadBalanceEndpoint(factory, properties);
            return factory;
        }
    }

    public abstract static class ServletSseExConfiguration extends ServletSseBaseConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ServletSseLoadBalanceRunner servletSseLoadBalanceRunner() {
            return new DefaultServletSseLoadBalanceRunner("LBSse-");
        }
    }

    public abstract static class ReactiveSseConfiguration extends ReactiveSseBaseConfiguration {

        @Bean
        public ReactiveSseConnectionSubscriberFactory reactiveSseConnectionSubscriberFactory(
                ReactiveSseClientFactory sseClientFactory,
                SseLoadBalanceProperties properties) {
            ReactiveSseConnectionSubscriberFactory factory =
                    new ReactiveSseConnectionSubscriberFactory();
            factory.setProtocol("http");
            factory.setSseClientFactory(sseClientFactory);
            setLoadBalanceEndpoint(factory, properties);
            return factory;
        }
    }

    public abstract static class ReactiveSseSSLConfiguration extends ReactiveSseBaseConfiguration {

        @Bean
        public ReactiveSseConnectionSubscriberFactory reactiveSseConnectionSubscriberFactory(
                ReactiveSseClientFactory sseClientFactory,
                SseLoadBalanceProperties properties) {
            ReactiveSseConnectionSubscriberFactory factory =
                    new ReactiveSseConnectionSubscriberFactory();
            factory.setProtocol("https");
            factory.setSseClientFactory(sseClientFactory);
            setLoadBalanceEndpoint(factory, properties);
            return factory;
        }
    }

    public static class ServletSseBaseConfiguration extends SseBaseConfiguration {

        @Bean
        public ServletSseLoadBalanceEndpoint servletSseLoadBalanceEndpoint(
                SseLoadBalanceConcept concept, SseLoadBalanceProperties properties) {
            String endpoint = ConnectionLoadBalanceConcept
                    .formatEndpoint(properties.getLoadBalance().getObservableEndpoint());
            return new ServletSseLoadBalanceEndpoint(concept, endpoint);
        }
    }

    public static class ReactiveSseBaseConfiguration extends SseBaseConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public ReactiveSseClientFactory reactiveSseClientFactory() {
            return new DefaultSseClientFactory();
        }

        @Bean
        public ReactiveSseLoadBalanceEndpoint reactiveSseLoadBalanceEndpoint(
                SseLoadBalanceConcept concept, SseLoadBalanceProperties properties) {
            String endpoint = ConnectionLoadBalanceConcept
                    .formatEndpoint(properties.getLoadBalance().getObservableEndpoint());
            return new ReactiveSseLoadBalanceEndpoint(concept, endpoint);
        }
    }

    public static class SseBaseConfiguration {

        protected void setLoadBalanceEndpoint(ProtocolConnectionSubscriberFactory<?> factory,
                                              SseLoadBalanceProperties properties) {
            String endpoint = ConnectionLoadBalanceConcept
                    .formatEndpoint(properties.getLoadBalance().getSubscriberEndpoint());
            factory.setEndpoint(endpoint);
        }

        /*@Bean
        @Order(100)
        @ConditionalOnMissingBean
        public ConnectionSubscribeHandler sseConnectionSubscribeHandler() {
            return new ConnectionSubscribeHandler().addScopes(SseScoped.NAME);
        }*/

        @Bean
        @Order(100)
        @ConditionalOnMissingBean
        public SseSubscribeHandler sseSubscribeHandler() {
            return new SseSubscribeHandler();
        }

        @Bean
        @Order(300)
        @ConditionalOnMissingBean
        @ConditionalOnProperty(value = "concept.sse.load-balance.monitor.enabled", havingValue = "true", matchIfMissing = true)
        public ScheduledConnectionLoadBalanceMonitor sseScheduledConnectionLoadBalanceMonitor(
                SseLoadBalanceProperties properties) {
            long period = properties.getLoadBalance().getMonitor().getPeriod();
            boolean logger = properties.getLoadBalance().getMonitor().isLogger();
            ScheduledConnectionLoadBalanceMonitor monitor = new ScheduledConnectionLoadBalanceMonitor();
            monitor.setPeriod(period);
            monitor.setLoggerEnabled(logger);
            monitor.addScopes(SseScoped.NAME);
            return monitor;
        }
    }
}
