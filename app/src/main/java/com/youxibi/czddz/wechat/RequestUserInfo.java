package com.youxibi.czddz.wechat;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.youxibi.czddz.R;
import com.youxibi.czddz.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RequestUserInfo extends AsyncTask<String, Void, String> {
    private Activity activity;

    public RequestUserInfo(Activity activity) {
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
        try {
            JSONObject jsonObject = new JSONObject(result);

            String openid = jsonObject.optString("openid");
            String nickname = jsonObject.optString("nickname");
            int sex = jsonObject.optInt("sex");
            String language = jsonObject.optString("language");
            String city = jsonObject.optString("city");
            String province = jsonObject.optString("province");
            String country = jsonObject.optString("country");
            String headimgurl = jsonObject.optString("headimgurl");
            String unionid = jsonObject.optString("unionid");

            Toast.makeText(activity, "你好，" + nickname, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
