package com.swipecare.payments.da;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "PidData")
public class PidData {
    @Element(name = "Data", required = false)
    public Data _Data;
    @Element(name = "DeviceInfo", required = false)
    public DeviceInfo _DeviceInfo;
    @Element(name = "Hmac", required = false)
    public String _Hmac;
    @Element(name = "Resp", required = false)
    public Resp _Resp;
    @Element(name = "Skey", required = false)
    public Skey _Skey;
}
