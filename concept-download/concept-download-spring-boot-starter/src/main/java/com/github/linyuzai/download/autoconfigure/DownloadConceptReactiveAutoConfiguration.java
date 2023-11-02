package com.github.linyuzai.download.autoconfigure;

import com.github.linyuzai.download.autoconfigure.web.reactive.ReactiveDownloadConcept;
import com.github.linyuzai.download.core.concept.DownloadConcept;
import com.github.linyuzai.download.core.context.DownloadContextFactory;
import com.github.linyuzai.download.core.event.DownloadEventPublisher;
import com.github.linyuzai.download.core.handler.DownloadHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Reactive 环境配置。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class DownloadConceptReactiveAutoConfiguration {

    @Bean
    public DownloadConcept downloadConcept(DownloadContextFactory factory,
                                           List<DownloadHandler> handlers,
                                           DownloadEventPublisher eventPublisher) {
        return new ReactiveDownloadConcept(factory, handlers, eventPublisher);
    }
}
