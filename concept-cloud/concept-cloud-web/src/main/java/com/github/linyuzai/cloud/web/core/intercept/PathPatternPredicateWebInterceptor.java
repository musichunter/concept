package com.github.linyuzai.cloud.web.core.intercept;

import com.github.linyuzai.cloud.web.core.context.WebContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Collection;

public class PathPatternPredicateWebInterceptor extends PredicateWebInterceptor {

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Getter
    @Setter
    private Collection<String> patterns;

    public PathPatternPredicateWebInterceptor(String... paths) {
        this(Arrays.asList(paths));
    }

    public PathPatternPredicateWebInterceptor(Collection<String> paths) {
        setPatterns(paths);
        setPredicate(this::match);
    }

    @Override
    public PathPatternPredicateWebInterceptor negate() {
        super.negate();
        return this;
    }

    protected boolean match(WebContext context) {
        String path = context.get(WebContext.Request.PATH);
        for (String pattern : patterns) {
            if (matcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}
