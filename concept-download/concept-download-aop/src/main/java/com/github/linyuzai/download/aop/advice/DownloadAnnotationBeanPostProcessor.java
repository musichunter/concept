package com.github.linyuzai.download.aop.advice;

import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;

@Deprecated
public class DownloadAnnotationBeanPostProcessor extends AbstractAdvisingBeanPostProcessor {

    public DownloadAnnotationBeanPostProcessor(DownloadConceptAdvisor advisor) {
        this.advisor = advisor;
    }
}
