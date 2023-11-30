package com.github.linyuzai.download.autoconfigure.source.reactive;

import com.github.linyuzai.download.core.context.DownloadContext;
import com.github.linyuzai.download.core.event.DownloadEventPublisher;
import com.github.linyuzai.download.core.options.DownloadOptions;
import com.github.linyuzai.download.core.source.Source;
import com.github.linyuzai.download.core.source.SourceFactory;
import com.github.linyuzai.download.core.source.http.HttpSourceFactory;
import com.github.linyuzai.download.core.source.prefix.PrefixSourceFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.Charset;

/**
 * 匹配前缀 'http://' 或 'https://' 并使用 {@link WebClient} 的 {@link SourceFactory}。
 */
public class WebClientSourceFactory extends PrefixSourceFactory {

    @Override
    public Source create(Object source, DownloadContext context) {
        String url = (String) source;
        DownloadOptions options = DownloadOptions.get(context);
        Charset charset = options.getCharset();
        boolean cacheEnabled = options.isSourceCacheEnabled();
        String cachePath = options.getSourceCachePath();
        WebClientSource build = new WebClientSource.Builder<>()
                .url(url)
                .asyncLoad(true)
                .charset(charset)
                .cacheEnabled(cacheEnabled)
                .cachePath(cachePath)
                .build();
        DownloadEventPublisher publisher = DownloadEventPublisher.get(context);
        publisher.publish(new WebClientSourceCreatedEvent(context, build));
        return build;
    }

    @Override
    protected String[] getPrefixes() {
        return HttpSourceFactory.PREFIXES;
    }
}
