package com.swipecare.payments.da;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

public class Skey {
    @Attribute(name = "ci", required = false)
    public String ci;
    @Text(required = true)
    public String value;
}
