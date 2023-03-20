package com.swipecare.payments.da;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "DeviceInfo")
public class DeviceInfo {
    @Element(name = "additional_info", required = false)
    public additional_info add_info;
    @Attribute(name = "dc")
    public String dc;
    @Attribute(name = "dpId")
    public String dpId;
    @Attribute(name = "mc")
    public String mc;
    @Attribute(name = "mi")
    public String mi;
    @Attribute(name = "rdsId")
    public String rdsId;
    @Attribute(name = "rdsVer")
    public String rdsVer;
}
