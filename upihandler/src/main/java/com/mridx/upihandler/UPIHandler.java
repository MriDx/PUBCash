package com.mridx.upihandler;

import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UPIHandler {

    private static final String SHARED_PREF_NAME = "PUBCash";
    private static final String TRANS_ID = "transid";

    public String buildURI(HashMap<String, String> params) {


        String amount = params.get("amount");

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmm");
        Date myDate = new Date();
        String id = date.format(myDate);
        String transid = id + params.get("username");
        Log.d("transid", transid);

        Uri uri = new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", "mridulbaishya28@oksbi")//upi
                        //.appendQueryParameter("pa", "mridx@ybl")
                        .appendQueryParameter("pn", "PUBCash(Mrdidul Baishya)")// merchant name
                        .appendQueryParameter("mc", "1234")//merchant code
                        .appendQueryParameter("tr", transid)// transaction ref id
                        .appendQueryParameter("tn", "Pay for PUBCash Match")// transaction note
                        .appendQueryParameter("am", amount)//amount
                        .appendQueryParameter("cu", "INR")//currency
                        .appendQueryParameter("url", "http://noobdevs.tk/")//transaction url
                        .build();
        return String.valueOf(uri);
    }
}
