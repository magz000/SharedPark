package com.sharepark.util;

public class WebServiceHelper {

    public static String web_tip = "http://web.tip.edu.ph/pickapark/parker/";
    public static String web_pap = "http://www.pickapark.com.ph/";
    public static String web_tip_root = "http://web.tip.edu.ph/pickapark/";
    public static String local_tip = "http://192.168.100.16/pickapark/parker/";

    public static String getWebService(Boolean istip) {
        String link ="";
        if(istip){
            link = "http://web.tip.edu.ph/pickapark/parker/";
        }
        else{
            link = "http://pickapark.tip.edu.ph/pap/parker/";
        }

        return link;
    }

    public static String getWebService_root(Boolean istip) {
        String link ="";
        if(istip){
            link = "http://web.tip.edu.ph/pickapark/";
        }
        else{
            link = "http://pickapark.tip.edu.ph/pap/";
        }
        return link;
    }


}
