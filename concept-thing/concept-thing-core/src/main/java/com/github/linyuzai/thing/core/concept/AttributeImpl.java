package com.github.linyuzai.thing.core.concept;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttributeImpl extends AbstractAttribute {

    private String id;

    private String key;

    private Label label;

    private Thing thing;

    private Object value;
}
