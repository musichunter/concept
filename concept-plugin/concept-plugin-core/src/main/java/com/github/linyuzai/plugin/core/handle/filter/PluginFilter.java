package com.github.linyuzai.plugin.core.handle.filter;

import com.github.linyuzai.plugin.core.concept.Plugin;
import com.github.linyuzai.plugin.core.handle.PluginHandler;
import com.github.linyuzai.plugin.core.context.PluginContext;

/**
 * 插件过滤器
 */
public interface PluginFilter extends PluginHandler, PluginHandler.Dependency {

    interface PropertyKey {

        String PREFIX = Plugin.Metadata.PropertyKey.PREFIX + "filter.";
    }

    @Override
    default void handle(PluginContext context) {
        filter(context);
    }

    /**
     * 过滤插件
     *
     * @param context 上下文 {@link PluginContext}
     */
    void filter(PluginContext context);

    /**
     * 取反
     *
     * @return {@link PluginFilter} 本身
     */
    PluginFilter negate();
}
