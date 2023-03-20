package com.swipecare.payments.da;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "additional_info")
public class additional_info {
    @ElementList(inline = true, name = "Param", required = false)
    public List<Param> params;
}
