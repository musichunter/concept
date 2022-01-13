package com.github.linyuzai.download.core.concept;

import com.github.linyuzai.download.core.cache.Cacheable;
import com.github.linyuzai.download.core.compress.Compression;
import com.github.linyuzai.download.core.source.Source;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 可下载的对象 / An object can download
 *
 * @see Source
 * @see Compression
 */
public interface Downloadable extends Part, Cacheable {

    /**
     * @return 名称 / name
     */
    String getName();

    /**
     * @return Content Type
     */
    String getContentType();

    /**
     * @return 编码 / charset
     */
    Charset getCharset();

    /**
     * @return 字节数 / bytes count
     */
    Long getLength();

    /**
     * @return 所有的部分，如文件夹下的多个文件 / All parts, such as multiple files under a folder
     */
    default Collection<Part> getParts() {
        Collection<Part> parts = new ArrayList<>();
        addPart(this, parts);
        return parts;
    }

    static void addPart(Part part, Collection<Part> parts) {
        parts.add(part);
        for (Part child : part.getChildren()) {
            addPart(child, parts);
        }
    }
}
