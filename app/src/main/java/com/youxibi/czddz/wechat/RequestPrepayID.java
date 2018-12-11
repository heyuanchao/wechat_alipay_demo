package com.youxibi.czddz.wechat;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.youxibi.czddz.MainActivity;
import com.youxibi.czddz.R;
import com.youxibi.czddz.Util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestPrepayID extends AsyncTask<String, Void, String> {
    private static final String TAG = "RequestPrepayID";
    private Activity activity;
    private String key;

    public RequestPrepayID(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        key = params[1];
        try {
            return Util.httpPost(params[0], params[2]);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            Toast.makeText(activity, R.string.connection_error, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, result);
        Map<String, String> map = parseXML(result);
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                Log.i(TAG, entry.getKey() + ": " + entry.getValue());
//            }
        String return_code = map.get("return_code");
        String return_msg = map.get("return_msg");
        if ("FAIL".equals(return_code)) {
            Toast.makeText(activity, return_msg, Toast.LENGTH_SHORT).show();
            return;
        }
        String result_code = map.get("result_code");
        String err_code = map.get("err_code");
        String err_code_des = map.get("err_code_des");
        if ("FAIL".equals(result_code)) {
            Toast.makeText(activity, err_code + ": " + err_code_des, Toast.LENGTH_SHORT).show();
            return;
        }
        String appid = map.get("appid");
        String partnerid = map.get("mch_id");
        String prepayid = map.get("prepay_id");
        String noncestr = Util.getRandomString(1, 32);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        PayReq req = new PayReq();
        req.appId = appid;
        req.nonceStr = noncestr;
        req.packageValue = "Sign=WXPay";
        req.partnerId = partnerid;
        req.prepayId = prepayid;
        req.timeStamp = timestamp;

        String stringSignTemp = "appid=" + appid
                + "&noncestr=" + noncestr
                + "&package=" + "Sign=WXPay"
                + "&partnerid=" + partnerid
                + "&prepayid=" + prepayid
                + "&timestamp=" + timestamp
                + "&key=" + key;
        req.sign = Util.MD5(stringSignTemp).toUpperCase();
        ((MainActivity)activity).getWXAPI().sendReq(req);
    }

    private Map<String, String> parseXML(String xml) {
        Map<String, String> map = new HashMap<>();

        ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(stream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:// 开始元素事件
                        if ("return_code".equals(nodeName) || "result_code".equals(nodeName)
                                || "err_code".equals(nodeName) || "err_code_des".equals(nodeName)
                                || "return_msg".equals(nodeName) || "appid".equals(nodeName)
                                || "mch_id".equals(nodeName) || "prepay_id".equals(nodeName)) {
                            map.put(nodeName, parser.nextText());
                        }

                        break;
                    case XmlPullParser.END_TAG:// 结束元素事件
                        break;
                }
                eventType = parser.next();
            }
            stream.close();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
