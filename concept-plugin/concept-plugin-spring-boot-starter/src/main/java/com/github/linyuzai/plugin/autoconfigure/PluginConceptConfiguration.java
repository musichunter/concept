package com.github.linyuzai.plugin.autoconfigure;

import com.github.linyuzai.plugin.autoconfigure.event.ApplicationConnectionEventPublisher;
import com.github.linyuzai.plugin.autoconfigure.logger.CommonsPluginLogger;
import com.github.linyuzai.plugin.autoconfigure.preperties.PluginConceptProperties;
import com.github.linyuzai.plugin.autoconfigure.processor.DynamicPluginProcessor;
import com.github.linyuzai.plugin.core.autoload.PluginAutoLoader;
import com.github.linyuzai.plugin.core.autoload.PluginExecutorProvider;
import com.github.linyuzai.plugin.core.autoload.location.LocalPluginLocation;
import com.github.linyuzai.plugin.core.autoload.location.PluginLocation;
import com.github.linyuzai.plugin.core.autoload.WatchServicePluginAutoLoader;
import com.github.linyuzai.plugin.core.concept.DefaultPluginConcept;
import com.github.linyuzai.plugin.core.concept.PluginConcept;
import com.github.linyuzai.plugin.core.context.DefaultPluginContextFactory;
import com.github.linyuzai.plugin.core.context.PluginContextFactory;
import com.github.linyuzai.plugin.core.event.PluginEventListener;
import com.github.linyuzai.plugin.core.event.PluginEventPublisher;
import com.github.linyuzai.plugin.core.extract.PluginExtractor;
import com.github.linyuzai.plugin.core.factory.PluginFactory;
import com.github.linyuzai.plugin.core.filter.PluginFilter;
import com.github.linyuzai.plugin.core.handle.DefaultPluginHandlerChainFactory;
import com.github.linyuzai.plugin.core.handle.PluginHandlerChainFactory;
import com.github.linyuzai.plugin.core.logger.PluginErrorLogger;
import com.github.linyuzai.plugin.core.logger.PluginLoadLogger;
import com.github.linyuzai.plugin.core.logger.PluginLogger;
import com.github.linyuzai.plugin.core.repository.DefaultPluginRepository;
import com.github.linyuzai.plugin.core.repository.PluginRepository;
import com.github.linyuzai.plugin.core.resolve.ContentResolver;
import com.github.linyuzai.plugin.core.resolve.EntryResolver;
import com.github.linyuzai.plugin.core.resolve.PluginResolver;
import com.github.linyuzai.plugin.core.resolve.PropertiesResolver;
import com.github.linyuzai.plugin.core.tree.DefaultPluginTreeFactory;
import com.github.linyuzai.plugin.core.tree.PluginTreeFactory;
import com.github.linyuzai.plugin.jar.autoload.JarLocationFilter;
import com.github.linyuzai.plugin.jar.factory.JarPluginFactory;
import com.github.linyuzai.plugin.jar.factory.JarSubPluginFactory;
import com.github.linyuzai.plugin.jar.resolve.JarClassNameResolver;
import com.github.linyuzai.plugin.jar.resolve.JarClassResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class PluginConceptConfiguration {

    @Configuration(proxyBeanMethods = false)
    public static class JarConfiguration {

        @Bean
        public JarPluginFactory jarPluginFactory() {
            return new JarPluginFactory();
        }

        @Bean
        public JarSubPluginFactory jarSubPluginFactory() {
            return new JarSubPluginFactory();
        }

        @Bean
        public JarClassNameResolver jarClassNameResolver() {
            return new JarClassNameResolver();
        }

        @Bean
        public JarClassResolver jarClassResolver() {
            return new JarClassResolver();
        }

        @Bean
        @ConditionalOnMissingBean
        public PluginLocation.Filter jarLocationFilter() {
            return new JarLocationFilter();
        }
    }

    @Bean
    public static DynamicPluginProcessor dynamicPluginProcessor() {
        return new DynamicPluginProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginContextFactory pluginContextFactory() {
        return new DefaultPluginContextFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginHandlerChainFactory pluginHandlerChainFactory() {
        return new DefaultPluginHandlerChainFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginTreeFactory pluginTreeFactory() {
        return new DefaultPluginTreeFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginRepository pluginRepository() {
        return new DefaultPluginRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginEventPublisher pluginEventPublisher(ApplicationEventPublisher eventPublisher) {
        return new ApplicationConnectionEventPublisher(eventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginLogger pluginLogger() {
        return new CommonsPluginLogger();
    }

    @Bean
    public PluginLoadLogger pluginLoadLogger(PluginLogger logger) {
        return new PluginLoadLogger(logger);
    }

    @Bean
    public PluginErrorLogger pluginErrorLogger(PluginLogger logger) {
        return new PluginErrorLogger(logger);
    }

    @Bean
    public EntryResolver pluginEntryResolver() {
        return new EntryResolver();
    }

    @Bean
    public ContentResolver pluginContentResolver() {
        return new ContentResolver();
    }

    @Bean
    public PropertiesResolver pluginPropertiesResolver() {
        return new PropertiesResolver();
    }

    @Bean(initMethod = "initialize", destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public PluginConcept pluginConcept(PluginContextFactory contextFactory,
                                       PluginHandlerChainFactory handlerChainFactory,
                                       PluginTreeFactory treeFactory,
                                       PluginRepository repository,
                                       PluginEventPublisher eventPublisher,
                                       PluginLogger logger,
                                       List<PluginFactory> factories,
                                       List<PluginResolver> resolvers,
                                       List<PluginFilter> filters,
                                       List<PluginExtractor> extractors,
                                       List<PluginEventListener> eventListeners) {
        return new DefaultPluginConcept.Builder()
                .contextFactory(contextFactory)
                .handlerChainFactory(handlerChainFactory)
                .treeFactory(treeFactory)
                .repository(repository)
                .eventPublisher(eventPublisher)
                .logger(logger)
                .addFactories(factories)
                .addResolvers(resolvers)
                .addFilters(filters)
                .addExtractors(extractors)
                .addEventListeners(eventListeners)
                .build();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(name = "concept.plugin.autoload.enabled", havingValue = "true", matchIfMissing = true)
    public static class AutoloadConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public PluginExecutorProvider pluginExecutorProvider() {
            return PluginExecutorProvider.NO_EXECUTOR;
        }

        @Bean
        @ConditionalOnMissingBean
        public PluginLocation pluginLocation(PluginConceptProperties properties,
                                             PluginLocation.Filter filter) {
            String basePath = properties.getAutoload().getLocation().getBasePath();
            return new LocalPluginLocation(basePath, filter);
        }

        @Bean(initMethod = "start", destroyMethod = "stop")
        @ConditionalOnMissingBean
        public PluginAutoLoader pluginAutoLoader(PluginConcept concept,
                                                 PluginExecutorProvider executorProvider,
                                                 PluginLocation location) {
            return new WatchServicePluginAutoLoader(concept, executorProvider.get(), location);
        }
    }
}
