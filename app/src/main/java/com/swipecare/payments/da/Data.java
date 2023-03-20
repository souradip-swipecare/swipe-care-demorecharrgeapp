package com.swipecare.payments.da;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

public class Data {
    @Attribute(name = "type", required = false)
    public String type;
    @Text(required = true)
    public String value;
}
