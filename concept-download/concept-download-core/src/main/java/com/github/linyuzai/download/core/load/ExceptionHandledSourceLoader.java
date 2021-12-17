package com.github.linyuzai.download.core.load;

import com.github.linyuzai.download.core.context.DownloadContext;
import com.github.linyuzai.download.core.source.Source;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 处理了异常的 加载器 / Loader which handling exception
 */
@Getter
@AllArgsConstructor
public class ExceptionHandledSourceLoader implements SourceLoader {

    private Source source;

    private SourceLoadExceptionHandler handler;

    /**
     * @return 和下载源的是否异步加载一致 / Is consistent with the download source
     */
    @Override
    public boolean isAsyncLoad() {
        return source.isAsyncLoad();
    }

    /**
     * 捕获异常，并回调异常处理器 / Catch the exception and callback the exception handler
     *
     * @param context 下载上下文 / Context of download
     * @return 加载结果 / Result of loading
     */
    @Override
    public SourceLoadResult load(DownloadContext context) {
        try {
            source.load(context);
            return new SourceLoadResult(source);
        } catch (Throwable e) {
            SourceLoadResult result = new SourceLoadResult(source, e);
            handler.onLoading(result.getException());
            return result;
        }
    }
}