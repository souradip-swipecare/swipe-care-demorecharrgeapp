package com.swipecare.payments.da;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "Param")
public class Param {
    @Attribute(name = "name", required = false)
    public String name;
    @Attribute(name = "value", required = false)
    public String value;
}
