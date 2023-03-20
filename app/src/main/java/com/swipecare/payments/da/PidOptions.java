package com.swipecare.payments.da;

import android.text.TextUtils;
import android.util.Log;

import org.apache.xml.security.utils.Constants;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.io.Writer;

@Root(name = "PidOptions", strict = false)
public class PidOptions {
    @Element(name = "CustOpts", required = false)
    public CustOpts CustOpts;
    @Element(name = "Opts", required = false)
    public Opts Opts;
    @Attribute(name = "ver", required = false)
    public String ver;

    public static String getPIDOptions(String str) {
        try {
            Opts opts = new Opts();
            opts.fCount = "1";
            opts.fType = "2";
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = "0";
            opts.pidVer = "2.0";
            opts.timeout = "10000";
/*
            if (TextUtils.isEmpty(str)) {
                opts.format = "1";
            } else {
                opts.wadh = str;
                opts.format = "0";
            }
*/
            opts.env = Constants._TAG_P;
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "1.0";
            pidOptions.Opts = opts;
            Persister persister = new Persister();
            Writer stringWriter = new StringWriter();
            persister.write(pidOptions, stringWriter);
            return stringWriter.toString();
        } catch (Exception str3) {
            Log.e("Error", str3.toString());
            return null;
        }
    }

//    public  static String getPIDOptions() {
//
//        try {
//
//            return "<?xml version=\"1.0\"?><PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" wadh=\"\" posh=\"UNKNOWN\" env=\"P\" /><CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts></PidOptions>";
//        } catch (Exception e) {
//            return "";
//        }
//    }
}
