package com.youxibi.czddz.wechat;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.youxibi.czddz.R;
import com.youxibi.czddz.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by heyuanchao on 2017/11/21.
 */

public class RequestWXPay extends AsyncTask<String, Void, String> {
    private static final String TAG = "RequestPayParameter";
    private Activity activity;

    public RequestWXPay(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return Util.httpGet(params[0]);
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
        try {
            JSONObject jsonObject = new JSONObject(result);

            String appid = jsonObject.optString("appid");
            String body = jsonObject.optString("body");
            String key = jsonObject.optString("key");
            String mch_id = jsonObject.optString("mch_id");
            String nonce_str = Util.getRandomString(1, 32);
//            String nonce_str = "5K8264ILTKCH16CQ2502SI8ZNMTM67VS";
            String notify_url = jsonObject.optString("notify_url");
            String total_fee = jsonObject.optString("total_fee");
            String out_trade_no = jsonObject.optString("out_trade_no");
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
//            String out_trade_no = formatter.format(new Date()) + Util.getRandomString(0, 6);
//            String out_trade_no = "20150806125346";

            String spbill_create_ip = jsonObject.optString("spbill_create_ip");

            String stringSignTemp = "appid=" + appid
                    + "&body=" + body
                    + "&mch_id=" + mch_id
                    + "&nonce_str=" + nonce_str
                    + "&notify_url=" + notify_url
                    + "&out_trade_no=" + out_trade_no
                    + "&spbill_create_ip=" + spbill_create_ip
                    + "&total_fee=" + total_fee
                    + "&trade_type=APP"
                    + "&key=" + key;
            String sign = Util.MD5(stringSignTemp).toUpperCase();

            String params = "<xml>" + "<appid>" + appid + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + mch_id + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + notify_url + "</notify_url>"
                    + "<out_trade_no>" + out_trade_no + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + total_fee + "</total_fee>"
                    + "<trade_type>APP</trade_type>"
                    + "<sign>" + sign + "</sign>"
                    + "</xml>";
            Log.i(TAG, params);
            new RequestPrepayID(activity).execute("https://api.mch.weixin.qq.com/pay/unifiedorder", key, params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
