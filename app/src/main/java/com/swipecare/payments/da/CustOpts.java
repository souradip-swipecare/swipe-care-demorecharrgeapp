package com.swipecare.payments.da;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "CustOpts")
public class CustOpts {
    @ElementList(inline = true, name = "Param", required = false)
    public List<Param> params;
}
